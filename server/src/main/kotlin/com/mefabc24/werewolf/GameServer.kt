package com.mefabc24.werewolf

import com.mefabc24.werewolf.game.GameController
import com.mefabc24.werewolf.game.GamePhase
import com.mefabc24.werewolf.game.GameState
import com.mefabc24.werewolf.lobby.Lobby
import com.mefabc24.werewolf.lobby.LobbyController
import com.mefabc24.werewolf.network.ConnectedResponse
import com.mefabc24.werewolf.network.Event
import com.mefabc24.werewolf.network.PlayerJoinedEvent
import com.mefabc24.werewolf.network.PlayerLeftEvent
import com.mefabc24.werewolf.network.Request
import com.mefabc24.werewolf.network.Response
import com.mefabc24.werewolf.player.Player
import com.mefabc24.werewolf.request.RequestHandler
import io.ktor.server.application.install
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.websocket.*
import io.ktor.server.netty.Netty
import io.ktor.server.routing.routing
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.serialization.json.Json

class GameServer {
    private var server: EmbeddedServer<*,*>? = null
    private val clients = mutableMapOf<Int, WebSocketSession>()

    private val lobby = Lobby()
    private val lobbyController = LobbyController(lobby)

    private val gameState = GameState()
    private val gameController = GameController(gameState)

    private var nextPlayerId: Int = 1

    private val requestHandler = RequestHandler(gameController, lobbyController)

    fun start() {
        server = embeddedServer(Netty, 8080) {
            install(WebSockets)

            routing {
                webSocket("/werewolf") {
                    if (gameState.gamePhase != GamePhase.LOBBY) {
                        close(
                            CloseReason(
                                CloseReason.Codes.VIOLATED_POLICY,
                                "Game already in progress"
                            )
                        )
                        return@webSocket
                    }

                    val name = call.request.queryParameters["name"] ?: return@webSocket  // will be used later when Player model exists
                    val playerId = nextPlayerId++

                    val player = Player(playerId, name)

                    if (lobby.hostPlayerId == null)
                        lobby.hostPlayerId = playerId

                    clients[playerId] = this
                    lobbyController.join(player)

                    send(
                        playerId = playerId,
                        response = ConnectedResponse(playerId, lobbyController.getPlayerInfos())
                    )

                    broadcastExcept(setOf(playerId), PlayerJoinedEvent(playerId, name))

                    handleConnection(playerId, this)
                }
            }
        }.start(wait = false)
    }

    fun stop() {
        server?.stop(500, 1000)
        server = null
    }

    private suspend fun handleConnection(playerId: Int, session: WebSocketSession) {
        try {
            for (frame in session.incoming) {
                if (frame is Frame.Text) {
                    val message = frame.readText()
                    val request = Json.decodeFromString<Request>(message)

                    val result = requestHandler.handle(playerId, request)

                    result.response?.let { response ->
                        send(playerId, response)
                    }

                    result.event?.let { event ->
                        broadcast(event)
                    }
                }
            }
        } finally {
            // on client disconnect:
            clients.remove(playerId)

            val player = lobbyController.leave(playerId)

            player?.let {
                broadcast(PlayerLeftEvent(it.id, it.name))
            }
        }
    }

    private suspend fun send(
        playerId: Int,
        response: Response
    ) {
        clients[playerId]?.send(Json.encodeToString<Response>(response))
    }

    private suspend fun broadcast(event: Event) {
        val message = Json.encodeToString<Event>(event)

        clients.values.forEach { session ->
            session.send(message)
        }
    }

    private suspend fun broadcastExcept(
        excludedPlayers: Set<Int>,
        event: Event
    ) {
        val message = Json.encodeToString<Event>(event)

        clients.forEach { (playerId, session) ->
            if (playerId !in excludedPlayers) {
                session.send(message)
            }
        }
    }
}