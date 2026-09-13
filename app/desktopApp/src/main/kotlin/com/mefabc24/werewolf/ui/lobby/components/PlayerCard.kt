package com.mefabc24.werewolf.ui.lobby.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mefabc24.werewolf.resources.*
import com.mefabc24.werewolf.ui.common.components.SectionIsland
import com.mefabc24.werewolf.ui.lobby.LobbyIcon
import com.mefabc24.werewolf.ui.lobby.LobbyScrollArea
import com.mefabc24.werewolf.ui.lobby.LobbyStyle
import com.mefabc24.werewolf.ui.lobby.lobbyRow
import com.mefabc24.werewolf.ui.lobby.model.LobbyPlayerUi
import org.jetbrains.compose.resources.painterResource

@Composable
fun PlayerListIsland(
    players: List<LobbyPlayerUi>,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    title: String = "Spielerliste",
    description: String = "Spieler in der aktuellen Lobby",
) {
    SectionIsland(
        icon = painterResource(Res.drawable.users),
        title = title,
        description = description,
        modifier = modifier,
    ) {
        LobbyScrollArea(
            listState,
            Modifier.fillMaxWidth().weight(1f)
        ) { listModifier ->
            LazyColumn(
                state = listState,
                modifier = listModifier,
                verticalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(bottom = 2.dp),
            ) {
                items(players, key = { it.id }) { player ->
                    PlayerCard(
                        name = player.name,
                        isReady = player.isReady,
                        isHost = player.isHost,
                        isOpenSlot = player.isOpenSlot,
                    )
                }
            }
        }
    }
}

@Composable
fun PlayerCard(
    name: String,
    isReady: Boolean = false,
    isHost: Boolean = false,
    isOpenSlot: Boolean = false,
    modifier: Modifier = Modifier,
    icon: Painter = painterResource(
        when {
            isOpenSlot -> Res.drawable.open_space
            isHost -> Res.drawable.crown
            else -> Res.drawable.user
        },
    ),
) {
    Row(
        modifier = modifier.fillMaxWidth().heightIn(min = 48.dp).lobbyRow()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (isOpenSlot) LobbyStyle.Muted else LobbyStyle.Text,
            modifier = Modifier.size(20.dp),
        )
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = name,
                modifier = Modifier.weight(1f, fill = false),
                color = if (isOpenSlot) LobbyStyle.Muted else LobbyStyle.Text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (isHost && !isOpenSlot) {
                Text(
                    text = "Host",
                    color = LobbyStyle.Text,
                    fontSize = 10.sp,
                    lineHeight = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.background(LobbyStyle.Red, RoundedCornerShape(3.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                )
            }
        }
        PlayerStatus(isReady = isReady, isOpenSlot = isOpenSlot)
    }
}

@Composable
fun PlayerStatus(
    isReady: Boolean,
    isOpenSlot: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val color = when {
        isOpenSlot -> LobbyStyle.Muted
        isReady -> LobbyStyle.Ready
        else -> LobbyStyle.NotReady
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        LobbyIcon(
            resource = if (isReady && !isOpenSlot) Res.drawable.circle_check else Res.drawable.circle,
            modifier = Modifier.size(13.dp),
            tint = color,
        )
        Text(
            text = when {
                isOpenSlot -> "Warten"
                isReady -> "Bereit"
                else -> "Nicht bereit"
            },
            color = color,
            fontSize = 11.sp,
            maxLines = 1,
        )
    }
}
