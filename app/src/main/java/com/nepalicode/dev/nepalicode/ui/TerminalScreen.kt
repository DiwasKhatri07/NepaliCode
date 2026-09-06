package com.nepalicode.dev.nepalicode.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nepalicode.dev.ui.theme.HighDensityCoral
import com.nepalicode.dev.ui.theme.HighDensityCyan
import com.nepalicode.dev.ui.theme.HighDensityPrimary
import com.nepalicode.dev.ui.theme.IdeBorder
import com.nepalicode.dev.ui.theme.IdeSurface
import com.nepalicode.dev.ui.theme.IdeSurfaceVariant
import com.nepalicode.dev.ui.theme.NepaliCrimson
import com.nepalicode.dev.ui.theme.TechCyan
import com.nepalicode.dev.ui.theme.TechMint
import com.nepalicode.dev.ui.theme.TerminalBackground
import com.nepalicode.dev.ui.theme.TerminalCyan
import com.nepalicode.dev.ui.theme.TerminalGreen
import com.nepalicode.dev.ui.theme.TerminalRed
import com.nepalicode.dev.ui.theme.TerminalYellow
import com.nepalicode.dev.ui.theme.TextMuted
import com.nepalicode.dev.ui.theme.TextPrimary
import com.nepalicode.dev.ui.theme.TextSecondary

@Composable
fun TerminalScreen(
    entries: List<TerminalEntry>,
    isRunning: Boolean,
    isWaitingForInput: Boolean,
    inputPrompt: String,
    onRunScript: () -> Unit,
    onStopExecution: () -> Unit,
    onSubmitRepl: (String) -> Unit,
    onSubmitInput: (String) -> Unit,
    onClearTerminal: () -> Unit,
    modifier: Modifier = Modifier
) {
    var replText by remember { mutableStateOf("") }
    var userInputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Auto-scroll to bottom on new output
    LaunchedEffect(entries.size) {
        if (entries.isNotEmpty()) {
            listState.animateScrollToItem(entries.size - 1)
        }
    }

    val replQuickCommands = listOf(
        "prakar(\"Nepal\")",
        "ank(\"123\") + 77",
        "suchi([1, 2, 3])",
        "kram([5, 2, 9, 1])",
        "ulta([10, 20, 30])",
        "ganit.sqrt(144)",
        "web.get(\"https://example.com\")",
        "regex.khoj(\"\\\\d+\", \"NP-2026\")",
        "vars",
        "modules",
        "syntax",
        "credits",
        "clear"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TerminalBackground)
            .imePadding()
    ) {
        // Terminal Header Bar (High Density Theme)
        Surface(
            color = IdeSurface,
            tonalElevation = 0.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Terminal,
                        contentDescription = "Terminal",
                        tint = HighDensityPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Nepali Terminal & REPL",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    if (isRunning) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(HighDensityCoral.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(10.dp),
                                color = HighDensityCoral,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Running...",
                                color = HighDensityCoral,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isRunning) {
                        IconButton(
                            onClick = onStopExecution,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("stop_execution_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = "Stop",
                                tint = HighDensityCoral,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    } else {
                        IconButton(
                            onClick = onRunScript,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("run_from_terminal_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Run Script",
                                tint = HighDensityPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = onClearTerminal,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("clear_terminal_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ClearAll,
                            contentDescription = "Clear Terminal",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        HorizontalDivider(color = IdeBorder, thickness = 1.dp)

        // Terminal Output List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp)
                .testTag("terminal_output_list"),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(entries) { entry ->
                TerminalLogItem(entry)
            }
        }

        // Active Interactive Input Prompt (when input() / leu() is called)
        AnimatedVisibility(visible = isWaitingForInput) {
            Surface(
                color = IdeSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = if (inputPrompt.isNotEmpty()) inputPrompt else "Program requested input (leu / input):",
                        color = TerminalYellow,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = userInputText,
                            onValueChange = { userInputText = it },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("user_input_prompt_field"),
                            placeholder = { Text("Type response here...", color = TextMuted, fontSize = 12.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = HighDensityPrimary,
                                unfocusedBorderColor = IdeBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(onSend = {
                                onSubmitInput(userInputText)
                                userInputText = ""
                            })
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        ElevatedButton(
                            onClick = {
                                onSubmitInput(userInputText)
                                userInputText = ""
                            },
                            colors = ButtonDefaults.elevatedButtonColors(containerColor = HighDensityPrimary),
                            modifier = Modifier.testTag("submit_input_button")
                        ) {
                            Text("Send", color = Color(0xFF381E72), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // REPL Interactive Shell Input Bar
        Surface(
            color = IdeSurface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                HorizontalDivider(color = IdeBorder, thickness = 1.dp)

                // Quick REPL expression pills
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(replQuickCommands) { cmd ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = IdeSurfaceVariant,
                            modifier = Modifier
                                .clickable { onSubmitRepl(cmd) }
                                .border(0.5.dp, IdeBorder, RoundedCornerShape(6.dp))
                        ) {
                            Text(
                                text = cmd,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = HighDensityCyan,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = ">>> ",
                        color = HighDensityPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )

                    OutlinedTextField(
                        value = replText,
                        onValueChange = { replText = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("repl_input_field"),
                        placeholder = { Text("nepali REPL expression or command...", color = TextMuted, fontSize = 12.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HighDensityPrimary,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = {
                            onSubmitRepl(replText)
                            replText = ""
                        })
                    )

                    IconButton(
                        onClick = {
                            onSubmitRepl(replText)
                            replText = ""
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("submit_repl_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Execute REPL",
                            tint = HighDensityPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TerminalLogItem(entry: TerminalEntry) {
    val textColor = when (entry.type) {
        TerminalEntryType.STDOUT -> TerminalGreen
        TerminalEntryType.STDERR -> TerminalRed
        TerminalEntryType.SYSTEM -> TerminalCyan
        TerminalEntryType.INPUT_PROMPT -> TerminalYellow
        TerminalEntryType.REPL_PROMPT -> Color(0xFFF1F5F9)
    }

    val prefix = when (entry.type) {
        TerminalEntryType.REPL_PROMPT -> ""
        TerminalEntryType.STDERR -> ""
        TerminalEntryType.INPUT_PROMPT -> ""
        else -> ""
    }

    Text(
        text = prefix + entry.text,
        color = textColor,
        fontSize = 12.sp,
        fontFamily = FontFamily.Monospace,
        lineHeight = 18.sp
    )
}
