package com.mefabc24.werewolf.client

import com.mefabc24.werewolf.game.ClientGameState
import com.mefabc24.werewolf.player.PlayerInfo

class ClientState {
    var playerId: Int? = null
    val players = mutableListOf<PlayerInfo>()
    var gameState: ClientGameState? = null
}