package com.mefabc24.werewolf.player.role

import kotlinx.serialization.Serializable

@Serializable
data object Mayor : Role {
    override val name = "Mayor"
    override val team = Team.VILLAGE
}