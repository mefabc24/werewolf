package com.mefabc24.werewolf.client

import com.mefabc24.werewolf.game.ClientGameState
import com.mefabc24.werewolf.player.PlayerInfo

class ClientState {
    var id: Int? = null
    var gameState: ClientGameState = ClientGameState()

    val selfPlayer: PlayerInfo?
        get() = gameState.players.find { it.id == id }
}