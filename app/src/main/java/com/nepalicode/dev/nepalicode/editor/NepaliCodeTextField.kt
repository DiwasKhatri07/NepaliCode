package com.nepalicode.dev.nepalicode.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nepalicode.dev.nepalilang.core.LintIssue
import com.nepalicode.dev.nepalilang.core.LintSeverity

@Composable
fun NepaliCodeTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    palette: IdeThemePalette,
    lintIssues: List<LintIssue> = emptyList(),
    fontSizeSp: Float = 13f,
    modifier: Modifier = Modifier,
    onErrorLineClick: ((LintIssue) -> Unit)? = null
) {
    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    val lines = remember(value.text) { value.text.lines() }
    val lineCount = maxOf(1, lines.size)

    // Calculate current cursor line (1-indexed)
    val cursorIndex = value.selection.start.coerceIn(0, value.text.length)
    val currentLineNumber = remember(value.text, cursorIndex) {
        var count = 1
        for (i in 0 until cursorIndex) {
            if (value.text[i] == '\n') count++
        }
        count
    }

    val errorLinesMap = remember(lintIssues) {
        lintIssues.associateBy { it.line }
    }

    val lineHeight = (fontSizeSp * 1.55f).sp

    Row(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background)
            .verticalScroll(verticalScrollState)
            .testTag("nepali_code_text_field_container")
    ) {
        // Line Numbers Gutter
        Column(
            modifier = Modifier
                .width(44.dp)
                .background(palette.background)
                .padding(top = 12.dp, bottom = 12.dp, end = 6.dp)
                .testTag("editor_gutter"),
            horizontalAlignment = Alignment.End
        ) {
            for (lineNum in 1..lineCount) {
                val hasError = errorLinesMap.containsKey(lineNum)
                val isCurrentLine = lineNum == currentLineNumber
                val issue = errorLinesMap[lineNum]

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable(enabled = hasError) {
                            if (issue != null && onErrorLineClick != null) {
                                onErrorLineClick(issue)
                            }
                        }
                ) {
                    if (hasError) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(
                                    if (issue?.severity == LintSeverity.ERROR) palette.errorUnderlineColor else palette.secondaryAccent,
                                    CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                    }

                    Text(
                        text = "$lineNum",
                        color = if (hasError) palette.errorUnderlineColor
                        else if (isCurrentLine) palette.primaryAccent
                        else palette.lineNumberColor,
                        fontSize = fontSizeSp.sp,
                        fontWeight = if (isCurrentLine) FontWeight.Bold else FontWeight.Normal,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = lineHeight
                    )
                }
            }
        }

        // Gutter Border
        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(palette.border)
        )

        // Text Field with Live Custom Tokenizer & Syntax Highlighting
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 12.dp)
                .horizontalScroll(horizontalScrollState)
                .testTag("nepali_code_editor"),
            textStyle = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = fontSizeSp.sp,
                color = palette.textPrimary,
                lineHeight = lineHeight
            ),
            cursorBrush = SolidColor(palette.primaryAccent),
            visualTransformation = VisualTransformation { text ->
                val annotated = NepaliSyntaxHighlighter.highlight(text.text, palette, lintIssues)
                TransformedText(annotated, OffsetMapping.Identity)
            }
        )
    }
}

/**
 * Basic code editor component using Jetpack Compose that supports
 * multi-line text input and basic line numbering for NepaliLang code editing.
 */
@Composable
fun NepaliCodeEditor(
    code: String,
    onCodeChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    palette: IdeThemePalette = IdeThemeRegistry.HighDensityDark,
    fontSizeSp: Float = 13f,
    lintIssues: List<LintIssue> = emptyList()
) {
    var textFieldValue by remember(code) {
        mutableStateOf(
            TextFieldValue(text = code, selection = androidx.compose.ui.text.TextRange(code.length))
        )
    }

    NepaliCodeTextField(
        value = textFieldValue,
        onValueChange = { newValue ->
            textFieldValue = newValue
            if (newValue.text != code) {
                onCodeChange(newValue.text)
            }
        },
        palette = palette,
        lintIssues = lintIssues,
        fontSizeSp = fontSizeSp,
        modifier = modifier
    )
}

