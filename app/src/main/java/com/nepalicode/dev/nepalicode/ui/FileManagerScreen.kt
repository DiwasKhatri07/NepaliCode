package com.nepalicode.dev.nepalicode.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nepalicode.dev.nepalicode.data.ProjectFile
import com.nepalicode.dev.ui.theme.HighDensityCoral
import com.nepalicode.dev.ui.theme.HighDensityCyan
import com.nepalicode.dev.ui.theme.HighDensityOnPrimary
import com.nepalicode.dev.ui.theme.HighDensityPrimary
import com.nepalicode.dev.ui.theme.IdeBackground
import com.nepalicode.dev.ui.theme.IdeBorder
import com.nepalicode.dev.ui.theme.IdeSurface
import com.nepalicode.dev.ui.theme.IdeSurfaceVariant
import com.nepalicode.dev.ui.theme.NepaliCrimson
import com.nepalicode.dev.ui.theme.TechCyan
import com.nepalicode.dev.ui.theme.TechMint
import com.nepalicode.dev.ui.theme.TextMuted
import com.nepalicode.dev.ui.theme.TextPrimary
import com.nepalicode.dev.ui.theme.TextSecondary

import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material.icons.filled.Save
import com.nepalicode.dev.nepalicode.editor.IdeThemePalette
import com.nepalicode.dev.nepalicode.editor.IdeThemeRegistry
import com.nepalicode.dev.nepalicode.ui.components.CreateFileDialog
import com.nepalicode.dev.nepalicode.ui.components.DeleteFileDialog
import com.nepalicode.dev.nepalicode.ui.components.RenameFileDialog

@Composable
fun FileManagerScreen(
    files: List<ProjectFile>,
    activeFileName: String,
    onSelectFile: (String) -> Unit,
    onCreateNewFile: (String, String) -> Unit,
    onDeleteFile: (String) -> Unit,
    modifier: Modifier = Modifier,
    palette: IdeThemePalette = IdeThemeRegistry.HighDensityDark,
    onRenameFile: (String, String) -> Unit = { _, _ -> },
    onSaveFile: (String) -> Unit = {}
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var fileToRename by remember { mutableStateOf<String?>(null) }
    var fileToDelete by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var saveFeedback by remember { mutableStateOf<String?>(null) }

    val filteredFiles = remember(files, searchQuery) {
        if (searchQuery.isBlank()) files
        else files.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background)
    ) {
        // File Manager Header
        Surface(
            color = palette.surface,
            tonalElevation = 0.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Folder,
                            contentDescription = null,
                            tint = palette.primaryAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Project Files & Scripts",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = palette.textPrimary
                            )
                            Text(
                                text = "${files.count { it.name.endsWith(".np") }} .np files • nepalilang-project",
                                fontSize = 10.sp,
                                color = palette.textMuted,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                onSaveFile(activeFileName)
                                saveFeedback = "Saved $activeFileName"
                            },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = palette.primaryAccent
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(palette.primaryAccent)
                            ),
                            modifier = Modifier.testTag("save_active_file_manager_button")
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(14.dp), tint = palette.primaryAccent)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Save Active", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { showCreateDialog = true },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = palette.primaryAccent,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier.testTag("new_file_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New .np", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (saveFeedback != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(palette.primaryAccent.copy(alpha = 0.15f))
                            .padding(horizontal = 14.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "✓ $saveFeedback",
                            color = palette.primaryAccent,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        HorizontalDivider(color = palette.border, thickness = 1.dp)

        // Search Filter
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Filter .np source files...", color = palette.textMuted, fontSize = 12.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("file_manager_search_input"),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = palette.primaryAccent,
                    unfocusedBorderColor = palette.border,
                    focusedTextColor = palette.textPrimary,
                    unfocusedTextColor = palette.textPrimary,
                    cursorColor = palette.primaryAccent
                )
            )
        }

        // File List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredFiles) { file ->
                val isActive = file.name == activeFileName
                val icon = when {
                    file.name.endsWith(".np") -> Icons.Default.Code
                    file.name.endsWith(".toml") -> Icons.Default.Settings
                    else -> Icons.Default.Description
                }
                val iconColor = when {
                    file.name.endsWith(".np") -> palette.primaryAccent
                    file.name.endsWith(".toml") -> palette.secondaryAccent
                    else -> palette.textMuted
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectFile(file.name) }
                        .testTag("file_item_${file.name}"),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isActive) palette.surfaceVariant else palette.surface
                    ),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(
                            if (isActive) palette.primaryAccent else palette.border
                        )
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(iconColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                    .padding(6.dp)
                            ) {
                                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = file.name,
                                        fontSize = 13.sp,
                                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isActive) palette.primaryAccent else palette.textPrimary,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    if (isActive) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(palette.primaryAccent)
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = "ACTIVE",
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Black
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = "${file.content.lines().size} lines • ${file.content.toByteArray().size} bytes",
                                    fontSize = 10.sp,
                                    color = palette.textMuted,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        // Row Actions
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    onSaveFile(file.name)
                                    saveFeedback = "Saved ${file.name}"
                                },
                                modifier = Modifier.size(32.dp).testTag("file_manager_save_${file.name}")
                            ) {
                                Icon(
                                    Icons.Default.Save,
                                    contentDescription = "Save",
                                    tint = palette.textMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            IconButton(
                                onClick = { fileToRename = file.name },
                                modifier = Modifier.size(32.dp).testTag("file_manager_rename_${file.name}")
                            ) {
                                Icon(
                                    Icons.Default.DriveFileRenameOutline,
                                    contentDescription = "Rename",
                                    tint = palette.secondaryAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            if (files.size > 1) {
                                IconButton(
                                    onClick = { fileToDelete = file.name },
                                    modifier = Modifier.size(32.dp).testTag("file_manager_delete_${file.name}")
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = palette.errorUnderlineColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Create File Dialog
    if (showCreateDialog) {
        CreateFileDialog(
            palette = palette,
            existingFiles = files.map { it.name },
            onCreate = { name, content ->
                onCreateNewFile(name, content)
                showCreateDialog = false
            },
            onDismiss = { showCreateDialog = false }
        )
    }

    // Rename File Dialog
    if (fileToRename != null) {
        RenameFileDialog(
            fileName = fileToRename!!,
            palette = palette,
            existingFiles = files.map { it.name },
            onRename = { oldName, newName ->
                onRenameFile(oldName, newName)
                fileToRename = null
            },
            onDismiss = { fileToRename = null }
        )
    }

    // Delete Confirmation Dialog
    if (fileToDelete != null) {
        DeleteFileDialog(
            fileName = fileToDelete!!,
            palette = palette,
            onConfirm = {
                onDeleteFile(fileToDelete!!)
                fileToDelete = null
            },
            onDismiss = { fileToDelete = null }
        )
    }
}
