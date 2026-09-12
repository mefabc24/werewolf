package com.mefabc24.werewolf.game.managers

import com.mefabc24.werewolf.game.GameState
import com.mefabc24.werewolf.game.Vote
import com.mefabc24.werewolf.player.role.Mayor
import kotlinx.coroutines.CompletableDeferred
import kotlin.time.Duration
import kotlinx.coroutines.withTimeoutOrNull

class VotingManager(
    private val gameState: GameState
) {
    private var pendingResult: CompletableDeferred<Int?>? = null

    fun startVoting() {
        pendingResult = CompletableDeferred()
    }

    suspend fun awaitResult(): Int? {
        val result = pendingResult
            ?: error("Voting has not been started.")

        return result.await()
    }

    suspend fun awaitResult(timeout: Duration): Int? {
        val result = pendingResult
            ?: error("Voting has not been started.")

        return withTimeoutOrNull(timeout) {
            result.await()
        } ?: calculateResult()
    }

    fun submitVote(
        voterId: Int,
        targetId: Int
    ): Boolean {
        if (gameState.votes.any { it.voterId == voterId }) {
            return false
        }

        val voter = gameState.players.find { it.id == voterId }
            ?: return false

        if (!voter.isAlive) return false

        gameState.votes.add(
            Vote(voterId, targetId)
        )

        val alivePlayers = gameState.players.count { it.isAlive }

        if (gameState.votes.size == alivePlayers) {
            pendingResult?.complete(calculateResult())
        }

        return true
    }

    private fun calculateResult(): Int? {
        val voteCounts = gameState.votes
            .groupBy { it.targetId }
            .mapValues { (_, votes) ->
                votes.sumOf { vote ->
                    val voter = gameState.players.first { it.id == vote.voterId }

                    if (voter.role == Mayor) 2 else 1
                }
            }

        val highestCount = voteCounts.values.maxOrNull()
            ?: return null

        val winners = voteCounts
            .filterValues { it == highestCount }
            .keys

        return winners.singleOrNull()
    }
}
