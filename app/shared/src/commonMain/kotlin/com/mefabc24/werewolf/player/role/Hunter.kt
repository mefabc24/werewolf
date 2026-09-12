package com.mefabc24.werewolf.player.role

import kotlinx.serialization.Serializable

@Serializable
data object Hunter : Role {
    override val name = "Hunter"
    override val team = Team.VILLAGE
}