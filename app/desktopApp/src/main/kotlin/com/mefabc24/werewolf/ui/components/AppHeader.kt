package com.mefabc24.werewolf.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mefabc24.werewolf.resources.Res
import com.mefabc24.werewolf.resources.logo
import com.mefabc24.werewolf.resources.settings
import com.mefabc24.werewolf.resources.wolf
import org.jetbrains.compose.resources.painterResource

@Composable
fun AppHeader(
    title: String,
    centerContent: (@Composable () -> Unit)? = null,
    onSettingsClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp)
            .background(Color(0xFF07101D))
            .padding(horizontal = 24.dp)
    ) {
        // Links
        Row(
            modifier = Modifier.align(Alignment.CenterStart),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(Res.drawable.logo),
                contentDescription = null,
                modifier = Modifier.size(42.dp)
            )

            Text(
                text = title,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Mitte
        centerContent?.let {
            Box(
                modifier = Modifier.align(Alignment.Center)
            ) {
                it()
            }
        }

        // Rechts
        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Image(
                painter = painterResource(Res.drawable.settings),
                contentDescription = "Einstellungen",
                modifier = Modifier.size(30.dp)
            )
        }
    }
}