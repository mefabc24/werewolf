package com.mefabc24.werewolf.client.handler

import com.mefabc24.werewolf.client.ClientState
import com.mefabc24.werewolf.network.Event
import com.mefabc24.werewolf.network.GameStartedEvent
import com.mefabc24.werewolf.network.MessageEvent
import com.mefabc24.werewolf.network.PlaceholderEvent
import com.mefabc24.werewolf.network.PlayerJoinedEvent
import com.mefabc24.werewolf.network.PlayerLeftEvent
import com.mefabc24.werewolf.player.PlayerInfo

class EventHandler(
    private val state: ClientState
) {
    fun handle(event: Event) {
        when (event) {
            is PlaceholderEvent -> println("Event received: $event")
            is PlayerJoinedEvent -> {
                state.players.add(PlayerInfo(event.playerId, event.playerName))
                println("Player joined: ${event.playerName} (ID: ${event.playerId})")
            }
            is PlayerLeftEvent -> {
                state.players.remove(PlayerInfo(event.playerId, event.playerName))
                println("Player left: ${event.playerName} (ID: ${event.playerId})")
            }
            is MessageEvent -> {
                val playerName = state.players
                    .find { it.playerId == event.playerId }
                    ?.playerName

                println("$playerName: ${event.message}")
            }
            is GameStartedEvent -> {
                state.gameState = event.gameState
                println("Game started")
            }
        }
    }
}