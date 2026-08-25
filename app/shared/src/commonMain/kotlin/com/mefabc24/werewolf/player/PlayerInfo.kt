package com.mefabc24.werewolf.player

import kotlinx.serialization.Serializable

@Serializable
data class PlayerInfo(
    val playerId: Int,
    val playerName: String
)