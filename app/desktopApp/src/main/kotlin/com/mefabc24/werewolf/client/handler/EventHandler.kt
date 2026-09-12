package com.mefabc24.werewolf.client.handler

import com.mefabc24.werewolf.client.ClientState
import com.mefabc24.werewolf.game.DayPhase
import com.mefabc24.werewolf.game.DeathCause
import com.mefabc24.werewolf.game.GamePhase
import com.mefabc24.werewolf.network.*
import com.mefabc24.werewolf.player.PlayerInfo
import com.mefabc24.werewolf.player.role.Team

class EventHandler(
    private val clientState: ClientState
) {
    fun handle(event: Event) {
        when (event) {
            is PlaceholderEvent -> println("Event received: $event")

            is PlayerJoinedEvent -> {
                clientState.gameState = clientState.gameState.copy(
                    players = clientState.gameState.players + PlayerInfo(
                        event.playerId,
                        event.playerName
                    )
                )
                println("Player joined: ${event.playerName} (ID: ${event.playerId})")
            }

            is PlayerLeftEvent -> {
                clientState.gameState = clientState.gameState.copy(
                    players = clientState.gameState.players.filter {
                        it.id != event.playerId
                    }
                )
                println("Player left: ${event.playerName} (ID: ${event.playerId})")
            }

            is MessageEvent -> {
                val playerName = clientState.gameState.players
                    .find { it.id == event.playerId }
                    ?.playerName

                println("$playerName: ${event.message}")
            }

            is GameStartedEvent -> {
                clientState.gameState = event.gameState
                println("Game started. Your role: ${clientState.selfPlayer?.role}")
            }

            is NightStartedEvent -> {
                clientState.gameState = clientState.gameState.copy(
                    gamePhase = GamePhase.NIGHT,
                    nightPhase = null,
                    dayPhase = null
                )

                println("Night falls over the village.")
                println("The streets grow quiet and the last lights fade.")
                println("All villagers close their eyes.")
                println("But while the village sleeps, some awaken in the darkness...")
            }

            is NightPhaseStartedEvent -> {
                clientState.gameState = clientState.gameState.copy(
                    gamePhase = GamePhase.NIGHT,
                    nightPhase = event.phase
                )

                println("Night phase started: ${event.phase}")
            }

            is DayPhaseStartedEvent -> {
                clientState.gameState = clientState.gameState.copy(
                    gamePhase = GamePhase.DAY,
                    dayPhase = event.phase,
                    hasVoted = if (event.phase == DayPhase.VOTING) {
                        mutableSetOf()
                    } else clientState.gameState.hasVoted
                )

                println("Day phase started: ${event.phase}")
            }

            is WerewolfTurnEvent -> println("It's your turn during the night. You can now perform your action.")

            is WitchTurnEvent -> {
                val attackedPlayers = clientState.gameState.players
                    .filter { it.id in event.attackedPlayerIds }

                println(
                    "Werewolf target(s): " +
                            attackedPlayers.joinToString { "${it.playerName} (${it.id})" }
                )
            }

            is SeerTurnEvent -> println("It's seer turn. You can now perform your action.")

            is PlayerVotedEvent -> {
                clientState.gameState = clientState.gameState.copy(
                    hasVoted = (clientState.gameState.hasVoted + event.playerId).toMutableSet()
                )

                val playerName = clientState.gameState.players
                    .find { it.id == event.playerId }
                    ?.playerName
                    ?: "Unknown player"

                println("$playerName has voted.")
            }

            is VotingFinishedEvent -> {
                val playerId = event.killedPlayerId

                clientState.gameState = clientState.gameState.copy(
                    players = clientState.gameState.players.map { player ->
                        if (player.id == playerId) {
                            player.copy(isAlive = false)
                        } else {
                            player
                        }
                    },
                    hasVoted = mutableSetOf()
                )

                if (playerId != null) {
                    val playerName = clientState.gameState.players
                        .find { it.id == playerId }
                        ?.playerName

                    println("Player was voted out: $playerName (ID: $playerId)")
                } else {
                    println("Voting finished without a decision.")
                }
            }

            is NightEndedEvent -> {
                clientState.gameState = clientState.gameState.copy(
                    players = clientState.gameState.players.map { player ->
                        if (player.id in event.killedPlayerIds) {
                            player.copy(isAlive = false)
                        } else {
                            player
                        }
                    },
                    nightPhase = null
                )

                if (event.killedPlayerIds.isEmpty()) {
                    println("Night ended. Nobody died.")
                }

                event.killedPlayerIds.forEach { playerId ->
                    val playerName = clientState.gameState.players
                        .find { it.id == playerId }
                        ?.playerName

                    println("Player was killed during the night: $playerName (ID: $playerId)")
                }
            }

            is DayStartedEvent -> {
                clientState.gameState = clientState.gameState.copy(
                    gamePhase = GamePhase.DAY,
                    nightPhase = null,
                    dayPhase = null
                )
                println("Day started.")
            }

            is RoundStartedEvent -> {
                clientState.gameState = clientState.gameState.copy(
                    round = event.round
                )

                println("Round ${event.round} has started.")
            }

            is YouDiedEvent -> {
                when(event.cause) {
                    DeathCause.NIGHT -> println("You have died during the night. You can no longer participate in the game.")
                    DeathCause.VOTED_OUT -> println("You have been voted out. You can no longer participate in the game.")
                }
            }

            is RoleRevealEvent -> {
                val playerName = clientState.gameState.players.find { it.id == event.playerId }?.playerName
                println("Player $playerName's role is ${event.role}.")

                clientState.gameState = clientState.gameState.copy(
                    players = clientState.gameState.players.map { player ->
                        if (player.id == event.playerId) {
                            player.copy(role = event.role)
                        } else {
                            player
                        }
                    }
                )
            }

            is GameWonEvent -> {
                when (event.winningTeam) {
                    Team.VILLAGE -> println("The villagers have won the game!")
                    Team.WEREWOLVES -> println("The werewolves have won the game!")
                }
            }

            is GameEndedEvent -> {
                clientState.gameState = clientState.gameState.copy(
                    gamePhase = GamePhase.END,
                    nightPhase = null,
                    dayPhase = null
                )

                println("Game ended.")
            }
        }
    }
}