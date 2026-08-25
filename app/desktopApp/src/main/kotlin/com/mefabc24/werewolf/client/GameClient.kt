package com.mefabc24.werewolf.client

import com.mefabc24.werewolf.network.ConnectedResponse
import com.mefabc24.werewolf.network.Event
import com.mefabc24.werewolf.network.MessageEvent
import com.mefabc24.werewolf.network.PlaceholderEvent
import com.mefabc24.werewolf.network.PlaceholderResponse
import com.mefabc24.werewolf.network.PlayerJoinedEvent
import com.mefabc24.werewolf.network.PlayerLeftEvent
import com.mefabc24.werewolf.network.Request
import com.mefabc24.werewolf.network.Response
import com.mefabc24.werewolf.player.PlayerInfo
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.Job
import kotlinx.serialization.json.Json
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import io.ktor.http.*

class GameClient {
    private var session: DefaultClientWebSocketSession? = null
    private var receiveJob: Job? = null

    val players: List<PlayerInfo>
        field: MutableList<PlayerInfo> = mutableListOf()

    private var playerId: Int? = null

    private val client = HttpClient(CIO) {
        install(WebSockets)
    }

    suspend fun connect(name: String) {
        session = client.webSocketSession {
            url {
                host = "localhost"
                port = 8080
                path("/werewolf")
                parameters.append("name", name)
            }
        }


        receiveJob = CoroutineScope(Dispatchers.Default).launch {
            receiveMessage()
        }
    }

    suspend fun disconnect() {
        session?.close()
        session = null

        receiveJob?.cancel()
        receiveJob = null
    }

    suspend fun send(request: Request) {
        val currentSession = session ?: return

        val message = Json.encodeToString<Request>(request)
        currentSession.send(message)
    }

    private suspend fun receiveMessage() {
        val currentSession = session ?: return

        try {
            for (frame in currentSession.incoming) {
                if (frame is Frame.Text) {
                    val message = frame.readText()
                    handleMessage(message)
                }
            }
        } finally {
            session = null
            println("Connection lost - lobby closed")
        }
    }

    private fun handleMessage(message: String) {
        try {
            val response = Json.decodeFromString<Response>(message)
            handleResponse(response)
            return
        } catch (_: Exception) {}

        try {
            val event = Json.decodeFromString<Event>(message)
            handleEvent(event)
            return
        } catch (_: Exception) {}

    }

    private fun handleResponse(response: Response) {
        when (response) {
            is PlaceholderResponse -> println("Response received: $response")
            is ConnectedResponse -> {
                playerId = response.playerId

                players.clear()
                players.addAll(response.players)

                val playerName = players
                    .find { it.playerId == playerId }
                    ?.playerName

                println("Connected as $playerName (ID: ${response.playerId})")
            }
        }
    }

    private fun handleEvent(event: Event) {
        when (event) {
            is PlaceholderEvent -> println("Event received: $event")
            is PlayerJoinedEvent -> {
                players.add(PlayerInfo(event.playerId, event.playerName))
                println("Player joined: ${event.playerName} (ID: ${event.playerId})")
            }
            is PlayerLeftEvent -> {
                players.remove(PlayerInfo(event.playerId, event.playerName))
                println("Player left: ${event.playerName} (ID: ${event.playerId})")
            }
            is MessageEvent -> {
                val playerName = players
                    .find { it.playerId == event.playerId }
                    ?.playerName

                println("$playerName: ${event.message}")
            }
        }
    }
}