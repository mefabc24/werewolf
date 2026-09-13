package com.mefabc24.werewolf.ui.lobby.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mefabc24.werewolf.resources.Res
import com.mefabc24.werewolf.resources.send_arrow
import com.mefabc24.werewolf.ui.lobby.LobbyIcon
import com.mefabc24.werewolf.ui.lobby.LobbyScrollArea
import com.mefabc24.werewolf.ui.lobby.LobbyStyle
import com.mefabc24.werewolf.ui.lobby.lobbyRow
import com.mefabc24.werewolf.ui.lobby.model.ChatMessageUi

@Composable
fun LobbyChatContent(
    messages: List<ChatMessageUi>,
    draft: String,
    onDraftChange: (String) -> Unit,
    onSend: (String) -> Unit,
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
                items(messages, key = { it.id }) { message ->
                    ChatMessageRow(
                        name = message.name,
                        message = message.message,
                        timestamp = message.timestamp,
                    )
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        ChatInput(value = draft, onValueChange = onDraftChange, onSend = onSend)
    }
}

@Composable
fun ChatMessageRow(
    name: String,
    message: String,
    timestamp: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().heightIn(min = 40.dp).lobbyRow()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = LobbyStyle.Text, fontWeight = FontWeight.Medium)) {
                    append(name)
                    append(": ")
                }
                append(message)
            },
            modifier = Modifier.weight(1f),
            color = LobbyStyle.Secondary,
            fontSize = 13.sp,
            lineHeight = 19.sp,
        )
        Text(timestamp, color = LobbyStyle.Secondary, fontSize = 11.sp, lineHeight = 19.sp, maxLines = 1)
    }
}

@Composable
fun ChatInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Nachricht eingeben...",
) {
    Row(
        modifier = modifier.fillMaxWidth().heightIn(min = 48.dp)
            .border(1.dp, LobbyStyle.Border, LobbyStyle.RowShape)
            .padding(start = 12.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f).padding(vertical = 12.dp)
                .semantics { contentDescription = "Chat-Nachricht" },
            textStyle = TextStyle(color = LobbyStyle.Text, fontSize = 13.sp, lineHeight = 20.sp),
            singleLine = true,
            cursorBrush = SolidColor(LobbyStyle.Text),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = { if (value.isNotBlank()) onSend(value) }),
            decorationBox = { innerTextField ->
                Box {
                    if (value.isEmpty()) {
                        Text(placeholder, color = LobbyStyle.Secondary, fontSize = 13.sp)
                    }
                    innerTextField()
                }
            },
        )
        IconButton(
            onClick = { onSend(value) },
            enabled = value.isNotBlank(),
            modifier = Modifier.size(40.dp),
        ) {
            LobbyIcon(
                Res.drawable.send_arrow,
                tint = if (value.isNotBlank()) LobbyStyle.Text else LobbyStyle.Muted,
                description = "Nachricht senden",
            )
        }
    }
}
