package com.mefabc24.werewolf.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.mefabc24.werewolf.resources.Res
import com.mefabc24.werewolf.resources.lobby
import com.mefabc24.werewolf.resources.lobby_sidebar_bg
import com.mefabc24.werewolf.resources.settings
import com.mefabc24.werewolf.ui.components.*
import org.jetbrains.compose.resources.painterResource

@Composable
fun LobbyScreen() {
    GameScreenLayout(
        title = "Lobby",
        onSettingsClick = { },

        sidebarContent = {
            Image(
                painter = painterResource(Res.drawable.lobby_sidebar_bg),
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )
        },

        mainContent = {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LobbyInfoIsland(
                    lobbyCode = "C8F9ACB5",
                    hostName = "Fabi",
                    playerCount = 6,
                    maxPlayers = 8,
                    readyCount = 3,
                    onStartClick = { }
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SectionIsland(
                        icon = painterResource(Res.drawable.settings),
                        title = "Spielerliste",
                        description = "Spieler in der aktuellen Lobby",
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PlayerCard(
                                name = "Fabi",
                                icon = painterResource(Res.drawable.lobby),
                                isHost = true,
                                isReady = true
                            )

                            PlayerCard(
                                name = "Melly",
                                icon = painterResource(Res.drawable.lobby),
                                isReady = false
                            )

                            PlayerCard(
                                name = "Sven",
                                icon = painterResource(Res.drawable.lobby),
                                isReady = true
                            )

                            PlayerCard(
                                name = "Offener Platz",
                                icon = painterResource(Res.drawable.lobby),
                                isOpenSlot = true
                            )
                        }
                    }

                    TabbedIsland(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) { selectedTab ->

                        when (selectedTab) {
                            LobbyTab.CHAT -> {
                                LobbyChatContent()
                            }

                            LobbyTab.EVENTS -> {
                                LobbyEventContent()
                            }

                            LobbyTab.SETTINGS -> {
                                LobbySettingsContent()
                            }
                        }
                    }
                }
            }
        }
    )
}