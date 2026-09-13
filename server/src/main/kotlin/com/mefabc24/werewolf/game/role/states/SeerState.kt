package com.mefabc24.werewolf.game.role.states

data class SeerState(
    var canSee: Boolean = true,
    val seenPlayerIds: MutableSet<Int> = mutableSetOf()
)
