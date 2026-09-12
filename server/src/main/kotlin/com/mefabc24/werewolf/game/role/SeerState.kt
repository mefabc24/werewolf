package com.mefabc24.werewolf.game.role

data class SeerState(
    var canSee: Boolean = true,
    val seenPlayerIds: MutableSet<Int> = mutableSetOf()
)
