package com.mefabc24.werewolf.ui.lobby.preview

import androidx.compose.runtime.Composable
import com.mefabc24.werewolf.ui.lobby.model.LobbyTab
import com.mefabc24.werewolf.ui.lobby.LobbyScreen
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun LobbyChatPreview() {
    LobbyScreen(initialTab = LobbyTab.CHAT)
}

@Preview
@Composable
fun LobbyEventsPreview() {
    LobbyScreen(initialTab = LobbyTab.EVENTS)
}

@Preview
@Composable
fun LobbySettingsPreview() {
    LobbyScreen(initialTab = LobbyTab.SETTINGS)
}
