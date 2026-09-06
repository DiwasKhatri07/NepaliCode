package com.nepalicode.dev.nepalicode.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nepalicode.dev.nepalicode.editor.IdeThemeMode
import com.nepalicode.dev.nepalicode.editor.IdeThemePalette
import com.nepalicode.dev.nepalicode.editor.IdeThemeRegistry

@Composable
fun ThemeSelectorDialog(
    currentMode: IdeThemeMode,
    currentPalette: IdeThemePalette,
    onSelectTheme: (IdeThemeMode) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("theme_selector_dialog"),
        containerColor = currentPalette.surface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = "Theme Selector",
                    tint = currentPalette.primaryAccent,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "IDE Theme & Editor Mode",
                        color = currentPalette.textPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Choose from Dark Mode and various styles",
                        color = currentPalette.textMuted,
                        fontSize = 11.sp
                    )
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(IdeThemeMode.values()) { mode ->
                    val palette = IdeThemeRegistry.getPalette(mode)
                    val isSelected = mode == currentMode

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) currentPalette.surfaceVariant else currentPalette.background)
                            .border(
                                width = if (isSelected) 1.5.dp else 0.5.dp,
                                color = if (isSelected) currentPalette.primaryAccent else currentPalette.border,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                onSelectTheme(mode)
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                            .testTag("theme_item_${mode.name.lowercase()}"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Swatches preview
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ColorSwatch(palette.background, "Canvas")
                            ColorSwatch(palette.primaryAccent, "Keyword")
                            ColorSwatch(palette.stringColor, "String")
                            ColorSwatch(palette.builtinColor, "Builtin")
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = mode.title,
                                color = currentPalette.textPrimary,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                            Text(
                                text = mode.description,
                                color = currentPalette.textMuted,
                                fontSize = 10.sp
                            )
                        }

                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .background(currentPalette.primaryAccent, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = currentPalette.onPrimaryAccent,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = currentPalette.primaryAccent,
                    contentColor = currentPalette.onPrimaryAccent
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Done", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
private fun ColorSwatch(color: Color, description: String) {
    Box(
        modifier = Modifier
            .size(14.dp)
            .background(color, CircleShape)
            .border(0.5.dp, Color.White.copy(alpha = 0.3f), CircleShape)
    )
}
