package com.nepalicode.dev.nepalicode.ui.components

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.nepalicode.dev.nepalicode.data.ProjectFile
import com.nepalicode.dev.nepalicode.editor.IdeThemePalette

@Composable
fun QuickExamplesDialog(
    files: List<ProjectFile>,
    activeFileName: String,
    palette: IdeThemePalette,
    onSelectFile: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = palette.surface,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, palette.primaryAccent.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                .testTag("quick_examples_dialog")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
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
                                text = "Code Examples & Demos",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.textPrimary
                            )
                            Text(
                                text = "Explore pre-built NepaliCode scripts",
                                fontSize = 11.sp,
                                color = palette.textMuted
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = palette.textMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp)
                ) {
                    items(files) { file ->
                        val isActive = file.name == activeFileName
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isActive) palette.primaryAccent.copy(alpha = 0.15f) else palette.background)
                                .clickable {
                                    onSelectFile(file.name)
                                    onDismiss()
                                }
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                                .testTag("example_file_${file.name}"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Code,
                                contentDescription = null,
                                tint = if (isActive) palette.primaryAccent else palette.secondaryAccent,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = file.name,
                                    fontSize = 13.sp,
                                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isActive) palette.primaryAccent else palette.textPrimary,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = getFileDescription(file.name),
                                    fontSize = 11.sp,
                                    color = palette.textMuted
                                )
                            }
                            if (isActive) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(palette.primaryAccent)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "ACTIVE",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = androidx.compose.ui.graphics.Color.Black
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }
    }
}

private fun getFileDescription(name: String): String = when (name) {
    "main.np" -> "Language core: variables, functions, conditions & loops"
    "oop_classes.np" -> "Object-Oriented Programming: classes (kakshya) & methods"
    "csv_and_files.np" -> "CSV parsing, writing & filesystem path operations"
    "suraksha_crypto.np" -> "Security hashing: SHA-256, MD5 & Base64 encode/decode"
    "api_demo.np" -> "HTTP requests with anurodh and JSON parsing"
    "web_automation.np" -> "Virtual browser automation and link extraction"
    "web_automation_advanced.np" -> "End-to-end browser pipeline with typing & clicks"
    "regex_and_text.np" -> "Regex pattern matching, extraction & replacements"
    "math_and_stats.np" -> "Ganit scientific math, square root & statistics"
    "guessing_game.np" -> "Interactive game using random generator & console input"
    "nepali.toml" -> "Project configuration and dependencies manifest"
    else -> "NepaliCode script"
}
