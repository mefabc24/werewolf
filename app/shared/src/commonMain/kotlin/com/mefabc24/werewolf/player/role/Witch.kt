package com.mefabc24.werewolf.player.role

import kotlinx.serialization.Serializable

@Serializable
data object Witch : NightRole {
    override val name = "Witch"
    override val team = Team.VILLAGE
    override val nightActionMode = NightActionMode.SEQUENTIAL
}