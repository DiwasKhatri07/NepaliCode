package com.nepalicode.dev.nepalicode.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nepalicode.dev.nepalicode.editor.IdeThemePalette

data class NpTemplate(
    val name: String,
    val filename: String,
    val description: String,
    val content: String
)

val NP_TEMPLATES = listOf(
    NpTemplate(
        name = "Blank .np Script",
        filename = "script.np",
        description = "Empty NepaliLang script ready for your code",
        content = "# NepaliLang (.np) Script\n# Created with NepaliCode IDE\n\nkaam main():\n    dekha(\"Namaste Nepal!\")\n\nmain()\n"
    ),
    NpTemplate(
        name = "Hello World (.np)",
        filename = "hello_world.np",
        description = "Standard greeting with Nepali output aliases",
        content = "# NepaliLang Hello World Example\n\nnaam = \"Diwas\"\ndekha(\"Namaste,\", naam)\ndekha(\"NepaliLang मा स्वागत छ!\")\n"
    ),
    NpTemplate(
        name = "Web Automation (.np)",
        filename = "web_automation.np",
        description = "Automate browser interactions, typing, and clicks",
        content = "lyau browser\n\n# Open website & automate actions\nsite = browser.khol(\"https://example.com\")\nsite.wait(1)\nsite.type(\"input[name='q']\", \"NepaliLang programming\")\nsite.click(\"button[type='submit']\")\nsite.wait(2)\ndekha(\"Web title:\", site.title())\nsite.close()\n"
    ),
    NpTemplate(
        name = "SQLite Database App (.np)",
        filename = "database_app.np",
        description = "Create table, insert rows, and run queries",
        content = "lyau database\n\ndb = database.open(\"nepali.db\")\ndb.execute(\"CREATE TABLE IF NOT EXISTS vidyarthi (id INTEGER, naam TEXT, ank INTEGER)\")\ndb.execute(\"INSERT INTO vidyarthi VALUES (?, ?, ?)\", [1, \"Aarav\", 95])\ndb.execute(\"INSERT INTO vidyarthi VALUES (?, ?, ?)\", [2, \"Pooja\", 88])\n\nrecords = db.query(\"SELECT * FROM vidyarthi\")\ndekha(\"Vidyarthi suchi:\", records)\ndb.close()\n"
    ),
    NpTemplate(
        name = "HTTP API Client (.np)",
        filename = "api_client.np",
        description = "Fetch data from REST APIs using anurodh",
        content = "lyau anurodh\n\npratikriya = anurodh.get(\"https://api.github.com\")\ndekha(\"HTTP Status:\", pratikriya.status)\ndekha(\"Body:\", pratikriya.text)\n"
    ),
    NpTemplate(
        name = "Developer Credits Demo (.np)",
        filename = "credits_demo.np",
        description = "Showcase developer attribution & language features",
        content = "# NepaliLang (.np) Specification\n# Architect: Diwas Khatri (diwaskhatri935@gmail.com)\n\ndekha(\"=====================================\")\ndekha(\"   NepaliLang v2.4.0 High-Density\")\ndekha(\"   Lead Architect: Diwas Khatri\")\ndekha(\"   Contact: diwaskhatri935@gmail.com\")\ndekha(\"=====================================\")\n\nkaam jankari():\n    firta \"NepaliLang provides native syntax in Nepali & English!\"\n\ndekha(jankari())\n"
    )
)

@Composable
fun NewNpFileDialog(
    palette: IdeThemePalette,
    onCreateFile: (fileName: String, content: String) -> Unit,
    onDismiss: () -> Unit
) {
    var fileName by remember { mutableStateOf("new_script.np") }
    var selectedTemplate by remember { mutableStateOf(NP_TEMPLATES[0]) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("new_np_file_dialog"),
        containerColor = palette.surface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = "New .np File",
                    tint = palette.primaryAccent,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "New NepaliLang (.np) File",
                        color = palette.textPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Create with .np extension & starter template",
                        color = palette.textMuted,
                        fontSize = 11.sp
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = fileName,
                    onValueChange = {
                        fileName = it
                    },
                    label = { Text("File Name", fontSize = 12.sp) },
                    placeholder = { Text("my_script.np", fontSize = 12.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = palette.primaryAccent,
                        unfocusedBorderColor = palette.border,
                        focusedTextColor = palette.textPrimary,
                        unfocusedTextColor = palette.textPrimary,
                        focusedLabelColor = palette.primaryAccent,
                        unfocusedLabelColor = palette.textMuted
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_file_input")
                )

                Text(
                    text = "Select .np Starter Template:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.primaryAccent
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(NP_TEMPLATES) { template ->
                        val isSelected = template == selectedTemplate

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) palette.surfaceVariant else palette.background)
                                .border(
                                    width = if (isSelected) 1.dp else 0.5.dp,
                                    color = if (isSelected) palette.primaryAccent else palette.border,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    selectedTemplate = template
                                    if (fileName == "new_script.np" || fileName.endsWith(".np")) {
                                        fileName = template.filename
                                    }
                                }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = template.name,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.textPrimary
                                )
                                Text(
                                    text = template.description,
                                    fontSize = 10.sp,
                                    color = palette.textMuted
                                )
                            }
                            Text(
                                text = ".np",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.primaryAccent,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalName = if (!fileName.endsWith(".np")) "$fileName.np" else fileName
                    onCreateFile(finalName, selectedTemplate.content)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = palette.primaryAccent,
                    contentColor = palette.onPrimaryAccent
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("confirm_create_file_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Create .np File", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = palette.textSecondary, fontSize = 12.sp)
            }
        }
    )
}
