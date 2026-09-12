package com.mefabc24.werewolf.network

import kotlinx.serialization.Serializable

@Serializable
sealed interface Request

// Placeholder
@Serializable
data object PlaceholderRequest : Request

@Serializable
data class MessageRequest(
    val message: String
) : Request

@Serializable
data object StartGameRequest : Request

@Serializable
data class VoteRequest(val targetId: Int) : Request

@Serializable
data class WerewolfActionRequest(val targetId: Int?) : Request

@Serializable
data class WitchActionRequest(
    val healTargetId: Int? = null,
    val killTargetId: Int? = null
) : Request

@Serializable
data class SeerActionRequest(val targetId: Int?) : Request