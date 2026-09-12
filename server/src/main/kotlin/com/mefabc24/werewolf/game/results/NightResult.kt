package com.mefabc24.werewolf.game.results

data class NightResult(
    val killedPlayerIds: MutableSet<Int> = mutableSetOf()
)
