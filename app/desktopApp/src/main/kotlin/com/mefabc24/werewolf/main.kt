package com.mefabc24.werewolf

import com.mefabc24.werewolf.client.GameClient
import com.mefabc24.werewolf.game.ClientGameState
import com.mefabc24.werewolf.network.MessageRequest
import com.mefabc24.werewolf.network.PlaceholderRequest
import com.mefabc24.werewolf.network.SeerActionRequest
import com.mefabc24.werewolf.network.StartGameRequest
import com.mefabc24.werewolf.network.VoteRequest
import com.mefabc24.werewolf.network.WerewolfActionRequest
import com.mefabc24.werewolf.network.WitchActionRequest
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val gameClient = GameClient()

    println("Game started. Waiting for commands.")

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

            "info" -> gameClient.gameState.players.forEach {
                println("${it.playerName} (ID: ${it.id})")
            }

            "role" -> print(gameClient.selfPlayer?.role)

            "start" -> gameClient.send(StartGameRequest)

            "vote" -> {
                val targetId = input.getOrNull(1)?.toIntOrNull()

                if (targetId != null) {
                    gameClient.send(VoteRequest(targetId))
                } else {
                    println("Usage: vote <playerId>")
                }
            }

            "wolves" -> {
                val werewolfIds = gameClient.gameState.werewolfIds

                werewolfIds?.forEach { id ->
                    val name = gameClient.gameState.players.find { it.id == id }?.playerName
                    println("Wolf: $name (ID: $id)")
                } ?: println("Werewolf IDs: null")
            }

            "werewolf" -> {
                val targetId = input.getOrNull(1)
                    ?.takeUnless { it == "-" }
                    ?.toIntOrNull()

                if (input.size == 2) {
                    gameClient.send(WerewolfActionRequest(targetId))
                } else {
                    println("Usage: werewolf <targetId>")
                }
            }

            "witch" -> {
                val healTargetId = input.getOrNull(1)
                    ?.takeUnless { it == "-" }
                    ?.toIntOrNull()

                val killTargetId = input.getOrNull(2)
                    ?.takeUnless { it == "-" }
                    ?.toIntOrNull()

                if (input.size >= 3) {
                    gameClient.send(
                        WitchActionRequest(
                            healTargetId = healTargetId,
                            killTargetId = killTargetId
                        )
                    )
                } else {
                    println("Usage: witch <healId|-> <killId|->")
                }
            }

            "seer" -> {
                val targetId = input.getOrNull(1)
                    ?.takeUnless { it == "-" }
                    ?.toIntOrNull()

                if (input.size == 2) gameClient.send(SeerActionRequest(targetId))
                else println("Usage: seer <targetId>")

            }

            "state" -> {
                printGameState(gameClient.gameState)
            }

            "quit" -> {
                gameClient.disconnect()
                break
            }

            else -> println("Unknown command")
        }
    }
}

private fun printGameState(gameState: ClientGameState) {
    val reset = "\u001B[0m"
    val bold = "\u001B[1m"

    val gray = "\u001B[90m"
    val red = "\u001B[31m"
    val orange = "\u001B[38;5;208m"
    val green = "\u001B[32m"
    val yellow = "\u001B[33m"
    val blue = "\u001B[34m"
    val purple = "\u001B[35m"
    val cyan = "\u001B[36m"

    println()
    println("$gray============================================================$reset")
    println("$bold$cyan CLIENT GAME STATE$reset")
    println("$gray============================================================$reset")

    println("Game Phase : $yellow${gameState.gamePhase}$reset")
    println("Night Phase: $blue${gameState.nightPhase ?: "-"}$reset")
    println("Day Phase  : $yellow${gameState.dayPhase ?: "-"}$reset")
    println("Round      : $cyan${gameState.round}$reset")

    println()
    println("$bold${cyan}Players:$reset")

    gameState.players.forEach { player ->
        val aliveText =
            if (player.isAlive) "${green}Alive$reset"
            else "${red}Dead$reset"

        val votedText =
            if (player.id in gameState.hasVoted) "${green}Yes$reset"
            else "${gray}No$reset"

        val roleText = when (player.role?.name) {
            "Werewolf" -> "$red${player.role}$reset"
            "Witch" -> "$purple${player.role}$reset"
            "Seer" -> "$blue${player.role}$reset"
            "Mayor" -> "$orange${player.role}$reset"
            "Villager" -> "${gray}${player.role}$reset"
            else -> "${gray}Unknown$reset"
        }

        println(
            "  $cyan[${player.id}]$reset ${player.playerName}" +
                    " | Role: $roleText" +
                    " | $aliveText" +
                    " | Voted: $votedText"
        )
    }

    println()

    gameState.werewolfIds?.let {
        val names = it.map { ids ->
            gameState.players
                .find { it.id == ids }
                ?.playerName
                ?: "Unknown"
        }

        println("Werewolves: $red${names.joinToString(", ")}$reset")
    }

    val votingTime =
        if (gameState.settings.voteTimeSeconds == -1) "Infinite"
        else "${gameState.settings.voteTimeSeconds}s"

    val discussionTime =
        if (gameState.settings.discussionTimeSeconds == -1) "Infinite"
        else "${gameState.settings.discussionTimeSeconds}s"

    val nightActionTime =
        if (gameState.settings.nightRoleActingTimeSeconds == -1) "Infinite"
        else "${gameState.settings.nightRoleActingTimeSeconds}s"

    println()
    println("$bold${cyan}Settings:$reset")
    println("  Werewolves        : $red${gameState.settings.werewolfAmount}$reset")
    println("  Discussion time   : $yellow$discussionTime$reset")
    println("  Vote time         : $yellow$votingTime$reset")
    println("  Night action time : $blue$nightActionTime$reset")
    println("  Optional roles    : $purple${gameState.settings.optionalRoles}$reset")

    println("$gray============================================================$reset")
    println()
}