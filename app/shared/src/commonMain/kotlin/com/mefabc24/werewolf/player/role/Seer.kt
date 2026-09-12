package com.mefabc24.werewolf.player.role

import kotlinx.serialization.Serializable

@Serializable
data object Seer : NightRole {
    override val name = "Seer"
    override val team = Team.VILLAGE
    override val nightActionMode = NightActionMode.SIMULTANEOUS
}