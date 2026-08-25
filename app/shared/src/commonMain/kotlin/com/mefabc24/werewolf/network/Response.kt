package com.mefabc24.werewolf.network

import com.mefabc24.werewolf.player.PlayerInfo
import kotlinx.serialization.Serializable

@Serializable
sealed interface Response

// Placeholder
@Serializable
data object PlaceholderResponse : Response

@Serializable
data class ConnectedResponse(
    val playerId: Int,
    val players: List<PlayerInfo>
) : Response