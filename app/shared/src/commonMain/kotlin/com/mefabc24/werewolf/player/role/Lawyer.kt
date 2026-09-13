package com.mefabc24.werewolf.player.role

import kotlinx.serialization.Serializable

@Serializable
data object Lawyer : Role {
    override val name = "Lawyer"
    override val team = Team.WEREWOLVES
}