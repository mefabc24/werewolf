package com.mefabc24.werewolf.lobby

import com.mefabc24.werewolf.player.Player
import com.mefabc24.werewolf.player.PlayerInfo

class LobbyController(private val lobby: Lobby) {
    fun join(player: Player) {
        lobby.players.add(player)
    }

    fun leave(playerId: Int): Player? {
        val player = lobby.players.find { it.id == playerId }
            ?: return null

        lobby.players.remove(player)

        return player
    }

    fun isHost(playerId: Int): Boolean = lobby.hostPlayerId == playerId

    fun getPlayers(): List<Player> = lobby.players.toList()

    fun getPlayerInfos(): List<PlayerInfo> = lobby.players.map { PlayerInfo(it.id, it.name) }
}