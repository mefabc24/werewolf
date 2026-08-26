package com.mefabc24.werewolf.network

import com.mefabc24.werewolf.game.ClientGameState
import kotlinx.serialization.Serializable

@Serializable
sealed interface Event

// Placeholder
@Serializable
data object PlaceholderEvent : Event

@Serializable
data class PlayerJoinedEvent(
    val playerId: Int,
    val playerName: String
) : Event

@Serializable
data class PlayerLeftEvent(
    val playerId: Int,
    val playerName: String
) : Event

@Serializable
data class MessageEvent(
    val playerId: Int,
    val message: String
) : Event

@Serializable
data class GameStartedEvent(val gameState: ClientGameState) : Event