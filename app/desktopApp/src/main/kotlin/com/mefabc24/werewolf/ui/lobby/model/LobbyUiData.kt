package com.mefabc24.werewolf.ui.lobby.model

import org.jetbrains.compose.resources.DrawableResource

/** Presentation data only. Values and ordering are supplied by the caller. */
data class LobbyUiData(
    val info: LobbyInfoUi,
    val players: List<LobbyPlayerUi>,
    val messages: List<ChatMessageUi>,
    val events: List<LobbyEventUi>,
    val settings: List<SettingsSectionUi>,
)

data class LobbyInfoUi(
    val lobbyCode: String,
    val hostName: String,
    val playerCount: Int,
    val maxPlayers: Int,
    val readyCount: Int,
    val startEnabled: Boolean = true,
)

data class LobbyPlayerUi(
    val id: String,
    val name: String,
    val isHost: Boolean = false,
    val isReady: Boolean = false,
    val isOpenSlot: Boolean = false,
)

data class ChatMessageUi(
    val id: String,
    val name: String,
    val message: String,
    val timestamp: String,
)

data class LobbyEventUi(
    val id: String,
    val icon: DrawableResource,
    val text: String,
    val timestamp: String,
)

enum class SettingTone { NEUTRAL, POSITIVE, NEGATIVE }

data class SettingUi(
    val id: String,
    val icon: DrawableResource,
    val label: String,
    val value: String,
    val tone: SettingTone = SettingTone.NEUTRAL,
)

data class SettingsSectionUi(
    val id: String,
    val title: String,
    val settings: List<SettingUi>,
)
