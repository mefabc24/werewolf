package com.mefabc24.werewolf.ui.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mefabc24.werewolf.ui.lobby.LobbyStyle

@Composable
fun GameScreenLayout(
    title: String,
    centerHeaderContent: (@Composable () -> Unit)? = null,
    onSettingsClick: () -> Unit,
    sidebarContent: @Composable () -> Unit,
    mainContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    showSidebar: Boolean = true,
) {
    Box(modifier) {
        AppShell {
            Column(Modifier.fillMaxSize()) {
                AppHeader(
                    title = title,
                    centerContent = centerHeaderContent,
                    onSettingsClick = onSettingsClick,
                )
                HorizontalDivider(color = LobbyStyle.Border)
                Row(Modifier.fillMaxWidth().weight(1f)) {
                    if (showSidebar) {
                        Box(Modifier.weight(0.30f).fillMaxHeight()) {
                            sidebarContent()
                        }
                        VerticalDivider(color = LobbyStyle.Border)
                    }
                    Box(
                        Modifier.weight(if (showSidebar) 0.70f else 1f)
                            .fillMaxHeight().background(LobbyStyle.Main).padding(16.dp),
                    ) {
                        mainContent()
                    }
                }
            }
        }
    }
}
