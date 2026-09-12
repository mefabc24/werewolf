package com.mefabc24.werewolf

import com.mefabc24.werewolf.game.ClientGameState
import com.mefabc24.werewolf.game.DayPhase
import com.mefabc24.werewolf.game.DeathCause
import com.mefabc24.werewolf.game.GameEvent
import com.mefabc24.werewolf.game.GamePhase
import com.mefabc24.werewolf.game.NightPhase
import com.mefabc24.werewolf.lobby.Lobby
import com.mefabc24.werewolf.lobby.LobbyController
import com.mefabc24.werewolf.network.ActionAcceptedResponse
import com.mefabc24.werewolf.network.ConnectedResponse
import com.mefabc24.werewolf.network.DayPhaseStartedEvent
import com.mefabc24.werewolf.network.ErrorResponse
import com.mefabc24.werewolf.network.Event
import com.mefabc24.werewolf.network.GameEndedEvent
import com.mefabc24.werewolf.network.GameStartedEvent
import com.mefabc24.werewolf.network.GameStartedResponse
import com.mefabc24.werewolf.network.GameWonEvent
import com.mefabc24.werewolf.network.MessageEvent
import com.mefabc24.werewolf.network.MessageRequest
import com.mefabc24.werewolf.network.NightEndedEvent
import com.mefabc24.werewolf.network.NightPhaseStartedEvent
import com.mefabc24.werewolf.network.PlayerDiedEvent
import com.mefabc24.werewolf.network.PlayerJoinedEvent
import com.mefabc24.werewolf.network.PlayerLeftEvent
import com.mefabc24.werewolf.network.PlayerVotedEvent
import com.mefabc24.werewolf.network.Request
import com.mefabc24.werewolf.network.Response
import com.mefabc24.werewolf.network.RoleRevealEvent
import com.mefabc24.werewolf.network.RoundStartedEvent
import com.mefabc24.werewolf.network.SeerActionRequest
import com.mefabc24.werewolf.network.SeerTurnEvent
import com.mefabc24.werewolf.network.StartGameRequest
import com.mefabc24.werewolf.network.VoteAcceptedResponse
import com.mefabc24.werewolf.network.VoteRequest
import com.mefabc24.werewolf.network.VotingFinishedEvent
import com.mefabc24.werewolf.network.WerewolfActionRequest
import com.mefabc24.werewolf.network.WerewolfTurnEvent
import com.mefabc24.werewolf.network.WitchActionRequest
import com.mefabc24.werewolf.network.WitchTurnEvent
import com.mefabc24.werewolf.player.Player
import com.mefabc24.werewolf.player.PlayerInfo
import com.mefabc24.werewolf.player.role.NightActionMode
import com.mefabc24.werewolf.player.role.OptionalRole
import com.mefabc24.werewolf.player.role.Seer
import com.mefabc24.werewolf.player.role.Team
import com.mefabc24.werewolf.player.role.Villager
import com.mefabc24.werewolf.player.role.Werewolf
import com.mefabc24.werewolf.player.role.Witch
import com.mefabc24.werewolf.settings.GameSettings
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SharedCommonTest {

    @Test
    fun defaultSettingsDescribeAPlayableGame() {
        val settings = GameSettings()

        assertEquals(1, settings.werewolfAmount)
        assertEquals(0, settings.optionalRoles[OptionalRole.WITCH])
        assertEquals(0, settings.optionalRoles[OptionalRole.SEER])
        assertTrue(settings.canWitchUseBothPotions)
        assertTrue(settings.canWitchHealSelf)
        assertTrue(settings.canWitchKillWitch)
        assertEquals(-1, settings.voteTimeSeconds)
        assertEquals(0, settings.discussionTimeSeconds)
        assertEquals(-1, settings.nightRoleActingTimeSeconds)
    }

    @Test
    fun settingsRejectGamesWithoutWerewolves() {
        assertFailsWith<IllegalArgumentException> {
            GameSettings(werewolfAmount = 0)
        }
        assertFailsWith<IllegalArgumentException> {
            GameSettings(werewolfAmount = -1)
        }
    }

    @Test
    fun customSettingsKeepEveryRule() {
        val settings = GameSettings(
            werewolfAmount = 3,
            canWitchUseBothPotions = false,
            canWitchHealSelf = false,
            canWitchKillWitch = false,
            voteTimeSeconds = 45,
            discussionTimeSeconds = 90,
            nightRoleActingTimeSeconds = 30,
            optionalRoles = mapOf(OptionalRole.WITCH to 2, OptionalRole.SEER to 0)
        )

        assertEquals(3, settings.werewolfAmount)
        assertFalse(settings.canWitchUseBothPotions)
        assertFalse(settings.canWitchHealSelf)
        assertFalse(settings.canWitchKillWitch)
        assertEquals(45, settings.voteTimeSeconds)
        assertEquals(90, settings.discussionTimeSeconds)
        assertEquals(30, settings.nightRoleActingTimeSeconds)
        assertEquals(mapOf(OptionalRole.WITCH to 2, OptionalRole.SEER to 0), settings.optionalRoles)
    }

    @Test
    fun lobbyKeepsJoinOrderAndReturnsAPlayersSnapshot() {
        val lobby = Lobby()
        val controller = LobbyController(lobby)
        val first = Player(1, "Ada")
        val second = Player(2, "Linus")

        controller.join(first)
        controller.join(second)
        val snapshot = controller.getPlayers()
        lobby.players.add(Player(3, "Grace"))

        assertContentEquals(listOf(first, second), snapshot)
        assertNotSame(lobby.players, snapshot)
        assertContentEquals(listOf(1, 2, 3), controller.getPlayers().map { it.id })
    }

    @Test
    fun leavingRemovesAndReturnsExactlyTheRequestedPlayer() {
        val first = Player(1, "Ada")
        val second = Player(2, "Linus")
        val lobby = Lobby(players = mutableListOf(first, second))
        val controller = LobbyController(lobby)

        assertEquals(first, controller.leave(first.id))
        assertContentEquals(listOf(second), controller.getPlayers())
    }

    @Test
    fun leavingWithAnUnknownIdDoesNotChangeTheLobby() {
        val players = mutableListOf(Player(1, "Ada"), Player(2, "Linus"))
        val controller = LobbyController(Lobby(players = players))

        assertNull(controller.leave(99))
        assertContentEquals(listOf(1, 2), controller.getPlayers().map { it.id })
    }

    @Test
    fun lobbySettingsCanBeReplaced() {
        val controller = LobbyController(Lobby())
        val newSettings = GameSettings(werewolfAmount = 2, optionalRoles = emptyMap())

        controller.updateSettings(newSettings)

        assertEquals(newSettings, controller.getSettings())
    }

    @Test
    fun onlyTheConfiguredLobbyHostIsRecognized() {
        val controller = LobbyController(Lobby(hostPlayerId = 7))

        assertTrue(controller.isHost(7))
        assertFalse(controller.isHost(6))
    }

    @Test
    fun lobbyPlayerInfosDoNotLeakRolesOrDeadState() {
        val secretWerewolf = Player(1, "Ada", role = Werewolf, isAlive = false)
        val controller = LobbyController(Lobby(players = mutableListOf(secretWerewolf)))

        assertEquals(
            listOf(PlayerInfo(id = 1, playerName = "Ada")),
            controller.getPlayerInfos()
        )
    }

    @Test
    fun rolesExposeTheCorrectTeamsAndNightActionModes() {
        assertEquals(Team.VILLAGE, Villager.team)
        assertEquals(Team.WEREWOLVES, Werewolf.team)
        assertEquals(Team.VILLAGE, Witch.team)
        assertEquals(Team.VILLAGE, Seer.team)
        assertEquals(NightActionMode.SIMULTANEOUS, Werewolf.nightActionMode)
        assertEquals(NightActionMode.SEQUENTIAL, Witch.nightActionMode)
        assertEquals(NightActionMode.SIMULTANEOUS, Seer.nightActionMode)
    }

    @Test
    fun clientGameStateStartsInAnEmptyLobby() {
        val state = ClientGameState()

        assertTrue(state.players.isEmpty())
        assertEquals(GamePhase.LOBBY, state.gamePhase)
        assertNull(state.nightPhase)
        assertNull(state.dayPhase)
        assertTrue(state.hasVoted.isEmpty())
        assertEquals(0, state.round)
        assertNull(state.werewolfIds)
    }

    @Test
    fun clientStatesDoNotShareTheirMutableVotingSet() {
        val first = ClientGameState()
        val second = ClientGameState()

        first.hasVoted.add(42)

        assertEquals(setOf(42), first.hasVoted)
        assertTrue(second.hasVoted.isEmpty())
        assertNotSame(first.hasVoted, second.hasVoted)
    }

    @Test
    fun everyRequestTypeSurvivesPolymorphicSerialization() {
        val requests: List<Request> = listOf(
            MessageRequest("hello"),
            StartGameRequest,
            VoteRequest(targetId = 4),
            WerewolfActionRequest(targetId = null),
            WerewolfActionRequest(targetId = 3),
            WitchActionRequest(healTargetId = 2, killTargetId = 5),
            SeerActionRequest(targetId = 1)
        )

        requests.forEach(::assertRequestRoundTrip)
    }

    @Test
    fun everyResponseTypeSurvivesPolymorphicSerialization() {
        val responses: List<Response> = listOf(
            ConnectedResponse(1, listOf(PlayerInfo(1, "Ada", Witch))),
            GameStartedResponse,
            ErrorResponse("invalid action"),
            ActionAcceptedResponse,
            VoteAcceptedResponse
        )

        responses.forEach(::assertResponseRoundTrip)
    }

    @Test
    fun gameplayEventsSurvivePolymorphicSerialization() {
        val gameState = ClientGameState(
            players = listOf(
                PlayerInfo(1, "Ada", Werewolf),
                PlayerInfo(2, "Linus", isAlive = false)
            ),
            gamePhase = GamePhase.NIGHT,
            nightPhase = NightPhase.WITCH,
            hasVoted = mutableSetOf(2),
            round = 3,
            werewolfIds = listOf(1),
            settings = GameSettings(optionalRoles = mapOf(OptionalRole.WITCH to 1))
        )
        val events: List<Event> = listOf(
            PlayerJoinedEvent(1, "Ada"),
            PlayerLeftEvent(1, "Ada"),
            MessageEvent(1, "hello"),
            GameStartedEvent(gameState),
            com.mefabc24.werewolf.network.NightStartedEvent,
            NightPhaseStartedEvent(NightPhase.WEREWOLVES),
            WerewolfTurnEvent,
            WitchTurnEvent(setOf(2)),
            SeerTurnEvent,
            com.mefabc24.werewolf.network.DayStartedEvent,
            DayPhaseStartedEvent(DayPhase.VOTING),
            PlayerVotedEvent(1),
            VotingFinishedEvent(2),
            NightEndedEvent(setOf(2)),
            RoundStartedEvent(3),
            PlayerDiedEvent(2,DeathCause.NIGHT),
            RoleRevealEvent(2, Villager),
            GameWonEvent(Team.VILLAGE),
            GameEndedEvent
        )

        events.forEach(::assertEventRoundTrip)
    }

    @Test
    fun gameEventsAreBroadcastByDefaultAndCanBeTargeted() {
        assertTrue(GameEvent(GameEndedEvent).recipients.isEmpty())
        assertEquals(setOf(2, 5), GameEvent(WerewolfTurnEvent, setOf(2, 5)).recipients)
    }

    private fun assertRequestRoundTrip(request: Request) {
        val encoded = Json.encodeToString<Request>(request)
        assertEquals(request, Json.decodeFromString<Request>(encoded))
    }

    private fun assertResponseRoundTrip(response: Response) {
        val encoded = Json.encodeToString<Response>(response)
        assertEquals(response, Json.decodeFromString<Response>(encoded))
    }

    private fun assertEventRoundTrip(event: Event) {
        val encoded = Json.encodeToString<Event>(event)
        assertEquals(event, Json.decodeFromString<Event>(encoded))
    }
}
