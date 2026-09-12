package com.mefabc24.werewolf.game.managers

import com.mefabc24.werewolf.game.GameState
import com.mefabc24.werewolf.player.Player
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration

class VotingManagerTest {

    @Test
    fun allLivingPlayersResolveAUniqueWinner() = runTest {
        val state = stateWithPlayers(3)
        val manager = VotingManager(state)
        manager.startVoting()

        assertTrue(manager.submitVote(voterId = 1, targetId = 2))
        assertTrue(manager.submitVote(voterId = 2, targetId = 1))
        assertTrue(manager.submitVote(voterId = 3, targetId = 2))

        assertEquals(2, manager.awaitResult())
    }

    @Test
    fun aTieDoesNotEliminateAnyone() = runTest {
        val manager = VotingManager(stateWithPlayers(4))
        manager.startVoting()
        manager.submitVote(voterId = 1, targetId = 3)
        manager.submitVote(voterId = 2, targetId = 4)
        manager.submitVote(voterId = 3, targetId = 4)
        manager.submitVote(voterId = 4, targetId = 3)

        assertNull(manager.awaitResult())
    }

    @Test
    fun deadPlayersAreNotRequiredForVotingToFinish() = runTest {
        val state = stateWithPlayers(3)
        state.players.single { it.id == 3 }.isAlive = false
        val manager = VotingManager(state)
        manager.startVoting()

        manager.submitVote(voterId = 1, targetId = 2)
        manager.submitVote(voterId = 2, targetId = 1)

        assertNull(manager.awaitResult())
    }

    @Test
    fun aPlayerCannotVoteTwiceOrReplaceTheFirstVote() {
        val state = stateWithPlayers(3)
        val manager = VotingManager(state)
        manager.startVoting()

        assertTrue(manager.submitVote(voterId = 1, targetId = 2))
        assertFalse(manager.submitVote(voterId = 1, targetId = 3))

        assertEquals(1, state.votes.size)
        assertEquals(2, state.votes.single().targetId)
    }

    @Test
    fun timeoutUsesTheVotesAlreadyCast() = runTest {
        val manager = VotingManager(stateWithPlayers(4))
        manager.startVoting()
        manager.submitVote(voterId = 1, targetId = 2)
        manager.submitVote(voterId = 3, targetId = 2)

        assertEquals(2, manager.awaitResult(Duration.ZERO))
    }

    @Test
    fun timeoutWithTiedPartialVotesDoesNotEliminateAnyone() = runTest {
        val manager = VotingManager(stateWithPlayers(4))
        manager.startVoting()
        manager.submitVote(voterId = 1, targetId = 2)
        manager.submitVote(voterId = 3, targetId = 4)

        assertNull(manager.awaitResult(Duration.ZERO))
    }

    @Test
    fun timeoutWithoutVotesDoesNotEliminateAnyone() = runTest {
        val manager = VotingManager(stateWithPlayers(3))
        manager.startVoting()

        assertNull(manager.awaitResult(Duration.ZERO))
    }

    @Test
    fun awaitingBeforeVotingStartedFailsFast() = runTest {
        val manager = VotingManager(stateWithPlayers(3))

        assertFailsWith<IllegalStateException> {
            manager.awaitResult(Duration.ZERO)
        }
    }

    private fun stateWithPlayers(playerCount: Int): GameState =
        GameState(
            players = (1..playerCount)
                .map { Player(it, "Player $it") }
                .toMutableList()
        )
}
