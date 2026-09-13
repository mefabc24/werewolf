package com.mefabc24.werewolf.ui.common.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mefabc24.werewolf.resources.Res
import com.mefabc24.werewolf.resources.logo
import com.mefabc24.werewolf.resources.settings
import com.mefabc24.werewolf.ui.lobby.LobbyIcon
import com.mefabc24.werewolf.ui.lobby.LobbyStyle
import org.jetbrains.compose.resources.painterResource

@Composable
fun AppHeader(
    title: String,
    centerContent: (@Composable () -> Unit)? = null,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxWidth().height(68.dp).padding(horizontal = 20.dp)) {
        Row(
            modifier = Modifier.align(Alignment.CenterStart),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Image(
                painter = painterResource(Res.drawable.logo),
                contentDescription = null,
                modifier = Modifier.size(34.dp, 40.dp),
            )
            VerticalDivider(modifier = Modifier.height(24.dp), color = LobbyStyle.Border)
            Text(title, color = LobbyStyle.Text, fontSize = 22.sp, fontWeight = FontWeight.Medium)
        }
        centerContent?.let {
            Box(Modifier.align(Alignment.Center)) { it() }
        }
        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier.align(Alignment.CenterEnd).size(40.dp),
        ) {
            LobbyIcon(
                Res.drawable.settings,
                modifier = Modifier.size(30.dp),
                tint = LobbyStyle.Secondary,
                description = "Einstellungen",
            )
        }
    }
}
