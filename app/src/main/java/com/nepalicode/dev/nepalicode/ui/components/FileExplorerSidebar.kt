package com.nepalicode.dev.nepalicode.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nepalicode.dev.nepalicode.data.ProjectFile
import com.nepalicode.dev.nepalicode.editor.IdeThemePalette

data class FileTemplate(
    val name: String,
    val description: String,
    val defaultFileName: String,
    val codeSnippet: String
)

private val SCRIPT_TEMPLATES = listOf(
    FileTemplate(
        name = "Standard Script",
        description = "Main entry with kaam & dekha",
        defaultFileName = "script.np",
        codeSnippet = """# 🇳🇵 NepaliLang Script
# Standard execution entry

kaam main():
    dekha("Namaste from NepaliCode!")
    firta 0

main()
"""
    ),
    FileTemplate(
        name = "OOP Class (Kakshya)",
        description = "Object-Oriented kakshya with methods",
        defaultFileName = "student.np",
        codeSnippet = """# 🏛️ Object-Oriented NepaliLang
kakshya Vidyarthi:
    kaam init(naam, umar):
        yo.naam = naam
        yo.umar = umar

    kaam vivaran():
        dekha("Vidyarthi:", yo.naam, "| Umar:", yo.umar)

s1 = naya Vidyarthi("Aayush", 20)
s1.vivaran()
"""
    ),
    FileTemplate(
        name = "Web & API",
        description = "HTTP requests & JSON parsing",
        defaultFileName = "fetch_data.np",
        codeSnippet = """# 🌐 HTTP Requests & JSON
lyau anurodh
lyau json

dekha("Calling API endpoint...")
res = anurodh.get("https://jsonplaceholder.typicode.com/todos/1")
dekha("Status:", res.status)
dekha("Body:", res.text)
"""
    ),
    FileTemplate(
        name = "Blank .np File",
        description = "Empty script ready for your code",
        defaultFileName = "untitled.np",
        codeSnippet = """# 🇳🇵 NepaliLang Script
# Write your code here...
"""
    )
)

