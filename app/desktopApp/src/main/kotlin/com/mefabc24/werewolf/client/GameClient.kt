package com.mefabc24.werewolf.client

import com.mefabc24.werewolf.client.handler.EventHandler
import com.mefabc24.werewolf.client.handler.ResponseHandler
import com.mefabc24.werewolf.network.*
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
    private val state = ClientState()

    private var session: DefaultClientWebSocketSession? = null
    private var receiveJob: Job? = null

    private val eventHandler = EventHandler(state)
    private val responseHandler = ResponseHandler(state)

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
            responseHandler.handle(response)
            return
        } catch (_: Exception) {}

        try {
            val event = Json.decodeFromString<Event>(message)
            eventHandler.handle(event)
            return
        } catch (_: Exception) {}

    }
}