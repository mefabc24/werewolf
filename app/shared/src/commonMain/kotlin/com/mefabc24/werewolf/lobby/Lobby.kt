package com.mefabc24.werewolf.lobby

import com.mefabc24.werewolf.player.Player
import com.mefabc24.werewolf.settings.GameSettings

data class Lobby(
    var hostPlayerId: Int? = null,
    val players: MutableList<Player> = mutableListOf(),
    var settings: GameSettings = GameSettings()
)
