package com.mefabc24.werewolf.game

import com.mefabc24.werewolf.player.PlayerInfo

data class ClientGameState(
    val players: MutableList<PlayerInfo> = mutableListOf(),
    var gamePhase: GamePhase = GamePhase.LOBBY,
    var nightPhase: NightPhase? = null,
    val hasVoted: MutableSet<Int> = mutableSetOf(), // IDs of all players who already voted
    var round: Int = 0
)
