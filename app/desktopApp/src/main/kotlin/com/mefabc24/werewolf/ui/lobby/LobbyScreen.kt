package com.mefabc24.werewolf.ui.lobby

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.mefabc24.werewolf.resources.Res
import com.mefabc24.werewolf.resources.lobby_sidebar_bg
import com.mefabc24.werewolf.ui.common.components.GameScreenLayout
import com.mefabc24.werewolf.ui.lobby.components.LobbyChatContent
import com.mefabc24.werewolf.ui.lobby.components.LobbyEventContent
import com.mefabc24.werewolf.ui.lobby.components.LobbyInfoIsland
import com.mefabc24.werewolf.ui.lobby.components.LobbySettingsContent
import com.mefabc24.werewolf.ui.lobby.components.PlayerListIsland
import com.mefabc24.werewolf.ui.lobby.components.TabbedIsland
import com.mefabc24.werewolf.ui.lobby.model.LobbyTab
import com.mefabc24.werewolf.ui.lobby.model.LobbyUiData
import com.mefabc24.werewolf.ui.lobby.preview.LobbyPreviewData
import org.jetbrains.compose.resources.painterResource

/** Standalone placeholder entry point. Only transient interaction state is kept here. */
@Composable
fun LobbyScreen(
    modifier: Modifier = Modifier,
    data: LobbyUiData = LobbyPreviewData.lobby,
    initialTab: LobbyTab = LobbyTab.CHAT,
    onCopyCode: () -> Unit = {},
    onStartClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onEditSettingsClick: () -> Unit = {},
    onSend: (String) -> Unit = {},
) {
    var selectedTab by remember { mutableStateOf(initialTab) }
    var chatDraft by remember { mutableStateOf("") }
    val playerListState = rememberLazyListState()
    val chatListState = rememberLazyListState()
    val eventListState = rememberLazyListState()
    val settingsListState = rememberLazyListState()

    LobbyScreenContent(
        data = data,
        selectedTab = selectedTab,
        onTabSelected = { selectedTab = it },
        chatDraft = chatDraft,
        onChatDraftChange = { chatDraft = it },
        modifier = modifier,
        onCopyCode = onCopyCode,
        onStartClick = onStartClick,
        onSettingsClick = onSettingsClick,
        onEditSettingsClick = onEditSettingsClick,
        onSend = onSend,
        playerListState = playerListState,
        chatListState = chatListState,
        eventListState = eventListState,
        settingsListState = settingsListState,
    )
}

/** Data and actions can be supplied by a future ViewModel without changing the components. */
@Composable
fun LobbyScreenContent(
    data: LobbyUiData,
    selectedTab: LobbyTab,
    onTabSelected: (LobbyTab) -> Unit,
    chatDraft: String,
    onChatDraftChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onCopyCode: () -> Unit = {},
    onStartClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onEditSettingsClick: () -> Unit = {},
    onSend: (String) -> Unit = {},
    playerListState: LazyListState = rememberLazyListState(),
    chatListState: LazyListState = rememberLazyListState(),
    eventListState: LazyListState = rememberLazyListState(),
    settingsListState: LazyListState = rememberLazyListState(),
) {
    BoxWithConstraints(modifier.fillMaxSize()) {
        val showArtwork = maxWidth >= 1200.dp
        GameScreenLayout(
            title = "Lobby",
            onSettingsClick = onSettingsClick,
            showSidebar = showArtwork,
            sidebarContent = {
                Image(
                    painter = painterResource(Res.drawable.lobby_sidebar_bg),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.TopCenter,
                )
            },
            mainContent = {
                Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    LobbyInfoIsland(
                        lobbyCode = data.info.lobbyCode,
                        hostName = data.info.hostName,
                        playerCount = data.info.playerCount,
                        maxPlayers = data.info.maxPlayers,
                        readyCount = data.info.readyCount,
                        onStartClick = onStartClick,
                        onCopyClick = onCopyCode,
                        startEnabled = data.info.startEnabled,
                    )
                    BoxWithConstraints(Modifier.fillMaxWidth().weight(1f)) {
                        val playerIsland: @Composable (Modifier) -> Unit = { islandModifier ->
                            PlayerListIsland(data.players, islandModifier, listState = playerListState)
                        }
                        val detailsIsland: @Composable (Modifier) -> Unit = { islandModifier ->
                            TabbedIsland(selectedTab, onTabSelected, islandModifier) { tab ->
                                when (tab) {
                                    LobbyTab.CHAT -> LobbyChatContent(
                                        messages = data.messages,
                                        draft = chatDraft,
                                        onDraftChange = onChatDraftChange,
                                        onSend = onSend,
                                        listState = chatListState,
                                    )

                                    LobbyTab.EVENTS -> LobbyEventContent(
                                        events = data.events,
                                        listState = eventListState,
                                    )

                                    LobbyTab.SETTINGS -> LobbySettingsContent(
                                        sections = data.settings,
                                        onEditClick = onEditSettingsClick,
                                        listState = settingsListState,
                                    )
                                }
                            }
                        }
                        if (maxWidth >= 700.dp) {
                            Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                                playerIsland(Modifier.weight(0.47f).fillMaxHeight())
                                detailsIsland(Modifier.weight(0.53f).fillMaxHeight())
                            }
                        } else {
                            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                playerIsland(Modifier.weight(0.4f).fillMaxWidth())
                                detailsIsland(Modifier.weight(0.6f).fillMaxWidth())
                            }
                        }
                    }
                }
            },
        )
    }
}
