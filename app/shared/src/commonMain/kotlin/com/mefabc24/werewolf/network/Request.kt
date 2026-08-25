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