package com.mefabc24.werewolf.player.role

import kotlinx.serialization.Serializable

@Serializable
sealed interface NightRole : Role {
    val nightActionMode: NightActionMode
}