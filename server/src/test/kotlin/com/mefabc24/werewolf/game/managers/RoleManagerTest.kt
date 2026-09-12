package com.mefabc24.werewolf.game.managers

import com.mefabc24.werewolf.game.GameState
import com.mefabc24.werewolf.player.Player
import com.mefabc24.werewolf.player.role.OptionalRole
import com.mefabc24.werewolf.player.role.Seer
import com.mefabc24.werewolf.player.role.Team
import com.mefabc24.werewolf.player.role.Villager
import com.mefabc24.werewolf.player.role.Werewolf
import com.mefabc24.werewolf.player.role.Witch
import com.mefabc24.werewolf.settings.GameSettings
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class RoleManagerTest {

    @Test
    fun assignsExactlyTheConfiguredRolesAndFillsWithVillagers() {
        val state = stateWithPlayers(
            playerCount = 8,
            settings = GameSettings(
                werewolfAmount = 2,
                optionalRoles = mapOf(
                    OptionalRole.WITCH to 1,
                    OptionalRole.SEER to 2
                )
            )
        )

        RoleManager(state).assignRoles()

        assertEquals(2, state.players.count { it.role == Werewolf })
        assertEquals(1, state.players.count { it.role == Witch })
        assertEquals(2, state.players.count { it.role == Seer })
        assertEquals(3, state.players.count { it.role == Villager })
        assertTrue(state.players.all { it.role != null })
    }

    @Test
    fun supportsGamesWithoutOptionalRoles() {
        val state = stateWithPlayers(
            playerCount = 4,
            settings = GameSettings(werewolfAmount = 1, optionalRoles = emptyMap())
        )

        RoleManager(state).assignRoles()

        assertEquals(1, state.players.count { it.role == Werewolf })
        assertEquals(3, state.players.count { it.role == Villager })
    }

    @Test
    fun rejectsAConfigurationWithMoreRolesThanPlayers() {
        val state = stateWithPlayers(
            playerCount = 2,
            settings = GameSettings(
                werewolfAmount = 1,
                optionalRoles = mapOf(OptionalRole.WITCH to 1, OptionalRole.SEER to 1)
            )
        )

        val exception = assertFailsWith<IllegalArgumentException> {
            RoleManager(state).assignRoles()
        }

        assertEquals("More roles configured than available players.", exception.message)
    }

    @Test
    fun assigningAgainReplacesEveryPreviousRole() {
        val state = stateWithPlayers(
            playerCount = 5,
            settings = GameSettings(werewolfAmount = 1, optionalRoles = emptyMap())
        )
        state.players.forEach { it.role = Witch }

        RoleManager(state).assignRoles()

        assertEquals(0, state.players.count { it.role == Witch })
        assertEquals(1, state.players.count { it.role == Werewolf })
        assertEquals(4, state.players.count { it.role == Villager })
    }

    @Test
    fun roleAndTeamQueriesReturnOnlyMatchingPlayers() {
        val wolf = Player(1, "Wolf", Werewolf)
        val witch = Player(2, "Witch", Witch)
        val seer = Player(3, "Seer", Seer)
        val villager = Player(4, "Villager", Villager)
        val manager = RoleManager(
            GameState(players = mutableListOf(wolf, witch, seer, villager))
        )

        assertEquals(listOf(wolf), manager.getPlayersByTeam(Team.WEREWOLVES))
        assertEquals(listOf(witch, seer, villager), manager.getPlayersByTeam(Team.VILLAGE))
        assertEquals(listOf(witch), manager.getPlayersByRole(Witch))
    }

    private fun stateWithPlayers(playerCount: Int, settings: GameSettings): GameState =
        GameState(
            players = (1..playerCount)
                .map { Player(it, "Player $it") }
                .toMutableList(),
            settings = settings
        )
}
