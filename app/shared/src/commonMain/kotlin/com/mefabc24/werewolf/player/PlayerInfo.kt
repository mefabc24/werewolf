package com.mefabc24.werewolf.player

import com.mefabc24.werewolf.player.role.Role
import kotlinx.serialization.Serializable

@Serializable
data class PlayerInfo(
    val id: Int,
    val playerName: String,
    val role: Role? = null,
    val isAlive: Boolean = true
)
