package com.mefabc24.werewolf.settings

import com.mefabc24.werewolf.player.role.OptionalRole
import kotlinx.serialization.Serializable

@Serializable
data class GameSettings(
    val werewolfAmount: Int = 1,

    val canWitchUseBothPotions: Boolean = true,
    val canWitchHealSelf: Boolean = true,
    val canWitchKillWitch: Boolean = true,

    val voteTimeSeconds: Int = -1,
    val discussionTimeSeconds: Int = 0,
    val nightRoleActingTimeSeconds: Int = -1,

    val optionalRoles: Map<OptionalRole, Int> = mapOf(
        OptionalRole.WITCH to 0,
        OptionalRole.SEER to 0,
        OptionalRole.MAYOR to 0,
        OptionalRole.HUNTER to 1,
    )
) {
    init {
        require(werewolfAmount >= SettingConstraints.MIN_WEREWOLVES)
    }
}