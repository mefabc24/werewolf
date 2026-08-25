package com.mefabc24.werewolf.player.role

data object Villager : Role {
    override val name = "Villager"
    override val team = Team.VILLAGE
}