@Composable
fun FileExplorerSidebar(
    files: List<ProjectFile>,
    activeFileName: String,
    searchQuery: String,
    palette: IdeThemePalette,
    onSelectFile: (String) -> Unit,
    onSaveActiveFile: () -> Unit,
    onSaveFile: (String) -> Unit,
    onCreateNewFile: (String, String) -> Unit,
    onRenameFile: (String, String) -> Unit,
    onDeleteFile: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var fileToRename by remember { mutableStateOf<String?>(null) }
    var fileToDelete by remember { mutableStateOf<String?>(null) }
    var saveFeedbackText by remember { mutableStateOf<String?>(null) }

    val filteredFiles = remember(files, searchQuery) {
        if (searchQuery.isBlank()) files
        else files.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    Surface(
        modifier = modifier
            .fillMaxHeight()
            .width(320.dp)
            .border(width = 1.dp, color = palette.border)
            .testTag("file_explorer_sidebar"),
        color = palette.surface,
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(bottom = 8.dp)
        ) {
            // Sidebar Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(palette.surfaceVariant.copy(alpha = 0.5f))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    NepaliCodeBadgeLogo(size = 26.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "FILE EXPLORER",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.textPrimary,
                                letterSpacing = 1.2.sp
                            )
                        }
                        Text(
                            text = "nepali_project/ (${files.count { it.name.endsWith(".np") }} .np scripts)",
                            fontSize = 10.sp,
                            color = palette.textMuted,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("sidebar_close_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Sidebar",
                        tint = palette.textMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            HorizontalDivider(color = palette.border)

            // Primary Action Buttons (New File & Save Active)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { showCreateDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("sidebar_new_file_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = palette.primaryAccent,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "New .np",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = {
                        onSaveActiveFile()
                        saveFeedbackText = "Saved!"
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("sidebar_save_btn"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = palette.primaryAccent
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(palette.primaryAccent)
                    )
                ) {
                    Icon(
                        imageVector = if (saveFeedbackText != null) Icons.Default.Check else Icons.Default.Save,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                        tint = palette.primaryAccent
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = saveFeedbackText ?: "Save Active",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Search / Filter Input
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("sidebar_search_input"),
                    placeholder = {
                        Text(
                            text = "Filter files...",
                            color = palette.textMuted,
                            fontSize = 12.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = palette.textMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { onSearchQueryChange("") },
                                modifier = Modifier
                                    .size(24.dp)
                                    .testTag("sidebar_clear_search")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear search",
                                    tint = palette.textMuted,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
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

            Spacer(modifier = Modifier.height(4.dp))

            // Project Directory Label & Stats
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FolderOpen,
                        contentDescription = null,
                        tint = palette.secondaryAccent,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "PROJECT FILES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textMuted,
                        letterSpacing = 0.8.sp
                    )
                }

                Text(
                    text = "${filteredFiles.size} items",
                    fontSize = 10.sp,
                    color = palette.textMuted,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Files List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(filteredFiles, key = { it.name }) { file ->
                    val isActive = file.name == activeFileName
                    val isNp = file.name.endsWith(".np")
                    val isToml = file.name.endsWith(".toml")

                    val iconTint = when {
                        isNp -> palette.primaryAccent
                        isToml -> palette.secondaryAccent
                        else -> palette.textMuted
                    }

                    val lineCount = file.content.lines().size
                    val byteCount = file.content.toByteArray().size

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isActive) palette.surfaceVariant
                                else Color.Transparent
                            )
                            .border(
                                width = if (isActive) 1.dp else 0.dp,
                                color = if (isActive) palette.primaryAccent.copy(alpha = 0.5f) else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { onSelectFile(file.name) }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .testTag("file_item_${file.name}"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Left: Icon & File Name + Size
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(iconTint.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when {
                                        isNp -> Icons.Default.Code
                                        isToml -> Icons.Default.Settings
                                        else -> Icons.Default.Description
                                    },
                                    contentDescription = null,
                                    tint = iconTint,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = file.name,
                                        fontSize = 12.sp,
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
                                    text = "$lineCount lines • ${byteCount}B",
                                    fontSize = 10.sp,
                                    color = palette.textMuted,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        // Right: Row Actions (Save, Rename, Delete)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            // Save button
                            IconButton(
                                onClick = { onSaveFile(file.name) },
                                modifier = Modifier
                                    .size(28.dp)
                                    .testTag("file_save_btn_${file.name}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Save,
                                    contentDescription = "Save ${file.name}",
                                    tint = palette.textMuted,
                                    modifier = Modifier.size(14.dp)
                                )
                            }

                            // Rename button
                            IconButton(
                                onClick = { fileToRename = file.name },
                                modifier = Modifier
                                    .size(28.dp)
                                    .testTag("file_rename_btn_${file.name}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DriveFileRenameOutline,
                                    contentDescription = "Rename ${file.name}",
                                    tint = palette.secondaryAccent,
                                    modifier = Modifier.size(15.dp)
                                )
                            }

                            // Delete button (disabled if only 1 file in project)
                            if (files.size > 1) {
                                IconButton(
                                    onClick = { fileToDelete = file.name },
                                    modifier = Modifier
                                        .size(28.dp)
                                        .testTag("file_delete_btn_${file.name}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete ${file.name}",
                                        tint = palette.errorUnderlineColor.copy(alpha = 0.8f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = palette.border)

            // Sidebar Footer Info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "NepaliLang Workspace",
                    fontSize = 10.sp,
                    color = palette.textMuted
                )
                Text(
                    text = "Auto-Sync & Local IO",
                    fontSize = 10.sp,
                    color = palette.primaryAccent,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    // CREATE FILE DIALOG WITH TEMPLATE SELECTOR
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

    // RENAME FILE DIALOG
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

    // DELETE FILE CONFIRMATION DIALOG
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

@Composable
fun CreateFileDialog(
    palette: IdeThemePalette,
    existingFiles: List<String>,
    onCreate: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTemplateIndex by remember { mutableStateOf(0) }
    var fileNameInput by remember {
        mutableStateOf(SCRIPT_TEMPLATES[0].defaultFileName)
    }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(palette.primaryAccent),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Create .np Script",
                        color = palette.textPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Select template or enter custom name",
                        color = palette.textMuted,
                        fontSize = 11.sp
                    )
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "TEMPLATES",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.textMuted,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(SCRIPT_TEMPLATES.indices.toList()) { index ->
                        val template = SCRIPT_TEMPLATES[index]
                        val isSelected = selectedTemplateIndex == index
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) palette.primaryAccent.copy(alpha = 0.2f) else palette.surfaceVariant)
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) palette.primaryAccent else palette.border,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    selectedTemplateIndex = index
                                    fileNameInput = template.defaultFileName
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Column {
                                Text(
                                    text = template.name,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) palette.primaryAccent else palette.textPrimary
                                )
                                Text(
                                    text = template.description,
                                    fontSize = 9.sp,
                                    color = palette.textMuted
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "FILE NAME (.np)",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.textMuted,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = fileNameInput,
                    onValueChange = {
                        fileNameInput = it
                        errorMessage = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("create_file_name_input"),
                    singleLine = true,
                    placeholder = { Text("my_script.np", color = palette.textMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = palette.primaryAccent,
                        unfocusedBorderColor = palette.border,
                        focusedTextColor = palette.textPrimary,
                        unfocusedTextColor = palette.textPrimary,
                        cursorColor = palette.primaryAccent
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = errorMessage!!,
                        color = palette.errorUnderlineColor,
                        fontSize = 11.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val rawName = fileNameInput.trim()
                    if (rawName.isBlank()) {
                        errorMessage = "File name cannot be empty"
                        return@Button
                    }
                    val finalName = if (!rawName.contains(".")) "$rawName.np" else rawName
                    if (existingFiles.contains(finalName)) {
                        errorMessage = "A file named '$finalName' already exists"
                        return@Button
                    }
                    val content = SCRIPT_TEMPLATES[selectedTemplateIndex].codeSnippet
                    onCreate(finalName, content)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = palette.primaryAccent,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("confirm_create_btn")
            ) {
                Text("Create File", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = palette.textMuted)
            }
        },
        containerColor = palette.surface,
        modifier = Modifier.testTag("create_file_dialog")
    )
}

@Composable
fun RenameFileDialog(
    fileName: String,
    palette: IdeThemePalette,
    existingFiles: List<String>,
    onRename: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var newNameInput by remember { mutableStateOf(fileName) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.DriveFileRenameOutline,
                    contentDescription = null,
                    tint = palette.secondaryAccent,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Rename File",
                    color = palette.textPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column {
                Text(
                    text = "Renaming source file: $fileName",
                    color = palette.textMuted,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = newNameInput,
                    onValueChange = {
                        newNameInput = it
                        errorMessage = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("rename_file_input"),
                    singleLine = true,
                    placeholder = { Text("new_name.np", color = palette.textMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = palette.secondaryAccent,
                        unfocusedBorderColor = palette.border,
                        focusedTextColor = palette.textPrimary,
                        unfocusedTextColor = palette.textPrimary,
                        cursorColor = palette.secondaryAccent
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = errorMessage!!,
                        color = palette.errorUnderlineColor,
                        fontSize = 11.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val raw = newNameInput.trim()
                    if (raw.isBlank()) {
                        errorMessage = "File name cannot be empty"
                        return@Button
                    }
                    val finalName = if (!raw.contains(".")) "$raw.np" else raw
                    if (finalName == fileName) {
                        onDismiss()
                        return@Button
                    }
                    if (existingFiles.contains(finalName)) {
                        errorMessage = "File '$finalName' already exists"
                        return@Button
                    }
                    onRename(fileName, finalName)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = palette.secondaryAccent,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("confirm_rename_btn")
            ) {
                Text("Rename", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = palette.textMuted)
            }
        },
        containerColor = palette.surface,
        modifier = Modifier.testTag("rename_file_dialog")
    )
}

@Composable
fun DeleteFileDialog(
    fileName: String,
    palette: IdeThemePalette,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = palette.errorUnderlineColor,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Delete File?",
                    color = palette.textPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column {
                Text(
                    text = "Are you sure you want to permanently delete:",
                    color = palette.textPrimary,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(palette.surfaceVariant)
                        .padding(8.dp)
                ) {
                    Text(
                        text = fileName,
                        color = palette.errorUnderlineColor,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "This action cannot be undone.",
                    color = palette.textMuted,
                    fontSize = 11.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = palette.errorUnderlineColor,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("confirm_delete_btn")
            ) {
                Text("Delete File", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = palette.textMuted)
            }
        },
        containerColor = palette.surface,
        modifier = Modifier.testTag("delete_file_dialog")
    )
}
