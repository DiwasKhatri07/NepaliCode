package com.nepalicode.dev.nepalicode.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.nepalicode.dev.nepalicode.ai.AiAssistantResponse
import com.nepalicode.dev.nepalicode.editor.IdeThemePalette

@Composable
fun AiAssistantDialog(
    response: AiAssistantResponse,
    palette: IdeThemePalette,
    onApply: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = palette.surface,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, palette.primaryAccent.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .testTag("ai_assistant_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        NepaliCodeBadgeLogo(size = 28.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "NepaliCode AI",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.textPrimary
                            )
                            Text(
                                text = response.title,
                                fontSize = 11.sp,
                                color = palette.primaryAccent,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = palette.textMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Explanation
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(palette.background)
                        .padding(12.dp)
                ) {
                    Text(
                        text = response.explanation,
                        fontSize = 12.sp,
                        color = palette.textPrimary,
                        lineHeight = 18.sp
                    )
                }

                // Diff Summary if available
                if (response.diffSummary.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Suggested Changes & Diff:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.secondaryAccent
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(palette.background)
                            .padding(8.dp)
                    ) {
                        response.diffSummary.forEach { change ->
                            Text(
                                text = "• $change",
                                fontSize = 11.sp,
                                color = palette.textMuted,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // Code preview if suggestedCode is present
                if (!response.suggestedCode.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Generated / Transformed Code:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.primaryAccent
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(palette.background)
                            .padding(10.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = response.suggestedCode,
                            fontSize = 11.sp,
                            color = palette.textPrimary,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("ai_dismiss_button")
                    ) {
                        Text("Close", color = palette.textMuted)
                    }

                    if (!response.suggestedCode.isNullOrBlank()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = onApply,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = palette.primaryAccent,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier.testTag("ai_apply_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Apply to File", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
