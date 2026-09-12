package com.mefabc24.werewolf.player.role

import kotlinx.serialization.Serializable

@Serializable
data object Villager : Role {
    override val name = "Villager"
    override val team = Team.VILLAGE
}