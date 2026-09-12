package com.mefabc24.werewolf.game

import com.mefabc24.werewolf.player.Player
import com.mefabc24.werewolf.player.role.Villager
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GameControllerVoteTest {

    @Test
    fun votingIsRejectedOutsideTheDayVotingPhase() = runTest {
        val state = stateReadyForVoting()
        val controller = GameController(state)

        state.gamePhase = GamePhase.LOBBY
        assertFalse(controller.submitVote(1, 2))

        state.gamePhase = GamePhase.DAY
        state.dayPhase = DayPhase.DISCUSSION
        assertFalse(controller.submitVote(1, 2))

        state.gamePhase = GamePhase.NIGHT
        state.dayPhase = DayPhase.VOTING
        assertFalse(controller.submitVote(1, 2))

        assertTrue(state.votes.isEmpty())
    }

    @Test
    fun aLivingPlayerCanVoteForAnotherLivingPlayer() = runTest {
        val state = stateReadyForVoting()
        val controller = GameController(state)

        assertTrue(controller.submitVote(voterId = 1, targetId = 2))

        assertEquals(setOf(Vote(voterId = 1, targetId = 2)), state.votes)
    }

    @Test
    fun selfVotingIsRejectedWithoutRecordingAVote() = runTest {
        val state = stateReadyForVoting()
        val controller = GameController(state)

        assertFalse(controller.submitVote(voterId = 1, targetId = 1))

        assertTrue(state.votes.isEmpty())
    }

    @Test
    fun unknownVotersAndTargetsAreRejected() = runTest {
        val state = stateReadyForVoting()
        val controller = GameController(state)

        assertFalse(controller.submitVote(voterId = 99, targetId = 2))
        assertFalse(controller.submitVote(voterId = 1, targetId = 99))

        assertTrue(state.votes.isEmpty())
    }

    @Test
    fun deadVotersAndDeadTargetsAreRejected() = runTest {
        val state = stateReadyForVoting()
        val controller = GameController(state)
        state.players.single { it.id == 1 }.isAlive = false
        state.players.single { it.id == 3 }.isAlive = false

        assertFalse(controller.submitVote(voterId = 1, targetId = 2))
        assertFalse(controller.submitVote(voterId = 2, targetId = 3))

        assertTrue(state.votes.isEmpty())
    }

    @Test
    fun secondVoteFromTheSamePlayerIsRejectedAndFirstVoteWins() = runTest {
        val state = stateReadyForVoting()
        val controller = GameController(state)

        assertTrue(controller.submitVote(voterId = 1, targetId = 2))
        assertFalse(controller.submitVote(voterId = 1, targetId = 3))

        assertEquals(setOf(Vote(voterId = 1, targetId = 2)), state.votes)
    }

    private fun stateReadyForVoting(): GameState =
        GameState(
            players = (1..3)
                .map { Player(it, "Player $it", Villager) }
                .toMutableList(),
            gamePhase = GamePhase.DAY,
            dayPhase = DayPhase.VOTING
        )
}
