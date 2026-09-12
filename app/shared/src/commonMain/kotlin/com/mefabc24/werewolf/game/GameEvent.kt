package com.mefabc24.werewolf.game

import com.mefabc24.werewolf.network.Event

data class GameEvent(
    val event: Event,
    val recipients: Set<Int> = emptySet(),
)
