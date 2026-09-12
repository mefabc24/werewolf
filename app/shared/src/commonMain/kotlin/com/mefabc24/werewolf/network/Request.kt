package com.mefabc24.werewolf.network

import kotlinx.serialization.Serializable

@Serializable
sealed interface Request

// General requests
@Serializable
data object PlaceholderRequest : Request

@Serializable
data class MessageRequest(
    val message: String
) : Request

// Game start
@Serializable
data object StartGameRequest : Request

// Day voting
@Serializable
data class VoteRequest(val targetId: Int) : Request

// Night actions
@Serializable
data class WerewolfActionRequest(val targetId: Int?) : Request

@Serializable
data class WitchActionRequest(
    val healTargetId: Int? = null,
    val killTargetId: Int? = null
) : Request

@Serializable
data class SeerActionRequest(val targetId: Int?) : Request

@Serializable
data class HunterActionRequest(val targetId: Int?) : Request
