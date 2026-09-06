package com.nepalicode.dev.nepalicode.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nepalicode.dev.nepalicode.editor.CodeSuggestion
import com.nepalicode.dev.nepalicode.editor.IdeThemePalette
import com.nepalicode.dev.nepalicode.editor.IdeThemeRegistry
import com.nepalicode.dev.nepalicode.editor.NepaliCodeTextField
import com.nepalicode.dev.nepalicode.editor.SuggestionCategory
import com.nepalicode.dev.nepalilang.core.LintIssue
import com.nepalicode.dev.nepalilang.core.LintSeverity

@Composable
fun EditorScreen(
    activeFileName: String,
    code: String,
    suggestions: List<CodeSuggestion>,
    lintIssues: List<LintIssue> = emptyList(),
    palette: IdeThemePalette = IdeThemeRegistry.HighDensityDark,
    fontSizeSp: Float = 13f,
    onCodeChange: (String, Int) -> Unit,
    onApplySuggestion: (CodeSuggestion) -> Unit,
    onInsertQuickSymbol: (String) -> Unit,
    onIncreaseFontSize: () -> Unit = {},
    onDecreaseFontSize: () -> Unit = {},
    onOpenThemeSelector: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var textFieldValue by remember(activeFileName) {
        mutableStateOf(TextFieldValue(text = code, selection = TextRange(0)))
    }

    // Keep internal state synced if code was modified externally
    if (textFieldValue.text != code) {
        textFieldValue = textFieldValue.copy(text = code)
    }

    var showDiagnosticsSheet by remember { mutableStateOf(false) }

    val quickSymbols = listOf(
        "    " to "Tab",
        ":" to ":",
        "=" to "=",
        "(" to "(",
        ")" to ")",
        "[" to "[",
        "]" to "]",
        "{" to "{",
        "}" to "}",
        "\"" to "\"",
        "'" to "'",
        "+" to "+",
        "-" to "-",
        "*" to "*",
        "/" to "/",
        "#" to "#",
        "kaam " to "kaam",
        "firta " to "firta",
        "yedi " to "yedi",
        "natra:" to "natra",
        "yo(" to "yo()",
        "print(" to "print()",
        "dekha(" to "dekha()",
        "anurodh." to "anurodh",
        "web." to "web",
        "browser." to "browser",
        "automation." to "automation",
        "database." to "database"
    )

    val errorCount = lintIssues.count { it.severity == LintSeverity.ERROR }
    val warningCount = lintIssues.count { it.severity == LintSeverity.WARNING }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background)
            .imePadding()
    ) {
        // Sub-Header: Active File Indicator, File Stats, Font Size Controls, Theme Switcher
        Surface(
            color = palette.surface,
            tonalElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(palette.surfaceVariant, RoundedCornerShape(6.dp))
                            .border(0.5.dp, palette.border, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = activeFileName,
                                color = palette.primaryAccent,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily.Monospace
                            )
                            if (activeFileName.endsWith(".np")) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .background(palette.primaryAccent.copy(alpha = 0.2f), CircleShape)
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "NP",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = palette.primaryAccent
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${code.lines().size}L • ${code.length}B",
                        color = palette.textMuted,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Controls: Font Size (+ / -) and Theme Palette Icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Zoom out font
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = palette.surfaceVariant,
                        modifier = Modifier
                            .size(26.dp)
                            .clickable { onDecreaseFontSize() }
                            .testTag("font_size_decrease_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("-", color = palette.textPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Text(
                        text = "${fontSizeSp.toInt()}sp",
                        color = palette.textMuted,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )

                    // Zoom in font
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = palette.surfaceVariant,
                        modifier = Modifier
                            .size(26.dp)
                            .clickable { onIncreaseFontSize() }
                            .testTag("font_size_increase_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("+", color = palette.textPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Theme Picker Quick Action
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = palette.surfaceVariant,
                        modifier = Modifier
                            .size(26.dp)
                            .clickable { onOpenThemeSelector() }
                            .testTag("editor_theme_picker_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = "Theme Palette",
                                tint = palette.primaryAccent,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }
        }

        HorizontalDivider(color = palette.border, thickness = 1.dp)

        // Main Editor Surface with Live Syntax Highlighting and Gutter
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            NepaliCodeTextField(
                value = textFieldValue,
                onValueChange = { newValue ->
                    textFieldValue = newValue
                    onCodeChange(newValue.text, newValue.selection.start)
                },
                palette = palette,
                lintIssues = lintIssues,
                fontSizeSp = fontSizeSp,
                onErrorLineClick = { issue ->
                    showDiagnosticsSheet = true
                }
            )

            // Floating Autocompletion Popup if suggestions are available
            if (suggestions.isNotEmpty()) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                        .testTag("code_suggestions_panel"),
                    shape = RoundedCornerShape(10.dp),
                    color = palette.surface,
                    border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(palette.border)),
                    shadowElevation = 8.dp
                ) {
                    Column(modifier = Modifier.padding(6.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Auto Suggestions",
                                tint = palette.primaryAccent,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "SUGGESTIONS & SNIPPETS",
                                color = palette.textMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                        ) {
                            items(suggestions) { item ->
                                SuggestionChip(
                                    suggestion = item,
                                    palette = palette,
                                    isFirst = item == suggestions.first(),
                                    onClick = { onApplySuggestion(item) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Live Error Diagnostics Bar
        Surface(
            color = palette.surface,
            tonalElevation = 2.dp,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    if (lintIssues.isNotEmpty()) {
                        showDiagnosticsSheet = !showDiagnosticsSheet
                    }
                }
                .testTag("editor_diagnostics_bar")
        ) {
            Column {
                HorizontalDivider(color = palette.border, thickness = 0.5.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (errorCount > 0) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "Errors",
                                tint = palette.errorUnderlineColor,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "$errorCount Error${if (errorCount > 1) "s" else ""}" +
                                        if (warningCount > 0) ", $warningCount Warning${if (warningCount > 1) "s" else ""}" else "",
                                color = palette.errorUnderlineColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else if (warningCount > 0) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Warnings",
                                tint = palette.secondaryAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "$warningCount Warning${if (warningCount > 1) "s" else ""}",
                                color = palette.secondaryAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Syntax OK",
                                tint = palette.secondaryAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Syntax Clean (.np)",
                                color = palette.textSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    if (lintIssues.isNotEmpty()) {
                        Text(
                            text = if (showDiagnosticsSheet) "Hide Details ▲" else "View Issues ▼",
                            color = palette.primaryAccent,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Expanded Diagnostics List
                AnimatedVisibility(
                    visible = showDiagnosticsSheet && lintIssues.isNotEmpty(),
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(palette.background)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        for (issue in lintIssues) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (issue.severity == LintSeverity.ERROR) palette.errorBackgroundColor
                                        else palette.surfaceVariant,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Line ${issue.line}:${issue.column}",
                                    color = palette.primaryAccent,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = issue.message,
                                    color = palette.textPrimary,
                                    fontSize = 11.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                if (issue.suggestion != null) {
                                    Text(
                                        text = "💡 ${issue.suggestion}",
                                        color = palette.textMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Quick Accessory Toolbar (Tabs, brackets, operators, keywords)
        Surface(
            color = palette.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                HorizontalDivider(color = palette.border, thickness = 0.5.dp)
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(quickSymbols) { (sym, label) ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = palette.surfaceVariant,
                            modifier = Modifier
                                .height(36.dp)
                                .clickable { onInsertQuickSymbol(sym) }
                                .border(0.5.dp, palette.border, RoundedCornerShape(8.dp))
                                .testTag("quick_symbol_$label")
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(horizontal = 10.dp)
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = when {
                                        label in listOf("kaam", "def") -> palette.defKeywordColor
                                        label in listOf("firta", "yedi", "natra") -> palette.keywordColor
                                        label.endsWith("()") || label.endsWith(".") -> palette.builtinColor
                                        else -> palette.textSecondary
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SuggestionChip(
    suggestion: CodeSuggestion,
    palette: IdeThemePalette,
    isFirst: Boolean = false,
    onClick: () -> Unit
) {
    val categoryColor = when (suggestion.category) {
        SuggestionCategory.KEYWORD_NEPALI -> palette.keywordColor
        SuggestionCategory.KEYWORD_ENGLISH -> palette.keywordColor
        SuggestionCategory.BUILTIN -> palette.builtinColor
        SuggestionCategory.STDLIB_MODULE -> palette.stdLibColor
        SuggestionCategory.METHOD -> palette.defKeywordColor
        SuggestionCategory.SNIPPET -> palette.secondaryAccent
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isFirst) palette.surfaceVariant else palette.surfaceVariant.copy(alpha = 0.5f),
        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(if (isFirst) palette.primaryAccent.copy(alpha = 0.6f) else palette.border)),
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag("suggestion_${suggestion.label.take(10)}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(
                text = suggestion.iconText.take(2),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = categoryColor,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = suggestion.label,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = if (isFirst) FontWeight.Bold else FontWeight.Medium,
                    color = if (isFirst) palette.textPrimary else palette.textSecondary
                )
                if (suggestion.detail.isNotEmpty()) {
                    Text(
                        text = suggestion.detail,
                        fontSize = 9.sp,
                        color = palette.textMuted
                    )
                }
            }
        }
    }
}
