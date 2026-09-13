package com.mefabc24.werewolf.ui.lobby

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

internal object LobbyStyle {
    val Main = Color(0xFF0E1924)
    val Island = Color(0xFF091420)
    val Row = Color(0xFF131F2B)
    val TabTrack = Color(0xFF07111C)
    val SelectedTab = Color(0xFF192632)
    val Border = Color(0xFF27313C)
    val Text = Color(0xFFE8ECEF)
    val Secondary = Color(0xFF939CA4)
    val Muted = Color(0xFF606B76)
    val Red = Color(0xFFAF1E2D)
    val Ready = Color(0xFF65A653)
    val NotReady = Color(0xFFCD3547)
    val IslandShape = RoundedCornerShape(14.dp)
    val RowShape = RoundedCornerShape(7.dp)
}

@Composable
fun Island(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .clip(LobbyStyle.IslandShape)
            .background(LobbyStyle.Island)
            .border(1.dp, LobbyStyle.Border, LobbyStyle.IslandShape)
            .padding(14.dp),
        content = content,
    )
}

internal fun Modifier.lobbyRow(): Modifier = this
    .clip(LobbyStyle.RowShape)
    .background(LobbyStyle.Row)
    .border(1.dp, LobbyStyle.Border, LobbyStyle.RowShape)

@Composable
internal fun LobbyIcon(
    resource: DrawableResource,
    modifier: Modifier = Modifier,
    tint: Color = LobbyStyle.Text,
    description: String? = null,
) {
    Icon(
        painter = painterResource(resource),
        contentDescription = description,
        tint = tint,
        modifier = modifier.size(20.dp),
    )
}

/** Reserves a narrow gutter so the desktop scrollbar never covers row content. */
@Composable
internal fun LobbyScrollArea(
    state: LazyListState,
    modifier: Modifier = Modifier,
    content: @Composable (Modifier) -> Unit,
) {
    Box(modifier) {
        content(Modifier.fillMaxSize().padding(end = 10.dp))
        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(state),
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight().width(5.dp),
            style = ScrollbarStyle(
                minimalHeight = 24.dp,
                thickness = 5.dp,
                shape = RoundedCornerShape(3.dp),
                hoverDurationMillis = 150,
                unhoverColor = LobbyStyle.Secondary.copy(alpha = 0.22f),
                hoverColor = LobbyStyle.Secondary.copy(alpha = 0.55f),
            ),
        )
    }
}

@Composable
internal fun LobbyOutlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    accent: Color = LobbyStyle.Secondary,
    borderColor: Color = LobbyStyle.Border,
    icon: @Composable () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.heightIn(min = 42.dp),
        shape = LobbyStyle.RowShape,
        border = BorderStroke(1.dp, if (enabled) borderColor else LobbyStyle.Border),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = accent,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = LobbyStyle.Muted,
        ),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
    ) {
        icon()
        Spacer(Modifier.width(8.dp))
        Text(text, fontSize = 13.sp)
    }
}

@Composable
internal fun PlayIcon(modifier: Modifier = Modifier, tint: Color = LobbyStyle.Red) {
    Canvas(modifier.size(16.dp)) {
        val triangle = Path().apply {
            moveTo(size.width * 0.25f, size.height * 0.15f)
            lineTo(size.width * 0.85f, size.height * 0.5f)
            lineTo(size.width * 0.25f, size.height * 0.85f)
            close()
        }
        drawPath(triangle, tint, style = Stroke(width = 1.5.dp.toPx()))
    }
}

@Composable
internal fun DropdownArrow(modifier: Modifier = Modifier) {
    Canvas(modifier.size(12.dp)) {
        val arrow = Path().apply {
            moveTo(size.width * 0.2f, size.height * 0.35f)
            lineTo(size.width * 0.5f, size.height * 0.65f)
            lineTo(size.width * 0.8f, size.height * 0.35f)
        }
        drawPath(arrow, LobbyStyle.Secondary, style = Stroke(width = 1.5.dp.toPx()))
    }
}
