package com.mefabc24.werewolf.ui.lobby.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mefabc24.werewolf.ui.lobby.Island
import com.mefabc24.werewolf.ui.lobby.LobbyStyle
import com.mefabc24.werewolf.ui.lobby.model.LobbyTab

@Composable
fun TabbedIsland(
    selectedTab: LobbyTab,
    onTabSelected: (LobbyTab) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (LobbyTab) -> Unit,
) {
    Island(modifier) {
        LobbyTabs(selectedTab = selectedTab, onTabSelected = onTabSelected)
        Spacer(Modifier.height(14.dp))
        Box(Modifier.fillMaxWidth().weight(1f)) {
            content(selectedTab)
        }
    }
}

@Composable
fun LobbyTabs(
    selectedTab: LobbyTab,
    onTabSelected: (LobbyTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().height(44.dp)
            .background(LobbyStyle.TabTrack, RoundedCornerShape(8.dp))
            .border(1.dp, LobbyStyle.Border, RoundedCornerShape(8.dp))
            .padding(4.dp).selectableGroup(),
    ) {
        LobbyTab.entries.forEach { tab ->
            val selected = selectedTab == tab
            Box(
                modifier = Modifier.weight(1f).fillMaxHeight()
                    .clip(RoundedCornerShape(5.dp))
                    .background(if (selected) LobbyStyle.SelectedTab else Color.Transparent)
                    .selectable(
                        selected = selected,
                        role = Role.Tab,
                        onClick = { onTabSelected(tab) },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = tab.title,
                    color = if (selected) LobbyStyle.Text else LobbyStyle.Secondary,
                    fontSize = 13.sp,
                    fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                    maxLines = 1,
                )
            }
        }
    }
}
