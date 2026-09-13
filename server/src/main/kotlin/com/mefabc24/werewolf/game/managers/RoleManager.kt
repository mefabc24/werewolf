package com.mefabc24.werewolf.game.managers

import com.mefabc24.werewolf.game.GameState
import com.mefabc24.werewolf.player.Player
import com.mefabc24.werewolf.player.role.Mayor
import com.mefabc24.werewolf.player.role.OptionalRole
import com.mefabc24.werewolf.player.role.Role
import com.mefabc24.werewolf.player.role.Seer
import com.mefabc24.werewolf.player.role.Team
import com.mefabc24.werewolf.player.role.Villager
import com.mefabc24.werewolf.player.role.Werewolf
import com.mefabc24.werewolf.player.role.Witch

class RoleManager(
    private val gameState: GameState
) {
    fun assignRoles() {
        val players = gameState.players
        val settings = gameState.settings

        val roles = mutableListOf<Role>()

        repeat(settings.werewolfAmount) {
            roles.add(Werewolf)
        }

        settings.optionalRoles.forEach { (optionalRole, amount) ->
            repeat(amount) {
                roles.add(optionalRole.role)
            }
        }

        require(roles.size <= players.size) {
            "More roles configured than available players."
        }

        while (roles.size < players.size) {
            roles.add(Villager)
        }

        roles.shuffle()

        players.forEachIndexed { index, player ->
            player.role = roles[index]
        }
    }

    fun getRandomWerewolf(): Player? {
        return gameState.players
            .filter { it.role == Werewolf }
            .randomOrNull()
    }

    fun getPlayersByTeam(team: Team): List<Player> =
        gameState.players.filter { it.role?.team == team }

    fun getPlayersByRole(role: Role): List<Player> =
        gameState.players.filter { it.role == role }
}