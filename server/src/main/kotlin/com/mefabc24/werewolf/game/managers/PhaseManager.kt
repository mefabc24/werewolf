package com.mefabc24.werewolf.game.managers

import com.mefabc24.werewolf.game.DayPhase
import com.mefabc24.werewolf.game.GamePhase
import com.mefabc24.werewolf.game.GameState
import com.mefabc24.werewolf.game.NightPhase
import com.mefabc24.werewolf.game.actions.Action
import com.mefabc24.werewolf.game.actions.SeerAction
import com.mefabc24.werewolf.game.actions.WerewolfAction
import com.mefabc24.werewolf.game.actions.WitchAction
import com.mefabc24.werewolf.game.results.NightResult
import com.mefabc24.werewolf.game.role.WitchState
import com.mefabc24.werewolf.player.role.NightActionMode
import com.mefabc24.werewolf.player.role.NightRole
import com.mefabc24.werewolf.player.role.OptionalRole
import com.mefabc24.werewolf.player.role.Seer
import com.mefabc24.werewolf.player.role.Werewolf
import com.mefabc24.werewolf.player.role.Witch
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class PhaseManager(
    private val gameState: GameState,
    private val votingManager: VotingManager
) {
    private val pendingActions = ConcurrentHashMap<Int, CompletableDeferred<Action>>()

    suspend fun night(
        result: NightResult,
        onPhaseStarted: suspend (NightPhase) -> Unit,
        onPlayerTurn: suspend (NightPhase, Set<Int>, NightResult) -> Unit,
        onSeerReveal: suspend (Int, Int) -> Unit
    ) {
        gameState.dayPhase = null
        gameState.gamePhase = GamePhase.NIGHT

        for (phase in getActiveNightPhases()) {
            val nightRole: NightRole = when (phase) {
                NightPhase.WEREWOLVES -> Werewolf
                NightPhase.WITCH -> Witch
                NightPhase.SEER -> Seer
            }

            val actingPlayers = getActingPlayers(nightRole)

            if (actingPlayers.isEmpty()) continue

            gameState.nightPhase = phase

            onPhaseStarted(phase)

            val timeout = gameState.settings.nightRoleActingTimeSeconds
                .takeIf { it >= 0 }
                ?.seconds

            when (nightRole.nightActionMode) {
                NightActionMode.SIMULTANEOUS -> {
                    val actions = awaitActions(actingPlayers, timeout) { playerIds ->
                        onPlayerTurn(phase, playerIds, result)
                    }

                    processActions(
                        phase,
                        actions,
                        result,
                        onSeerReveal
                    )
                }

                NightActionMode.SEQUENTIAL -> {
                    for (playerId in actingPlayers) {
                        val action = awaitAction(playerId, timeout) { playerIds ->
                            onPlayerTurn(phase, playerIds, result)
                        }

                        if (action != null) {
                            processActions(
                                phase,
                                mapOf(playerId to action),
                                result,
                                onSeerReveal
                            )
                        }
                    }
                }
            }
        }

        gameState.nightPhase = null
    }

    suspend fun day(
        onPhaseStarted: suspend (DayPhase) -> Unit,
        onVotingResolved: suspend (Int?) -> Unit
    ) {
        gameState.nightPhase = null
        gameState.gamePhase = GamePhase.DAY

        var votedPlayerId: Int? = null

        for (phase in DayPhase.entries) {
            gameState.dayPhase = phase

            onPhaseStarted(phase)

            when (phase) {
                DayPhase.DISCUSSION -> {
                    delay(gameState.settings.discussionTimeSeconds.seconds)
                }

                DayPhase.VOTING -> {
                    gameState.votes.clear()
                    votingManager.startVoting()

                    val timeout = gameState.settings.voteTimeSeconds
                        .takeIf { it >= 0 }
                        ?.seconds

                    votedPlayerId =
                        if (timeout == null) votingManager.awaitResult()
                        else votingManager.awaitResult(timeout)
                }

                DayPhase.RESULT -> {
                    onVotingResolved(votedPlayerId)
                    delay(5.seconds)
                }
            }
        }

        gameState.dayPhase = null
    }

    private suspend fun processActions(
        phase: NightPhase,
        actions: Map<Int, Action>,
        result: NightResult,
        onSeerReveal: suspend (Int, Int) -> Unit
    ) {
        when (phase) {
            NightPhase.WEREWOLVES -> {
                val votes = actions.values
                    .filterIsInstance<WerewolfAction>()
                    .mapNotNull { it.targetId }

                val voteCounts = votes.groupingBy { it }.eachCount()

                val highestVoteCount =
                    voteCounts.values.maxOrNull() ?: return

                val targets = voteCounts
                    .filterValues { it == highestVoteCount }
                    .keys

                if (targets.size == 1) {
                    result.killedPlayerIds.add(targets.first())
                }
            }

            NightPhase.WITCH -> {
                actions.forEach { (playerId, action) ->
                    if (action !is WitchAction) return@forEach

                    val witchState = requireWitchState(playerId)

                    action.healTargetId?.let {
                        result.killedPlayerIds.remove(it)
                        witchState.hasHealPotion = false
                    }

                    action.killTargetId?.let {
                        result.killedPlayerIds.add(it)
                        witchState.hasKillPotion = false
                    }
                }
            }

            NightPhase.SEER -> {
                actions.forEach { (seerId, action) ->
                    if (action !is SeerAction) return@forEach

                    action.targetId?.let { targetId ->
                        onSeerReveal(seerId, targetId)
                    }
                }
            }
        }
    }

    fun completePendingAction(
        playerId: Int,
        action: Action
    ): Boolean {
        val pendingAction = pendingActions[playerId]
            ?: return false

        return pendingAction.complete(action)
    }

    private suspend fun awaitAction(
        playerId: Int,
        timeout: Duration?,
        onPlayerTurn: suspend (Set<Int>) -> Unit
    ): Action? {
        val pendingAction = CompletableDeferred<Action>()

        pendingActions[playerId] = pendingAction

        onPlayerTurn(setOf(playerId))

        return try {
            if (timeout == null) {
                pendingAction.await()
            } else {
                withTimeoutOrNull(timeout) {
                    pendingAction.await()
                }
            }
        } finally {
            pendingActions.remove(playerId)
        }
    }

    suspend fun awaitPlayerAction(
        playerId: Int,
        onPlayerTurn: suspend (Set<Int>) -> Unit
    ): Action? {
        return awaitAction(
            playerId = playerId,
            timeout = null,
            onPlayerTurn = onPlayerTurn
        )
    }

    private suspend fun awaitActions(
        playerIds: Set<Int>,
        timeout: Duration?,
        onPlayerTurn: suspend (Set<Int>) -> Unit
    ): Map<Int, Action> {
        val actions = playerIds.associateWith {
            CompletableDeferred<Action>()
        }

        pendingActions.putAll(actions)

        onPlayerTurn(playerIds)

        return try {
            if (timeout == null) {
                actions.mapValues { (_, pendingAction) ->
                    pendingAction.await()
                }
            } else {
                withTimeoutOrNull(timeout) {
                    actions.values.forEach { it.await() }
                }

                actions
                    .filterValues { it.isCompleted }
                    .mapValues { (_, pendingAction) ->
                        pendingAction.await()
                    }
            }
        } finally {
            playerIds.forEach {
                pendingActions.remove(it)
            }
        }
    }

    private fun requireWitchState(playerId: Int): WitchState =
        gameState.witchStates[playerId]
            ?: error("WitchState for player $playerId not found.")

    private fun getActiveNightPhases(): List<NightPhase> {
        return NightPhase.entries.filter { phase ->
            when (phase) {
                NightPhase.WEREWOLVES -> gameState.settings.werewolfAmount > 0
                NightPhase.WITCH -> (gameState.settings.optionalRoles[OptionalRole.WITCH] ?: 0) > 0
                NightPhase.SEER -> (gameState.settings.optionalRoles[OptionalRole.SEER] ?: 0) > 0
            }
        }
    }

    private fun getActingPlayers(role: NightRole): Set<Int> =
         gameState.players
             .filter { it.role == role && it.isAlive }
             .map { it.id }
             .toSet()
}