package com.mefabc24.werewolf

import com.mefabc24.werewolf.client.GameClient
import com.mefabc24.werewolf.network.MessageRequest
import com.mefabc24.werewolf.network.PlaceholderRequest
import com.mefabc24.werewolf.network.StartGameRequest
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
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

            "echo" -> gameClient.send(
                MessageRequest(input.drop(1).joinToString(" "))
            )

            "info" -> gameClient.players.forEach {
                println("${it.playerName} (ID: ${it.playerId})")
            }

            "start" -> gameClient.send(StartGameRequest)

            "quit" -> {
                gameClient.disconnect()
                break
            }

            else -> println("Unknown command")
        }
    }
}