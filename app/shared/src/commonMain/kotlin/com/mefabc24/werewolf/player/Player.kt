package com.mefabc24.werewolf.player

import com.mefabc24.werewolf.player.role.Role

data class Player(
    val id: Int,
    val name: String,
    var role: Role? = null,
    var isAlive: Boolean = true
)
