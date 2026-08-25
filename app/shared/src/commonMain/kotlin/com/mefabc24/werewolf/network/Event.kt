package com.mefabc24.werewolf.network

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
data object GameStartedEvent : Event