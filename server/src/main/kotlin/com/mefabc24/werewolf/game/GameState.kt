package com.mefabc24.werewolf.game

import com.mefabc24.werewolf.player.Player

data class GameState(
    val players: MutableList<Player> = mutableListOf(),
    var gamePhase: GamePhase = GamePhase.LOBBY,
    var nightPhase: NightPhase? = null,
    val votes: MutableSet<Vote> = mutableSetOf(),
    var round: Int = 0
)
