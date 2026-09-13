package com.mefabc24.werewolf.game.managers

import com.mefabc24.werewolf.game.DayPhase
import com.mefabc24.werewolf.game.GamePhase
import com.mefabc24.werewolf.game.GameState
import com.mefabc24.werewolf.game.NightPhase
import com.mefabc24.werewolf.game.Vote
import com.mefabc24.werewolf.game.actions.Action
import com.mefabc24.werewolf.game.actions.SeerAction
import com.mefabc24.werewolf.game.actions.WerewolfAction
import com.mefabc24.werewolf.game.actions.WitchAction
import com.mefabc24.werewolf.game.results.NightResult
import com.mefabc24.werewolf.game.role.states.WitchState
import com.mefabc24.werewolf.player.Player
import com.mefabc24.werewolf.player.role.OptionalRole
import com.mefabc24.werewolf.player.role.Seer
import com.mefabc24.werewolf.player.role.Villager
import com.mefabc24.werewolf.player.role.Werewolf
import com.mefabc24.werewolf.player.role.Witch
import com.mefabc24.werewolf.settings.GameSettings
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PhaseManagerTest {

    @Test
    fun nightRunsActiveRolesInOrderAndPassesResultsForward() = runTest {
        val state = GameState(
            players = mutableListOf(
                Player(1, "Wolf", Werewolf),
                Player(2, "Witch", Witch),
                Player(3, "Seer", Seer),
                Player(4, "Villager", Villager)
            ),
            settings = GameSettings(
                optionalRoles = mapOf(
                    OptionalRole.WITCH to 1,
                    OptionalRole.SEER to 1
                )
            ),
            witchStates = mutableMapOf(2 to WitchState())
        )

        val execution = executeNight(
            state,
            actions = mapOf(
                1 to WerewolfAction(targetId = 4),
                2 to WitchAction(),
                3 to SeerAction(targetId = 1)
            )
        )

        assertEquals(NightPhase.entries.toList(), execution.startedPhases)
        assertEquals(
            listOf(
                NightPhase.WEREWOLVES to setOf(1),
                NightPhase.WITCH to setOf(2),
                NightPhase.SEER to setOf(3)
            ),
            execution.turns
        )
        assertEquals(setOf(4), execution.result.killedPlayerIds)
        assertEquals(setOf(4), execution.killedPlayersSeenByWitch)
        assertEquals(listOf(3 to 1), execution.seerReveals)
        assertEquals(GamePhase.NIGHT, state.gamePhase)
        assertNull(state.nightPhase)
        assertNull(state.dayPhase)
    }

    @Test
    fun tiedWerewolfTargetsLeaveEveryoneAlive() = runTest {
        val state = GameState(
            players = mutableListOf(
                Player(1, "Wolf 1", Werewolf),
                Player(2, "Wolf 2", Werewolf),
                Player(3, "Villager 1", Villager),
                Player(4, "Villager 2", Villager)
            ),
            settings = GameSettings(werewolfAmount = 2, optionalRoles = emptyMap())
        )

        val execution = executeNight(
            state,
            actions = mapOf(
                1 to WerewolfAction(targetId = 3),
                2 to WerewolfAction(targetId = 4)
            )
        )

        assertTrue(execution.result.killedPlayerIds.isEmpty())
    }

    @Test
    fun matchingWerewolfTargetsKillOnlyTheirConsensusTarget() = runTest {
        val state = GameState(
            players = mutableListOf(
                Player(1, "Wolf 1", Werewolf),
                Player(2, "Wolf 2", Werewolf),
                Player(3, "Villager 1", Villager),
                Player(4, "Villager 2", Villager)
            ),
            settings = GameSettings(werewolfAmount = 2, optionalRoles = emptyMap())
        )

        val execution = executeNight(
            state,
            actions = mapOf(
                1 to WerewolfAction(targetId = 3),
                2 to WerewolfAction(targetId = 3)
            )
        )

        assertEquals(setOf(3), execution.result.killedPlayerIds)
    }

    @Test
    fun witchCanHealTheWerewolfVictimAndConsumesOnlyHealPotion() = runTest {
        val witchState = WitchState()
        val state = stateWithWolfWitchAndVillagers(witchState)

        val execution = executeNight(
            state,
            actions = mapOf(
                1 to WerewolfAction(targetId = 3),
                2 to WitchAction(healTargetId = 3)
            )
        )

        assertTrue(execution.result.killedPlayerIds.isEmpty())
        assertFalse(witchState.hasHealPotion)
        assertTrue(witchState.hasKillPotion)
    }

    @Test
    fun witchCanPoisonAnotherPlayerAndConsumesOnlyKillPotion() = runTest {
        val witchState = WitchState()
        val state = stateWithWolfWitchAndVillagers(witchState)

        val execution = executeNight(
            state,
            actions = mapOf(
                1 to WerewolfAction(targetId = null),
                2 to WitchAction(killTargetId = 4)
            )
        )

        assertEquals(setOf(4), execution.result.killedPlayerIds)
        assertTrue(witchState.hasHealPotion)
        assertFalse(witchState.hasKillPotion)
    }

    @Test
    fun witchUsingBothPotionsReplacesTheWerewolfVictimWithPoisonTarget() = runTest {
        val witchState = WitchState()
        val state = stateWithWolfWitchAndVillagers(witchState)

        val execution = executeNight(
            state,
            actions = mapOf(
                1 to WerewolfAction(targetId = 3),
                2 to WitchAction(healTargetId = 3, killTargetId = 4)
            )
        )

        assertEquals(setOf(4), execution.result.killedPlayerIds)
        assertFalse(witchState.hasHealPotion)
        assertFalse(witchState.hasKillPotion)
    }

    @Test
    fun multipleWitchesActSequentiallyInSeparateTurns() = runTest {
        val state = GameState(
            players = mutableListOf(
                Player(1, "Wolf", Werewolf),
                Player(2, "Witch 1", Witch),
                Player(3, "Witch 2", Witch),
                Player(4, "Villager", Villager)
            ),
            settings = GameSettings(
                optionalRoles = mapOf(OptionalRole.WITCH to 2)
            ),
            witchStates = mutableMapOf(2 to WitchState(), 3 to WitchState())
        )

        val execution = executeNight(
            state,
            actions = mapOf(
                1 to WerewolfAction(targetId = null),
                2 to WitchAction(),
                3 to WitchAction()
            )
        )

        assertEquals(
            listOf(
                NightPhase.WEREWOLVES to setOf(1),
                NightPhase.WITCH to setOf(2),
                NightPhase.WITCH to setOf(3)
            ),
            execution.turns
        )
    }

    @Test
    fun deadNightRolesAreSkippedEvenWhenConfigured() = runTest {
        val state = GameState(
            players = mutableListOf(
                Player(1, "Wolf", Werewolf),
                Player(2, "Witch", Witch, isAlive = false),
                Player(3, "Seer", Seer, isAlive = false),
                Player(4, "Villager", Villager)
            ),
            settings = GameSettings()
        )

        val execution = executeNight(
            state,
            actions = mapOf(1 to WerewolfAction(targetId = null))
        )

        assertEquals(listOf(NightPhase.WEREWOLVES), execution.startedPhases)
        assertEquals(listOf(NightPhase.WEREWOLVES to setOf(1)), execution.turns)
    }

    @Test
    fun nightTimeoutContinuesWithoutAnActionAndCleansPendingTurn() = runTest {
        val state = GameState(
            players = mutableListOf(
                Player(1, "Wolf", Werewolf),
                Player(2, "Villager", Villager)
            ),
            settings = GameSettings(
                optionalRoles = emptyMap(),
                nightRoleActingTimeSeconds = 0
            )
        )
        val manager = PhaseManager(state, VotingManager(state))
        val result = NightResult()

        manager.night(
            result = result,
            onPhaseStarted = {},
            onPlayerTurn = { _, _, _ -> },
            onSeerReveal = { _, _ -> }
        )

        assertTrue(result.killedPlayerIds.isEmpty())
        assertNull(state.nightPhase)
        assertFalse(manager.completePendingAction(1, WerewolfAction(2)))
    }

    @Test
    fun dayRunsDiscussionVotingAndResultThenCleansTheSubphase() = runTest {
        val state = GameState(
            players = players(3),
            nightPhase = NightPhase.SEER,
            votes = mutableSetOf(Vote(voterId = 99, targetId = 1)),
            settings = GameSettings(
                optionalRoles = emptyMap(),
                discussionTimeSeconds = 2,
                voteTimeSeconds = -1
            )
        )
        val votingManager = VotingManager(state)
        val manager = PhaseManager(state, votingManager)
        val phases = mutableListOf<DayPhase>()
        var resolvedPlayerId: Int? = null

        manager.day(
            onPhaseStarted = { phase ->
                phases += phase
                if (phase == DayPhase.VOTING) {
                    launch {
                        votingManager.submitVote(1, 2)
                        votingManager.submitVote(2, 1)
                        votingManager.submitVote(3, 2)
                    }
                }
            },
            onVotingResolved = { resolvedPlayerId = it }
        )

        assertEquals(DayPhase.entries.toList(), phases)
        assertEquals(2, resolvedPlayerId)
        assertEquals(setOf(1, 2, 3), state.votes.map { it.voterId }.toSet())
        assertEquals(GamePhase.DAY, state.gamePhase)
        assertNull(state.dayPhase)
        assertNull(state.nightPhase)
    }

    @Test
    fun timedDayVotingResolvesUsingPartialVotes() = runTest {
        val state = GameState(
            players = players(4),
            settings = GameSettings(
                optionalRoles = emptyMap(),
                voteTimeSeconds = 1
            )
        )
        val votingManager = VotingManager(state)
        val manager = PhaseManager(state, votingManager)
        var resolvedPlayerId: Int? = null

        manager.day(
            onPhaseStarted = { phase ->
                if (phase == DayPhase.VOTING) {
                    launch {
                        votingManager.submitVote(1, 2)
                        votingManager.submitVote(3, 2)
                    }
                }
            },
            onVotingResolved = { resolvedPlayerId = it }
        )

        assertEquals(2, resolvedPlayerId)
        assertNull(state.dayPhase)
    }

    private suspend fun executeNight(
        state: GameState,
        actions: Map<Int, Action>
    ): NightExecution {
        val manager = PhaseManager(state, VotingManager(state))
        val result = NightResult()
        val phases = mutableListOf<NightPhase>()
        val turns = mutableListOf<Pair<NightPhase, Set<Int>>>()
        val reveals = mutableListOf<Pair<Int, Int>>()
        var playersSeenByWitch: Set<Int> = emptySet()

        manager.night(
            result = result,
            onPhaseStarted = { phases += it },
            onPlayerTurn = { phase, playerIds, currentResult ->
                turns += phase to playerIds
                if (phase == NightPhase.WITCH) {
                    playersSeenByWitch = currentResult.killedPlayerIds.toSet()
                }
                playerIds.forEach { playerId ->
                    val action = actions[playerId]
                    if (action != null) {
                        assertTrue(manager.completePendingAction(playerId, action))
                    }
                }
            },
            onSeerReveal = { seerId, targetId -> reveals += seerId to targetId }
        )

        return NightExecution(
            result = result,
            startedPhases = phases,
            turns = turns,
            killedPlayersSeenByWitch = playersSeenByWitch,
            seerReveals = reveals
        )
    }

    private fun stateWithWolfWitchAndVillagers(witchState: WitchState): GameState =
        GameState(
            players = mutableListOf(
                Player(1, "Wolf", Werewolf),
                Player(2, "Witch", Witch),
                Player(3, "Villager 1", Villager),
                Player(4, "Villager 2", Villager)
            ),
            settings = GameSettings(
                optionalRoles = mapOf(OptionalRole.WITCH to 1)
            ),
            witchStates = mutableMapOf(2 to witchState)
        )

    private fun players(count: Int): MutableList<Player> =
        (1..count).map { Player(it, "Player $it", Villager) }.toMutableList()

    private data class NightExecution(
        val result: NightResult,
        val startedPhases: List<NightPhase>,
        val turns: List<Pair<NightPhase, Set<Int>>>,
        val killedPlayersSeenByWitch: Set<Int>,
        val seerReveals: List<Pair<Int, Int>>
    )
}
