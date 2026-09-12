package com.mefabc24.werewolf.request

import com.mefabc24.werewolf.game.GameController
import com.mefabc24.werewolf.game.GameState
import com.mefabc24.werewolf.lobby.Lobby
import com.mefabc24.werewolf.lobby.LobbyController
import com.mefabc24.werewolf.network.ErrorResponse
import com.mefabc24.werewolf.network.MessageEvent
import com.mefabc24.werewolf.network.MessageRequest
import com.mefabc24.werewolf.network.PlaceholderEvent
import com.mefabc24.werewolf.network.PlaceholderRequest
import com.mefabc24.werewolf.network.PlaceholderResponse
import com.mefabc24.werewolf.network.SeerActionRequest
import com.mefabc24.werewolf.network.StartGameRequest
import com.mefabc24.werewolf.network.VoteRequest
import com.mefabc24.werewolf.network.WerewolfActionRequest
import com.mefabc24.werewolf.network.WitchActionRequest
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RequestHandlerTest {

    @Test
    fun placeholderRequestProducesBothPlaceholderMessages() = runTest {
        val result = handler().handle(playerId = 7, request = PlaceholderRequest)

        assertEquals(PlaceholderResponse, result.response)
        assertEquals(PlaceholderEvent, result.event)
    }

    @Test
    fun chatMessageKeepsTheAuthenticatedSenderId() = runTest {
        val result = handler().handle(playerId = 7, request = MessageRequest("hello"))

        assertNull(result.response)
        assertEquals(MessageEvent(playerId = 7, message = "hello"), result.event)
    }

    @Test
    fun onlyTheLobbyHostCanStartTheGame() = runTest {
        val result = handler(hostPlayerId = 1).handle(playerId = 2, request = StartGameRequest)

        assertEquals(ErrorResponse("Only the host can start the game."), result.response)
        assertNull(result.event)
    }

    @Test
    fun invalidWerewolfActionReturnsTheProtocolError() = runTest {
        val result = handler().handle(1, WerewolfActionRequest(targetId = 2))

        assertEquals(ErrorResponse("Invalid werewolf action."), result.response)
    }

    @Test
    fun invalidWitchActionReturnsTheProtocolError() = runTest {
        val result = handler().handle(1, WitchActionRequest(healTargetId = 2))

        assertEquals(ErrorResponse("Invalid witch action."), result.response)
    }

    @Test
    fun invalidSeerActionReturnsTheProtocolError() = runTest {
        val result = handler().handle(1, SeerActionRequest(targetId = 2))

        assertEquals(ErrorResponse("Invalid seer action."), result.response)
    }

    @Test
    fun invalidVoteReturnsTheProtocolError() = runTest {
        val result = handler().handle(1, VoteRequest(targetId = 2))

        assertEquals(ErrorResponse("Invalid vote."), result.response)
    }

    private fun handler(hostPlayerId: Int? = null): RequestHandler =
        RequestHandler(
            gameController = GameController(GameState()),
            lobbyController = LobbyController(Lobby(hostPlayerId = hostPlayerId))
        )
}
