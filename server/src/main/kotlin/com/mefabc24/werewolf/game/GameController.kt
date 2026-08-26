package com.mefabc24.werewolf.game

import com.mefabc24.werewolf.player.Player
import com.mefabc24.werewolf.player.PlayerInfo
import com.mefabc24.werewolf.player.role.Team

class GameController(
    private val gameState: GameState
) {

    fun start(players: List<Player>) {
        gameState.players.clear()
        gameState.players.addAll(players)

        gameState.round = 1
        gameState.gamePhase = GamePhase.START
    }

    fun getClientGameState(playerId: Int): ClientGameState {
        val player = gameState.players.find { it.id == playerId }

        val werewolfIds =
            if (player?.role?.team == Team.WEREWOLVES) {
                gameState.players
                    .filter { it.role?.team == Team.WEREWOLVES }
                    .map { it.id }
            } else null

        return ClientGameState(
            players = gameState.players.map { PlayerInfo(it.id, it.name) },
            gamePhase = gameState.gamePhase,
            nightPhase = gameState.nightPhase,
            round = gameState.round,
            hasVoted = gameState.votes.map { it.voterId }.toMutableSet(),
            werewolfIds = werewolfIds
        )
    }
}