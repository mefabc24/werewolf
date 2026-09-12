package com.mefabc24.werewolf.game

import com.mefabc24.werewolf.game.actions.*
import com.mefabc24.werewolf.game.managers.*
import com.mefabc24.werewolf.game.results.NightResult
import com.mefabc24.werewolf.game.role.SeerState
import com.mefabc24.werewolf.game.role.WitchState
import com.mefabc24.werewolf.network.*
import com.mefabc24.werewolf.player.Player
import com.mefabc24.werewolf.player.PlayerInfo
import com.mefabc24.werewolf.player.role.*
import com.mefabc24.werewolf.settings.GameSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class GameController(
    private val gameState: GameState
) {
    private val _events = MutableSharedFlow<GameEvent>()
    val events = _events.asSharedFlow()

    private val scope = CoroutineScope(Dispatchers.Default)
    private var gameJob: Job? = null

    private var currentNightResult: NightResult? = null

    private val votingManager = VotingManager(gameState)
    private val phaseManager = PhaseManager(gameState, votingManager)
    private val roleManager = RoleManager(gameState)

    suspend fun start(
        players: List<Player>,
        settings: GameSettings
    ) {
        resetState()

        gameState.players.addAll(
            players.map { it.copy(role = null, isAlive = true) }
        )

        gameState.settings = settings
        gameState.gamePhase = GamePhase.START

        roleManager.assignRoles()

        gameState.players.forEach { player ->
            when (player.role) {
                is Witch -> gameState.witchStates[player.id] = WitchState()
                is Seer -> gameState.seerStates[player.id] = SeerState()

                is Hunter -> {}
                is Werewolf -> {}
                is Villager -> {}
                is Mayor -> {}
                null -> error("Player ${player.id} has no role")
            }
        }

        for ((id) in gameState.players) {
            notifyClients(
                GameStartedEvent(generateClientGameState(id)),
                setOf(id)
            )
        }

        gameJob = scope.launch { gameLoop() }
    }

    private suspend fun end() {
        gameState.gamePhase = GamePhase.END

        notifyClients(GameEndedEvent)

        gameJob?.cancel()
        gameJob = null
    }

    private suspend fun gameLoop() {
        while (checkWinCondition() == null) {
            notifyClients(RoundStartedEvent(gameState.round))

            night()
            if (checkAndHandleWin()) break

            day()
            if (checkAndHandleWin()) break

            gameState.round++
        }
    }

    private suspend fun night() {
        notifyClients(NightStartedEvent)

        val nightResult = NightResult()
        currentNightResult = nightResult

        phaseManager.night(
            result = nightResult,
            onPhaseStarted = { phase ->
                notifyClients(
                    NightPhaseStartedEvent(phase)
                )
            },
            onPlayerTurn = { phase, actingPlayers, currentResult ->
                when (phase) {
                    NightPhase.WEREWOLVES -> {
                        notifyClients(
                            WerewolfTurnEvent,
                            actingPlayers
                        )
                    }

                    NightPhase.WITCH -> {
                        notifyClients(
                            WitchTurnEvent(
                                currentResult.killedPlayerIds.toSet()
                            ),
                            actingPlayers
                        )
                    }

                    NightPhase.SEER -> {
                        notifyClients(
                            SeerTurnEvent, actingPlayers
                        )
                    }
                }
            },
            onSeerReveal = { seerId, targetId ->
                val target = requirePlayer(targetId)

                notifyClients(
                    RoleRevealEvent(
                        targetId,
                        target.role!!
                    ), setOf(seerId)
                )
            }
        )

        currentNightResult = null

        nightResult.killedPlayerIds.forEach { id ->
            handlePlayerDeath(id, DeathCause.NIGHT)
        }

        notifyClients(NightEndedEvent(nightResult.killedPlayerIds))
    }

    private suspend fun day() {
        notifyClients(DayStartedEvent)
        phaseManager.day(
            onPhaseStarted = { phase ->
                notifyClients(DayPhaseStartedEvent(phase))
            },
            onVotingResolved = { votedPlayerId ->
                votedPlayerId?.let {id ->
                    handlePlayerDeath(id, DeathCause.VOTED_OUT)
                }

                notifyClients(VotingFinishedEvent(votedPlayerId))
            }
        )
    }

    private suspend fun checkAndHandleWin(): Boolean {
        checkWinCondition()?.let { team ->
            notifyClients(GameWonEvent(team))
            end()
            return true
        }
        return false
    }

    private suspend fun handlePlayerDeath(
        playerId: Int,
        cause: DeathCause
    ) {
        val player = requirePlayer(playerId)

        killPlayer(player.id)

        notifyClients(PlayerDiedEvent(playerId, cause))
        notifyClients(RoleRevealEvent(playerId, player.role!!))

        if (player.role == Hunter) {
            handleHunterDeath(playerId)
        }
    }

    private fun killPlayer(playerId: Int) {
        requirePlayer(playerId).isAlive = false
    }

    private suspend fun handleHunterDeath(playerId: Int) {
        val action = phaseManager.awaitPlayerAction(playerId) { playerIds ->
            notifyClients(HunterTurnEvent, playerIds)
        } as? HunterAction ?: return

        action.targetId?.let { targetId ->
            handlePlayerDeath(
                targetId,
                DeathCause.HUNTER
            )
        }
    }

    private fun checkWinCondition(): Team? {
        val aliveWerewolves = gameState.players.count {
            it.isAlive && it.role?.team == Team.WEREWOLVES
        }

        val aliveVillagers = gameState.players.count {
            it.isAlive && it.role?.team == Team.VILLAGE
        }

        if (aliveWerewolves == 0) return Team.VILLAGE
        if (aliveWerewolves >= aliveVillagers) return Team.WEREWOLVES
        return null
    }

    fun submitAction(
        playerId: Int,
        action: Action
    ): Boolean {
        val player = getPlayer(playerId) ?: return false

        val validAction = when (action) {
            is WerewolfAction ->
                gameState.nightPhase == NightPhase.WEREWOLVES &&
                        player.role == Werewolf &&
                        isValidWerewolfTarget(player, action.targetId)

            is WitchAction -> {
                val usingBoth = action.killTargetId != null && action.healTargetId != null

                gameState.nightPhase == NightPhase.WITCH &&
                        player.role == Witch &&
                        (!usingBoth || gameState.settings.canWitchUseBothPotions) &&
                        isValidWitchHealTarget(player, action.healTargetId) &&
                        isValidWitchKillTarget(player, action.killTargetId)
            }

            is SeerAction -> {
                gameState.nightPhase == NightPhase.SEER &&
                        player.role == Seer &&
                        isValidSeerTarget(player, action.targetId)
            }

            is HunterAction -> {
                !player.isAlive &&
                        player.role == Hunter &&
                        isValidHunterTarget(player, action.targetId)
            }

        }

        return validAction && phaseManager.completePendingAction(playerId, action)
    }

    private fun isValidWerewolfTarget(
        player: Player,
        targetId: Int?
    ): Boolean {
        if (targetId == null) return true

        val target = getPlayer(targetId) ?: return false

        return target.id != player.id &&
                target.isAlive &&
                target.role?.team == Team.VILLAGE
    }

    private fun isValidWitchHealTarget(player: Player, targetId: Int?): Boolean {
        if (targetId == null) return true

        val witchState = getWitchState(player.id) ?: return false

        if (!witchState.hasHealPotion) return false

        val target = getPlayer(targetId) ?: return false

        return !(target.id == player.id && !gameState.settings.canWitchHealSelf) &&
                targetId in currentNightResult?.killedPlayerIds.orEmpty()
    }

    private fun isValidWitchKillTarget(player: Player, targetId: Int?): Boolean {
        if (targetId == null) return true

        val witchState = getWitchState(player.id) ?: return false

        if (!witchState.hasKillPotion) return false

        val target = getPlayer(targetId) ?: return false
        val isWitch = target.role == Witch
        val canKillWitch = gameState.settings.canWitchKillWitch

        return !(isWitch && !canKillWitch) &&
                target.id != player.id &&
                target.isAlive
    }

    private fun isValidSeerTarget(player: Player, targetId: Int?): Boolean {
        if (targetId == null) return true

        val target = getPlayer(targetId) ?: return false
        val seerState = gameState.seerStates[player.id] ?: return false

        return target.id != player.id &&
                target.isAlive &&
                target.id !in seerState.seenPlayerIds
    }

    private fun isValidHunterTarget(player: Player, targetId: Int?): Boolean {
        if (targetId == null) return true

        val target = getPlayer(targetId) ?: return false

        return target.id != player.id &&
                target.isAlive
    }

    private fun getPlayer(id: Int): Player? =
        gameState.players.find { it.id == id }

    private fun requirePlayer(id: Int): Player =
        getPlayer(id) ?: error("Player with id $id not found.")

    private fun getWitchState(playerId: Int): WitchState? =
        gameState.witchStates[playerId]

    suspend fun submitVote(
        voterId: Int,
        targetId: Int
    ): Boolean {
        if (
            gameState.gamePhase != GamePhase.DAY ||
            gameState.dayPhase != DayPhase.VOTING
        ) return false

        val voter = getPlayer(voterId) ?: return false
        val target = getPlayer(targetId) ?: return false

        if (
            voter.id == target.id ||
            !voter.isAlive ||
            !target.isAlive
        ) return false

        val success = votingManager.submitVote(voterId, targetId)

        if (success) notifyClients(PlayerVotedEvent(voterId))

        return success
    }

    private fun generateClientGameState(playerId: Int): ClientGameState {
        val player = requirePlayer(playerId)

        val werewolfIds =
            if (player.role?.team == Team.WEREWOLVES) {
                gameState.players
                    .filter { it.role?.team == Team.WEREWOLVES }
                    .map { it.id }
            } else null

        return ClientGameState(
            players = gameState.players.map { p ->
                PlayerInfo(
                    id = p.id,
                    playerName = p.name,
                    isAlive = p.isAlive,
                    role = when {
                        p.id == playerId -> p.role
                        !p.isAlive -> p.role
                        else -> null
                    }
                )
            },
            gamePhase = gameState.gamePhase,
            nightPhase = gameState.nightPhase,
            dayPhase = gameState.dayPhase,
            round = gameState.round,
            hasVoted = gameState.votes.map { it.voterId }.toMutableSet(),
            werewolfIds = werewolfIds,
            settings = gameState.settings
        )
    }

    private fun resetState() {
        gameState.players.clear()
        gameState.nightPhase = null
        gameState.dayPhase = null
        gameState.round = 1
        gameState.votes.clear()
        currentNightResult = null

        gameState.witchStates.clear()
        gameState.seerStates.clear()
    }

    private suspend fun notifyClients(
        event: Event,
        recipients: Set<Int> = emptySet()
    ) {
        _events.emit(GameEvent(event, recipients))
    }
}
