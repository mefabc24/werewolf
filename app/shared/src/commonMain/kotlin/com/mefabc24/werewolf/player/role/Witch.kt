package com.mefabc24.werewolf.player.role

data class Witch(
    var hasHealPotion: Boolean = true,
    var hasKillPotion: Boolean = true
) : Role {
    override val name = "Witch"
    override val team = Team.VILLAGE
}