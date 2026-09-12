package com.mefabc24.werewolf.game.actions

data class WitchAction(
    val healTargetId: Int? = null,
    val killTargetId: Int? = null
) : Action
