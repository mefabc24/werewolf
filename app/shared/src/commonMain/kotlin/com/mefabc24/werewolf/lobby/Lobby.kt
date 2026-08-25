package com.mefabc24.werewolf.lobby

import com.mefabc24.werewolf.player.Player

data class Lobby(
    var hostPlayerId: Int? = null,
    val players: MutableList<Player> = mutableListOf()
)
