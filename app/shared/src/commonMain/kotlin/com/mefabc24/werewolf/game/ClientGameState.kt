package com.mefabc24.werewolf.game

import com.mefabc24.werewolf.player.PlayerInfo
import kotlinx.serialization.Serializable

@Serializable
data class ClientGameState(
    val players: List<PlayerInfo> = emptyList(),
    val gamePhase: GamePhase = GamePhase.LOBBY,
    val nightPhase: NightPhase? = null,
    val hasVoted: MutableSet<Int> = mutableSetOf(), // IDs of all players who already voted
    val round: Int = 0,
    val werewolfIds: List<Int>? = null
)
