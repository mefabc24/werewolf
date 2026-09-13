package com.mefabc24.werewolf.ui.lobby.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mefabc24.werewolf.resources.*
import com.mefabc24.werewolf.ui.lobby.Island
import com.mefabc24.werewolf.ui.lobby.LobbyIcon
import com.mefabc24.werewolf.ui.lobby.LobbyOutlineButton
import com.mefabc24.werewolf.ui.lobby.LobbyStyle
import com.mefabc24.werewolf.ui.lobby.PlayIcon

@Composable
fun LobbyInfoIsland(
    lobbyCode: String,
    hostName: String,
    playerCount: Int,
    maxPlayers: Int,
    readyCount: Int,
    onStartClick: () -> Unit,
    modifier: Modifier = Modifier,
    onCopyClick: () -> Unit = {},
    startEnabled: Boolean = true,
) {
    Island(modifier) {
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val singleRow = maxWidth >= 900.dp
            val twoByTwo = maxWidth < 600.dp
            val startButton: @Composable () -> Unit = {
                LobbyOutlineButton(
                    text = "Spiel starten",
                    onClick = onStartClick,
                    enabled = startEnabled,
                    accent = LobbyStyle.Red,
                    borderColor = LobbyStyle.Red.copy(alpha = 0.7f),
                    icon = { PlayIcon(tint = if (startEnabled) LobbyStyle.Red else LobbyStyle.Muted) },
                )
            }
            val code: @Composable (Modifier) -> Unit = { itemModifier ->
                LobbyInfoItem(
                    value = lobbyCode,
                    label = "Lobby-Code",
                    modifier = itemModifier,
                    trailing = {
                        IconButton(onClick = onCopyClick, modifier = Modifier.size(32.dp)) {
                            LobbyIcon(
                                Res.drawable.copy,
                                Modifier.size(17.dp),
                                description = "Lobby-Code kopieren"
                            )
                        }
                    },
                )
            }
            val host: @Composable (Modifier) -> Unit = { itemModifier ->
                LobbyInfoItem(
                    value = hostName,
                    label = "Host",
                    modifier = itemModifier,
                    icon = { LobbyIcon(Res.drawable.crown) },
                )
            }
            val players: @Composable (Modifier) -> Unit = { itemModifier ->
                LobbyInfoItem(
                    value = "$playerCount / $maxPlayers",
                    label = "Spieler",
                    modifier = itemModifier,
                    icon = { LobbyIcon(Res.drawable.user) },
                )
            }
            val readiness: @Composable (Modifier) -> Unit = { itemModifier ->
                LobbyInfoItem(
                    value = "$readyCount / $playerCount",
                    label = "Bereitschaft",
                    modifier = itemModifier,
                    icon = { LobbyIcon(Res.drawable.wobbly_checkmark) },
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    code(Modifier.weight(if (twoByTwo) 1f else 1.45f))
                    InfoDivider()
                    host(Modifier.weight(1f))
                    if (!twoByTwo) {
                        InfoDivider()
                        players(Modifier.weight(1f))
                        InfoDivider()
                        readiness(Modifier.weight(1.1f))
                    }
                    if (singleRow) {
                        InfoDivider()
                        Spacer(Modifier.width(14.dp))
                        startButton()
                    }
                }
                if (twoByTwo) {
                    HorizontalDivider(color = LobbyStyle.Border)
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        players(Modifier.weight(1f))
                        InfoDivider()
                        readiness(Modifier.weight(1f))
                    }
                }
                if (!singleRow) {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                        startButton()
                    }
                }
            }
        }
    }
}

@Composable
fun LobbyInfoItem(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = modifier.padding(horizontal = 12.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().heightIn(min = 32.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            icon?.invoke()
            Text(
                value,
                modifier = Modifier.weight(1f, fill = false),
                color = LobbyStyle.Text,
                fontSize = 20.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            trailing?.invoke()
        }
        Text(label, color = LobbyStyle.Secondary, fontSize = 12.sp, maxLines = 1)
    }
}

@Composable
private fun InfoDivider() {
    VerticalDivider(modifier = Modifier.height(48.dp), color = LobbyStyle.Border)
}
