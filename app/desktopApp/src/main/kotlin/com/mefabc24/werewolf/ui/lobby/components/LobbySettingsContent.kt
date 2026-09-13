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
import com.mefabc24.werewolf.resources.settings
import com.mefabc24.werewolf.ui.lobby.LobbyIcon
import com.mefabc24.werewolf.ui.lobby.LobbyOutlineButton
import com.mefabc24.werewolf.ui.lobby.LobbyScrollArea
import com.mefabc24.werewolf.ui.lobby.LobbyStyle
import com.mefabc24.werewolf.ui.lobby.lobbyRow
import com.mefabc24.werewolf.ui.lobby.model.SettingTone
import com.mefabc24.werewolf.ui.lobby.model.SettingsSectionUi
import org.jetbrains.compose.resources.DrawableResource

@Composable
fun LobbySettingsContent(
    sections: List<SettingsSectionUi>,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
) {
    Column(modifier.fillMaxSize()) {
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
                sections.forEachIndexed { index, section ->
                    item(key = "section:${section.id}") {
                        SettingsSection(
                            title = section.title,
                            modifier = Modifier.padding(top = if (index == 0) 0.dp else 10.dp, bottom = 4.dp),
                        )
                    }
                    items(section.settings, key = { "setting:${section.id}:${it.id}" }) { setting ->
                        SettingRow(
                            icon = setting.icon,
                            label = setting.label,
                            value = setting.value,
                            tone = setting.tone,
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        LobbyOutlineButton(
            text = "Einstellungen bearbeiten",
            onClick = onEditClick,
            modifier = Modifier.fillMaxWidth(),
            icon = {
                // TODO(icon): Replace this placeholder with the pencil/edit resource.
                LobbyIcon(
                    Res.drawable.settings,
                    Modifier.size(17.dp),
                    tint = LobbyStyle.Secondary
                )
            },
        )
    }
}

@Composable
fun SettingsSection(title: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().heightIn(min = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, color = LobbyStyle.Secondary, fontSize = 12.sp)
        HorizontalDivider(Modifier.weight(1f), color = LobbyStyle.Border)
    }
}

@Composable
fun SettingRow(
    icon: DrawableResource,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    tone: SettingTone = SettingTone.NEUTRAL,
) {
    Row(
        modifier = modifier.fillMaxWidth().heightIn(min = 40.dp).lobbyRow()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LobbyIcon(
            icon,
            Modifier.size(18.dp),
            tint = LobbyStyle.Secondary
        )
        Text(
            label,
            modifier = Modifier.weight(1f),
            color = LobbyStyle.Text,
            fontSize = 13.sp,
            lineHeight = 19.sp,
        )
        Text(
            value,
            modifier = Modifier.widthIn(max = 120.dp),
            color = when (tone) {
                SettingTone.NEUTRAL -> LobbyStyle.Secondary
                SettingTone.POSITIVE -> LobbyStyle.Ready
                SettingTone.NEGATIVE -> LobbyStyle.NotReady
            },
            fontSize = 13.sp,
            lineHeight = 19.sp,
        )
    }
}
