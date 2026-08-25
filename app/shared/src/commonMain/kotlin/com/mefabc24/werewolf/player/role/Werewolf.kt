package com.mefabc24.werewolf.player.role

data object Werewolf : Role {
    override val name = "Werewolf"
    override val team = Team.WEREWOLVES
}