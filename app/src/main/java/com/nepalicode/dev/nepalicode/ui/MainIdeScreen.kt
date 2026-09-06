package com.nepalicode.dev.nepalicode.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.FormatAlignLeft
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Rule
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nepalicode.dev.nepalicode.editor.IdeThemeRegistry
import com.nepalicode.dev.nepalicode.ui.components.AiAssistantDialog
import com.nepalicode.dev.nepalicode.ui.components.CommandItem
import com.nepalicode.dev.nepalicode.ui.components.FileExplorerSidebar
import com.nepalicode.dev.nepalicode.ui.components.IdeCommandPaletteDialog
import com.nepalicode.dev.nepalicode.ui.components.NepaliCodeBadgeLogo
import com.nepalicode.dev.nepalicode.ui.components.QuickExamplesDialog

@Composable
fun MainIdeScreen(
    viewModel: NepaliCodeViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    var showFileDropdown by remember { mutableStateOf(false) }
    var showOptionsMenu by remember { mutableStateOf(false) }

    val palette = remember(state.themeMode) {
        IdeThemeRegistry.getPalette(state.themeMode)
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(state.isSidebarOpen) {
        if (state.isSidebarOpen && drawerState.isClosed) {
            drawerState.open()
        } else if (!state.isSidebarOpen && drawerState.isOpen) {
            drawerState.close()
        }
    }

    LaunchedEffect(drawerState.isOpen) {
        if (!drawerState.isOpen && state.isSidebarOpen) {
            viewModel.closeSidebar()
        }
    }

    LaunchedEffect(state.saveNotification) {
        if (state.saveNotification != null) {
            kotlinx.coroutines.delay(2500)
            viewModel.dismissSaveNotification()
        }
    }

    val commands = remember(state.activeFileName, state.lintIssues) {
        listOf(
            CommandItem("run", "Run Script", "nepali ${state.activeFileName}", Icons.Default.PlayArrow, "RUN") {
                viewModel.runCurrentCode()
            },
            CommandItem("explorer", "File Explorer Sidebar", "Create, save, rename, and delete .np source files", Icons.Default.Folder, "FILES") {
                viewModel.openSidebar()
            },
            CommandItem("save_file", "Save Active File", "Save current changes to disk (${state.activeFileName})", Icons.Default.Save, "FILES") {
                viewModel.saveCurrentFile()
            },
            CommandItem("new_file", "Create New .np File", "Add a new script with custom template", Icons.Default.NoteAdd, "FILES") {
                viewModel.openNewFileDialog()
            },
            CommandItem("ai_explain", "AI: Explain Code", "Break down logic, execution flow and modules", Icons.Default.AutoAwesome, "AI") {
                viewModel.openAiAssistant("explain")
            },
            CommandItem("ai_improve", "AI: Optimize & Modernize", "Apply idiomatic NepaliCode patterns and clean diff", Icons.Default.AutoAwesome, "AI") {
                viewModel.openAiAssistant("improve")
            },
            CommandItem("ai_tests", "AI: Generate Unit Tests", "Produce automated assert test suite", Icons.Default.BugReport, "AI") {
                viewModel.openAiAssistant("tests")
            },
            CommandItem("ai_fix", "AI: Fix Errors & Lints", "Resolve compiler warnings and syntax gaps", Icons.Default.Build, "AI") {
                viewModel.openAiAssistant("fix")
            },
            CommandItem("format", "Format Code", "Standardize 4-space indents and operator spacing", Icons.Default.FormatAlignLeft, "CODE") {
                viewModel.formatCode()
            },
            CommandItem("lint", "Check Syntax", "Scan for unclosed blocks or missing colons", Icons.Default.Rule, "CODE") {
                viewModel.checkLint()
            },
            CommandItem("conv_py_np", "Translate: Python -> NepaliCode", "Convert def/return/if/else to kaam/firta/yedi/natra", Icons.Default.Translate, "TOOLS") {
                viewModel.convertPythonToNepali()
            },
            CommandItem("conv_np_py", "Translate: NepaliCode -> Python", "Convert native Nepali keywords to standard Python", Icons.Default.Translate, "TOOLS") {
                viewModel.convertNepaliToPython()
            },
            CommandItem("examples", "Browse Examples & Demos", "Explore pre-built automation, API and OOP scripts", Icons.Default.FolderSpecial, "PROJECT") {
                viewModel.openQuickExamples()
            },
            CommandItem("theme", "Select Theme & Mode", "Switch between High-Density Dark, Light, Matrix or Navy", Icons.Default.Palette, "VIEW") {
                viewModel.openThemeSelector()
            },
            CommandItem("clear_term", "Clear Terminal", "Flush IDE standard output and system logs", Icons.Default.DeleteSweep, "TERMINAL") {
                viewModel.clearTerminal()
            },
            CommandItem("credits", "Developer Credits & Spec", "Diwas Khatri / NepaliSource architecture", Icons.Default.Info, "ABOUT") {
                viewModel.openDevCredits()
            }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = palette.surface,
                drawerTonalElevation = 4.dp
            ) {
                FileExplorerSidebar(
                    files = state.files,
                    activeFileName = state.activeFileName,
                    searchQuery = state.sidebarSearchQuery,
                    palette = palette,
                    onSelectFile = { fileName ->
                        viewModel.switchFile(fileName)
                        viewModel.closeSidebar()
                    },
                    onSaveActiveFile = { viewModel.saveCurrentFile() },
                    onSaveFile = { fileName ->
                        if (fileName == state.activeFileName) {
                            viewModel.saveCurrentFile()
                        } else {
                            val f = state.files.find { it.name == fileName }
                            if (f != null) viewModel.saveFile(fileName, f.content)
                        }
                    },
                    onCreateNewFile = { name, content ->
                        viewModel.createNewFile(name, content)
                    },
                    onRenameFile = { oldName, newName ->
                        viewModel.renameFile(oldName, newName)
                    },
                    onDeleteFile = { name ->
                        viewModel.deleteFile(name)
                    },
                    onSearchQueryChange = { q ->
                        viewModel.updateSidebarSearch(q)
                    },
                    onClose = { viewModel.closeSidebar() }
                )
            }
        },
        gesturesEnabled = true
    ) {
        Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background),
        topBar = {
            Surface(
                color = palette.surface,
                tonalElevation = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Left: Logo, File Selector & Project Path
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f, fill = false)
                        ) {
                            NepaliCodeBadgeLogo(
                                size = 32.dp,
                                modifier = Modifier
                                    .padding(end = 6.dp)
                                    .clickable { viewModel.openQuickExamples() }
                                    .testTag("app_logo_badge")
                            )

                            IconButton(
                                onClick = { viewModel.toggleSidebar() },
                                modifier = Modifier.size(36.dp).testTag("main_menu_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Menu & Files",
                                    tint = palette.primaryAccent,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))

                            Box {
                                Column(
                                    modifier = Modifier
                                        .clickable { showFileDropdown = true }
                                        .padding(vertical = 2.dp)
                                        .testTag("file_dropdown_trigger")
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = state.activeFileName,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = palette.textPrimary,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "▾", color = palette.textMuted, fontSize = 11.sp)
                                    }
                                    Text(
                                        text = "NepaliLang (.np) IDE",
                                        fontSize = 10.sp,
                                        color = palette.textMuted,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                DropdownMenu(
                                    expanded = showFileDropdown,
                                    onDismissRequest = { showFileDropdown = false },
                                    modifier = Modifier.background(palette.surface)
                                ) {
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Folder, contentDescription = null, tint = palette.secondaryAccent, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("Open File Explorer...", color = palette.secondaryAccent, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            }
                                        },
                                        onClick = {
                                            showFileDropdown = false
                                            viewModel.openSidebar()
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Save, contentDescription = null, tint = palette.primaryAccent, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("Save ${state.activeFileName}", color = palette.primaryAccent, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            }
                                        },
                                        onClick = {
                                            showFileDropdown = false
                                            viewModel.saveCurrentFile()
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.NoteAdd, contentDescription = null, tint = palette.primaryAccent, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("New .np File...", color = palette.primaryAccent, fontSize = 13.sp)
                                            }
                                        },
                                        onClick = {
                                            showFileDropdown = false
                                            viewModel.openNewFileDialog()
                                        }
                                    )
                                    HorizontalDivider(color = palette.border)
                                    state.files.forEach { file ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = file.name,
                                                    color = if (file.name == state.activeFileName) palette.primaryAccent else palette.textPrimary,
                                                    fontFamily = FontFamily.Monospace,
                                                    fontSize = 13.sp,
                                                    fontWeight = if (file.name == state.activeFileName) FontWeight.Bold else FontWeight.Normal
                                                )
                                            },
                                            onClick = {
                                                viewModel.switchFile(file.name)
                                                showFileDropdown = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Right: AI/Command Palette, Quick Examples, Theme, Format, Lint, Run, Options
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            // AI & Command Palette Quick Button
                            IconButton(
                                onClick = { viewModel.openCommandPalette() },
                                modifier = Modifier.size(36.dp).testTag("command_palette_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "Command Palette & AI",
                                    tint = Color(0xFF00E676),
                                    modifier = Modifier.size(19.dp)
                                )
                            }

                            // Quick Examples Library
                            IconButton(
                                onClick = { viewModel.openQuickExamples() },
                                modifier = Modifier.size(36.dp).testTag("quick_examples_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FolderSpecial,
                                    contentDescription = "Code Examples",
                                    tint = palette.secondaryAccent,
                                    modifier = Modifier.size(19.dp)
                                )
                            }

                            // Theme Quick Picker
                            IconButton(
                                onClick = { viewModel.openThemeSelector() },
                                modifier = Modifier.size(36.dp).testTag("theme_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Palette,
                                    contentDescription = "Themes & Modes",
                                    tint = palette.primaryAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Format
                            IconButton(
                                onClick = { viewModel.formatCode() },
                                modifier = Modifier.size(36.dp).testTag("format_code_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FormatAlignLeft,
                                    contentDescription = "Format Code",
                                    tint = palette.textSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Lint Check
                            IconButton(
                                onClick = { viewModel.checkLint() },
                                modifier = Modifier.size(36.dp).testTag("lint_check_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Rule,
                                    contentDescription = "Check Code",
                                    tint = if (state.lintIssues.isNotEmpty()) palette.errorUnderlineColor else palette.textSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Quick Save
                            IconButton(
                                onClick = { viewModel.saveCurrentFile() },
                                modifier = Modifier.size(36.dp).testTag("top_bar_save_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Save,
                                    contentDescription = "Save Active File",
                                    tint = palette.primaryAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(2.dp))

                            // Pill Run Button
                            if (state.isRunning) {
                                Button(
                                    onClick = { viewModel.stopExecution() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = palette.errorUnderlineColor,
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(20.dp),
                                    modifier = Modifier
                                        .height(36.dp)
                                        .testTag("run_action_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Stop,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Stop", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                            } else {
                                Button(
                                    onClick = { viewModel.runCurrentCode() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = palette.primaryAccent,
                                        contentColor = palette.onPrimaryAccent
                                    ),
                                    shape = RoundedCornerShape(20.dp),
                                    modifier = Modifier
                                        .height(36.dp)
                                        .testTag("run_action_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = palette.onPrimaryAccent,
                                        modifier = Modifier.size(17.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Run", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }

                            // More Options Menu
                            Box {
                                IconButton(
                                    onClick = { showOptionsMenu = true },
                                    modifier = Modifier.size(36.dp).testTag("more_options_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = "More Options",
                                        tint = palette.textSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                DropdownMenu(
                                    expanded = showOptionsMenu,
                                    onDismissRequest = { showOptionsMenu = false },
                                    modifier = Modifier.background(palette.surface)
                                ) {
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Folder, contentDescription = null, tint = palette.primaryAccent, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("File Explorer Sidebar", color = palette.textPrimary, fontSize = 13.sp)
                                            }
                                        },
                                        onClick = {
                                            showOptionsMenu = false
                                            viewModel.openSidebar()
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Save, contentDescription = null, tint = palette.primaryAccent, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("Save Active File", color = palette.textPrimary, fontSize = 13.sp)
                                            }
                                        },
                                        onClick = {
                                            showOptionsMenu = false
                                            viewModel.saveCurrentFile()
                                        }
                                    )
                                    HorizontalDivider(color = palette.border)
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("AI: Explain Code", color = palette.textPrimary, fontSize = 13.sp)
                                            }
                                        },
                                        onClick = {
                                            showOptionsMenu = false
                                            viewModel.openAiAssistant("explain")
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = palette.primaryAccent, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("AI: Optimize & Modernize", color = palette.textPrimary, fontSize = 13.sp)
                                            }
                                        },
                                        onClick = {
                                            showOptionsMenu = false
                                            viewModel.openAiAssistant("improve")
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.BugReport, contentDescription = null, tint = palette.secondaryAccent, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("AI: Generate Unit Tests", color = palette.textPrimary, fontSize = 13.sp)
                                            }
                                        },
                                        onClick = {
                                            showOptionsMenu = false
                                            viewModel.openAiAssistant("tests")
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Build, contentDescription = null, tint = palette.errorUnderlineColor, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("AI: Auto-Fix Errors", color = palette.textPrimary, fontSize = 13.sp)
                                            }
                                        },
                                        onClick = {
                                            showOptionsMenu = false
                                            viewModel.openAiAssistant("fix")
                                        }
                                    )
                                    HorizontalDivider(color = palette.border)
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Translate, contentDescription = null, tint = palette.secondaryAccent, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("Python ➔ NepaliCode", color = palette.textPrimary, fontSize = 13.sp)
                                            }
                                        },
                                        onClick = {
                                            showOptionsMenu = false
                                            viewModel.convertPythonToNepali()
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Translate, contentDescription = null, tint = palette.secondaryAccent, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("NepaliCode ➔ Python", color = palette.textPrimary, fontSize = 13.sp)
                                            }
                                        },
                                        onClick = {
                                            showOptionsMenu = false
                                            viewModel.convertNepaliToPython()
                                        }
                                    )
                                    HorizontalDivider(color = palette.border)
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.FolderSpecial, contentDescription = null, tint = palette.primaryAccent, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("Code Examples Library", color = palette.textPrimary, fontSize = 13.sp)
                                            }
                                        },
                                        onClick = {
                                            showOptionsMenu = false
                                            viewModel.openQuickExamples()
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Palette, contentDescription = null, tint = palette.secondaryAccent, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("Themes & Color Modes", color = palette.textPrimary, fontSize = 13.sp)
                                            }
                                        },
                                        onClick = {
                                            showOptionsMenu = false
                                            viewModel.openThemeSelector()
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.NoteAdd, contentDescription = null, tint = palette.primaryAccent, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("New .np File Template", color = palette.textPrimary, fontSize = 13.sp)
                                            }
                                        },
                                        onClick = {
                                            showOptionsMenu = false
                                            viewModel.openNewFileDialog()
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Info, contentDescription = null, tint = palette.primaryAccent, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("Developer Credits & Spec", color = palette.textPrimary, fontSize = 13.sp)
                                            }
                                        },
                                        onClick = {
                                            showOptionsMenu = false
                                            viewModel.openDevCredits()
                                        }
                                    )
                                    HorizontalDivider(color = palette.border)
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = palette.textSecondary, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("Clear Terminal", color = palette.textPrimary, fontSize = 13.sp)
                                            }
                                        },
                                        onClick = {
                                            showOptionsMenu = false
                                            viewModel.clearTerminal()
                                        }
                                    )
                                }
                            }
                        }
                    }
                    HorizontalDivider(color = palette.border, thickness = 1.dp)

                    AnimatedVisibility(visible = state.saveNotification != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(palette.primaryAccent.copy(alpha = 0.15f))
                                .padding(horizontal = 14.dp, vertical = 5.dp)
                                .testTag("save_notification_banner")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Save,
                                    contentDescription = null,
                                    tint = palette.primaryAccent,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = state.saveNotification.orEmpty(),
                                    fontSize = 11.sp,
                                    color = palette.primaryAccent,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(palette.surface)
            ) {
                HorizontalDivider(color = palette.border, thickness = 1.dp)
                NavigationBar(
                    containerColor = palette.surface,
                    tonalElevation = 0.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                ) {
                    NavigationBarItem(
                        selected = state.currentTab == IdeTab.EDITOR,
                        onClick = { viewModel.selectTab(IdeTab.EDITOR) },
                        icon = { Icon(Icons.Default.Code, contentDescription = "Editor") },
                        label = { Text("Code", fontSize = 10.sp, fontWeight = FontWeight.Medium) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = palette.primaryAccent,
                            selectedTextColor = palette.primaryAccent,
                            unselectedIconColor = palette.textMuted,
                            unselectedTextColor = palette.textMuted,
                            indicatorColor = palette.surfaceVariant
                        ),
                        modifier = Modifier.testTag("tab_editor")
                    )

                    NavigationBarItem(
                        selected = state.currentTab == IdeTab.TERMINAL,
                        onClick = { viewModel.selectTab(IdeTab.TERMINAL) },
                        icon = {
                            if (state.isRunning) {
                                BadgedBox(
                                    badge = {
                                        Badge(containerColor = palette.errorUnderlineColor) {
                                            Text("●", fontSize = 8.sp, color = Color.White)
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.Terminal, contentDescription = "Terminal")
                                }
                            } else {
                                Icon(Icons.Default.Terminal, contentDescription = "Terminal")
                            }
                        },
                        label = { Text("Terminal", fontSize = 10.sp, fontWeight = FontWeight.Medium) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = palette.primaryAccent,
                            selectedTextColor = palette.primaryAccent,
                            unselectedIconColor = palette.textMuted,
                            unselectedTextColor = palette.textMuted,
                            indicatorColor = palette.surfaceVariant
                        ),
                        modifier = Modifier.testTag("tab_terminal")
                    )

                    NavigationBarItem(
                        selected = state.currentTab == IdeTab.WEB_AUTOMATION,
                        onClick = { viewModel.selectTab(IdeTab.WEB_AUTOMATION) },
                        icon = {
                            if (state.automationLogs.isNotEmpty()) {
                                BadgedBox(
                                    badge = {
                                        Badge(containerColor = palette.secondaryAccent) {
                                            Text("${state.automationLogs.size}", fontSize = 9.sp, color = Color.Black)
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.SmartToy, contentDescription = "Web & Auto")
                                }
                            } else {
                                Icon(Icons.Default.SmartToy, contentDescription = "Web & Auto")
                            }
                        },
                        label = { Text("Web/Auto", fontSize = 10.sp, fontWeight = FontWeight.Medium) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = palette.primaryAccent,
                            selectedTextColor = palette.primaryAccent,
                            unselectedIconColor = palette.textMuted,
                            unselectedTextColor = palette.textMuted,
                            indicatorColor = palette.surfaceVariant
                        ),
                        modifier = Modifier.testTag("tab_web_auto")
                    )

                    NavigationBarItem(
                        selected = state.currentTab == IdeTab.FILES,
                        onClick = { viewModel.selectTab(IdeTab.FILES) },
                        icon = { Icon(Icons.Default.Folder, contentDescription = "Files") },
                        label = { Text("Files", fontSize = 10.sp, fontWeight = FontWeight.Medium) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = palette.primaryAccent,
                            selectedTextColor = palette.primaryAccent,
                            unselectedIconColor = palette.textMuted,
                            unselectedTextColor = palette.textMuted,
                            indicatorColor = palette.surfaceVariant
                        ),
                        modifier = Modifier.testTag("tab_files")
                    )

                    NavigationBarItem(
                        selected = state.currentTab == IdeTab.NPPM,
                        onClick = { viewModel.selectTab(IdeTab.NPPM) },
                        icon = { Icon(Icons.Default.Inventory2, contentDescription = "NPPM") },
                        label = { Text("NPPM", fontSize = 10.sp, fontWeight = FontWeight.Medium) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = palette.primaryAccent,
                            selectedTextColor = palette.primaryAccent,
                            unselectedIconColor = palette.textMuted,
                            unselectedTextColor = palette.textMuted,
                            indicatorColor = palette.surfaceVariant
                        ),
                        modifier = Modifier.testTag("tab_nppm")
                    )

                    NavigationBarItem(
                        selected = state.currentTab == IdeTab.DOCS,
                        onClick = { viewModel.selectTab(IdeTab.DOCS) },
                        icon = { Icon(Icons.Default.Book, contentDescription = "Docs") },
                        label = { Text("Guide", fontSize = 10.sp, fontWeight = FontWeight.Medium) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = palette.primaryAccent,
                            selectedTextColor = palette.primaryAccent,
                            unselectedIconColor = palette.textMuted,
                            unselectedTextColor = palette.textMuted,
                            indicatorColor = palette.surfaceVariant
                        ),
                        modifier = Modifier.testTag("tab_docs")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (state.currentTab) {
                IdeTab.EDITOR -> {
                    EditorScreen(
                        activeFileName = state.activeFileName,
                        code = state.activeCode,
                        suggestions = state.suggestions,
                        lintIssues = state.lintIssues,
                        palette = palette,
                        fontSizeSp = state.fontSizeSp,
                        onCodeChange = { newCode, cursor -> viewModel.onCodeChanged(newCode, cursor) },
                        onApplySuggestion = { suggestion -> viewModel.applySuggestion(suggestion) },
                        onInsertQuickSymbol = { sym -> viewModel.insertQuickSymbol(sym) },
                        onIncreaseFontSize = { viewModel.increaseFontSize() },
                        onDecreaseFontSize = { viewModel.decreaseFontSize() },
                        onOpenThemeSelector = { viewModel.openThemeSelector() }
                    )
                }
                IdeTab.TERMINAL -> {
                    TerminalScreen(
                        entries = state.terminalEntries,
                        isRunning = state.isRunning,
                        isWaitingForInput = state.isWaitingForInput,
                        inputPrompt = state.inputPrompt,
                        onRunScript = { viewModel.runCurrentCode() },
                        onStopExecution = { viewModel.stopExecution() },
                        onSubmitRepl = { cmd -> viewModel.submitRepl(cmd) },
                        onSubmitInput = { input -> viewModel.submitInput(input) },
                        onClearTerminal = { viewModel.clearTerminal() }
                    )
                }
                IdeTab.WEB_AUTOMATION -> {
                    WebAutomationScreen(
                        currentUrl = state.webPreviewUrl,
                        webPageTitle = state.webPageTitle,
                        automationLogs = state.automationLogs,
                        onTriggerWebOpen = { url -> viewModel.onWebOpen(url) }
                    )
                }
                IdeTab.FILES -> {
                    FileManagerScreen(
                        files = state.files,
                        activeFileName = state.activeFileName,
                        palette = palette,
                        onSelectFile = { fileName -> viewModel.switchFile(fileName) },
                        onCreateNewFile = { name, content -> viewModel.createNewFile(name, content) },
                        onDeleteFile = { name -> viewModel.deleteFile(name) },
                        onRenameFile = { oldName, newName -> viewModel.renameFile(oldName, newName) },
                        onSaveFile = { name ->
                            if (name == state.activeFileName) {
                                viewModel.saveCurrentFile()
                            } else {
                                val f = state.files.find { it.name == name }
                                if (f != null) viewModel.saveFile(name, f.content)
                            }
                        }
                    )
                }
                IdeTab.NPPM -> {
                    PackageManagerScreen()
                }
                IdeTab.DOCS -> {
                    ReferenceScreen(
                        onLoadSnippet = { snippet ->
                            viewModel.createNewFile("snippet.np", snippet)
                        }
                    )
                }
            }
        }
    }
    }

    // Dialogs
    if (state.showThemeSelectorDialog) {
        ThemeSelectorDialog(
            currentMode = state.themeMode,
            currentPalette = palette,
            onSelectTheme = { mode ->
                viewModel.setTheme(mode)
            },
            onDismiss = { viewModel.closeThemeSelector() }
        )
    }

    if (state.showDevCreditsDialog) {
        DevCreditsDialog(
            palette = palette,
            onDismiss = { viewModel.closeDevCredits() },
            onInsertCreditsSnippet = {
                viewModel.insertDevCreditsSnippet()
            }
        )
    }

    if (state.showNewFileDialog) {
        NewNpFileDialog(
            palette = palette,
            onCreateFile = { name, content ->
                viewModel.createNewFile(name, content)
            },
            onDismiss = { viewModel.closeNewFileDialog() }
        )
    }

    if (state.showAiAssistantDialog && state.aiAssistantResponse != null) {
        AiAssistantDialog(
            response = state.aiAssistantResponse!!,
            palette = palette,
            onApply = { viewModel.applyAiSuggestedCode() },
            onDismiss = { viewModel.closeAiAssistant() }
        )
    }

    if (state.showCommandPalette) {
        IdeCommandPaletteDialog(
            palette = palette,
            query = state.commandPaletteQuery,
            onQueryChange = { q -> viewModel.updateCommandQuery(q) },
            commands = commands,
            onDismiss = { viewModel.closeCommandPalette() }
        )
    }

    if (state.showQuickExamplesSheet) {
        QuickExamplesDialog(
            files = state.files,
            activeFileName = state.activeFileName,
            palette = palette,
            onSelectFile = { fn -> viewModel.switchFile(fn) },
            onDismiss = { viewModel.closeQuickExamples() }
        )
    }
}
