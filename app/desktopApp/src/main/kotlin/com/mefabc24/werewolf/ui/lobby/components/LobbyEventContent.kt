package com.mefabc24.werewolf.ui.lobby.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mefabc24.werewolf.resources.Res
import com.mefabc24.werewolf.resources.filter
import com.mefabc24.werewolf.ui.lobby.DropdownArrow
import com.mefabc24.werewolf.ui.lobby.LobbyIcon
import com.mefabc24.werewolf.ui.lobby.LobbyScrollArea
import com.mefabc24.werewolf.ui.lobby.LobbyStyle
import com.mefabc24.werewolf.ui.lobby.lobbyRow
import com.mefabc24.werewolf.ui.lobby.model.LobbyEventUi
import org.jetbrains.compose.resources.DrawableResource

@Composable
fun LobbyEventContent(
    events: List<LobbyEventUi>,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    filterLabel: String = "Alle Ereignisse",
) {
    Column(modifier.fillMaxSize()) {
        EventFilter(label = filterLabel)
        Spacer(Modifier.height(12.dp))
        LobbyScrollArea(
            listState,
            Modifier.fillMaxWidth().weight(1f)
        ) { listModifier ->
            LazyColumn(
                modifier = listModifier,
                state = listState,
                verticalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(bottom = 2.dp),
            ) {
                // Preserve caller ordering: oldest first, newest last.
                items(events, key = { "event:${it.id}" }) { event ->
                    EventRow(icon = event.icon, text = event.text, timestamp = event.timestamp)
                }
                item(key = "history-end") {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        HorizontalDivider(
                            Modifier.weight(1f),
                            color = LobbyStyle.Border
                        )
                        Text(
                            "Ende des Verlaufs",
                            color = LobbyStyle.Muted,
                            fontSize = 11.sp
                        )
                        HorizontalDivider(
                            Modifier.weight(1f),
                            color = LobbyStyle.Border
                        )
                    }
                }
            }
        }
    }
}

/** Visual placeholder only; no filtering or menu state is implemented. */
@Composable
fun EventFilter(label: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.heightIn(min = 24.dp).padding(horizontal = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LobbyIcon(
            Res.drawable.filter,
            Modifier.size(16.dp),
            tint = LobbyStyle.Secondary
        )
        Text(label, color = LobbyStyle.Secondary, fontSize = 13.sp)
        DropdownArrow()
    }
}

@Composable
fun EventRow(
    icon: DrawableResource,
    text: String,
    timestamp: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().heightIn(min = 40.dp).lobbyRow()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        LobbyIcon(icon, Modifier.size(18.dp))
        Text(
            text,
            modifier = Modifier.weight(1f),
            color = LobbyStyle.Secondary,
            fontSize = 13.sp,
            lineHeight = 19.sp,
        )
        Text(timestamp, color = LobbyStyle.Secondary, fontSize = 11.sp, lineHeight = 19.sp, maxLines = 1)
    }
}
