package com.mefabc24.werewolf

import androidx.compose.ui.window.application
import com.mefabc24.werewolf.client.GameClient
import com.mefabc24.werewolf.network.MessageRequest
import com.mefabc24.werewolf.network.PlaceholderRequest
import kotlinx.coroutines.runBlocking

fun main() = application {
    runBlocking {
        val gameClient = GameClient()

        while (true) {
            val input = readln().trim().split(Regex("\\s+"))

            when (input[0].lowercase()) {
                "connect" -> gameClient.connect(input.getOrNull(1) ?: "Client")
                "host" -> {
                    val gameServer = GameServer()
                    gameServer.start()
                    gameClient.connect("${input.getOrNull(1) ?: "Host Client"} (Host)")
                }
                "test" -> gameClient.send(PlaceholderRequest)
                "echo" -> gameClient.send(MessageRequest(input.drop(1).joinToString(" ")))
                "quit" -> {
                    gameClient.disconnect()
                    break
                }
                "info" -> gameClient.players.forEach { println("${it.playerName} (ID: ${it.playerId})") }
                else -> println("Unknown command")
            }
        }
    }
}