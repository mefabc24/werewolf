package com.mefabc24.werewolf.network

import com.mefabc24.werewolf.player.PlayerInfo
import kotlinx.serialization.Serializable

@Serializable
sealed interface Response

// Connection and lobby
@Serializable
data object PlaceholderResponse : Response

@Serializable
data class ConnectedResponse(
    val playerId: Int,
    val players: List<PlayerInfo>
) : Response

// Game start
@Serializable
data object GameStartedResponse : Response

// Actions
@Serializable
data object ActionAcceptedResponse : Response

@Serializable
data object VoteAcceptedResponse : Response

// Errors
@Serializable
data class ErrorResponse(val message: String) : Response
