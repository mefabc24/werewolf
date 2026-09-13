package com.mefabc24.werewolf.ui.common.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mefabc24.werewolf.ui.lobby.Island
import com.mefabc24.werewolf.ui.lobby.LobbyStyle

@Composable
fun SectionIsland(
    icon: Painter,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Island(modifier) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(icon, contentDescription = null, tint = LobbyStyle.Text, modifier = Modifier.size(28.dp))
            Column(Modifier.weight(1f)) {
                Text(title, color = LobbyStyle.Text, fontSize = 17.sp, fontWeight = FontWeight.Medium)
                if (description.isNotEmpty()) {
                    Spacer(Modifier.height(3.dp))
                    Text(description, color = LobbyStyle.Secondary, fontSize = 12.sp, lineHeight = 16.sp)
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        content()
    }
}
