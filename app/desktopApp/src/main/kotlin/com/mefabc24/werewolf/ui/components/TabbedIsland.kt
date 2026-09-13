package com.mefabc24.werewolf.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TabbedIsland(
    modifier: Modifier = Modifier,
    content: @Composable (LobbyTab) -> Unit
) {
    var selectedTab by remember {
        mutableStateOf(LobbyTab.CHAT)
    }

    val shape = RoundedCornerShape(16.dp)

    Column(
        modifier = modifier
            .background(Color(0xFF0B1624), shape)
            .border(
                1.dp,
                Color(0xFF273240),
                shape
            )
            .padding(16.dp)
    ) {
        LobbyTabBar(
            selectedTab = selectedTab,
            onTabSelected = {
                selectedTab = it
            }
        )

        Spacer(Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            content(selectedTab)
        }
    }
}

@Composable
private fun LobbyTabBar(
    selectedTab: LobbyTab,
    onTabSelected: (LobbyTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .border(
                1.dp,
                Color(0xFF273240),
                RoundedCornerShape(10.dp)
            )
            .padding(4.dp)
    ) {
        LobbyTab.entries.forEach { tab ->
            val selected = tab == selectedTab

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(
                        color = if (selected)
                            Color(0xFF182433)
                        else
                            Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable {
                        onTabSelected(tab)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tab.title,
                    color = if (selected)
                        Color.White
                    else
                        Color.White.copy(alpha = 0.45f),
                    fontSize = 16.sp,
                    fontWeight = if (selected)
                        FontWeight.SemiBold
                    else
                        FontWeight.Normal
                )
            }
        }
    }
}