package com.nepalicode.dev.nepalicode.editor

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.nepalicode.dev.nepalilang.core.LintIssue

object NepaliSyntaxHighlighter {

    // Default aliases matching High Density Dark
    val KeywordColor = IdeThemeRegistry.HighDensityDark.keywordColor
    val DefKeywordColor = IdeThemeRegistry.HighDensityDark.defKeywordColor
    val BuiltinColor = IdeThemeRegistry.HighDensityDark.builtinColor
    val StdLibColor = IdeThemeRegistry.HighDensityDark.stdLibColor
    val StringColor = IdeThemeRegistry.HighDensityDark.stringColor
    val NumberColor = IdeThemeRegistry.HighDensityDark.numberColor
    val CommentColor = IdeThemeRegistry.HighDensityDark.commentColor
    val OperatorColor = IdeThemeRegistry.HighDensityDark.operatorColor
    val IdentifierColor = IdeThemeRegistry.HighDensityDark.textPrimary

    val NepaliKeywordColor = KeywordColor
    val EnglishKeywordColor = KeywordColor

    fun highlight(
        code: String,
        palette: IdeThemePalette = IdeThemeRegistry.HighDensityDark,
        lintIssues: List<LintIssue> = emptyList()
    ): AnnotatedString {
        if (code.isEmpty()) return buildAnnotatedString { append("") }

        val tokenizer = NepaliTokenizer(code)
        val (tokens, diagnostics) = tokenizer.tokenize()

        val builder = AnnotatedString.Builder(code)

        // 1. Color tokens based on tokenizer output
        for (token in tokens) {
            val start = token.start.coerceIn(0, code.length)
            val end = token.end.coerceIn(0, code.length)
            if (start >= end) continue

            when (token.type) {
                HighlightTokenType.KEYWORD_NEPALI -> {
                    builder.addStyle(
                        SpanStyle(color = palette.keywordColor, fontWeight = FontWeight.Bold),
                        start, end
                    )
                }
                HighlightTokenType.KEYWORD_ENGLISH -> {
                    builder.addStyle(
                        SpanStyle(color = palette.keywordColor, fontWeight = FontWeight.Bold),
                        start, end
                    )
                }
                HighlightTokenType.DEF_KEYWORD -> {
                    builder.addStyle(
                        SpanStyle(color = palette.defKeywordColor, fontWeight = FontWeight.Bold),
                        start, end
                    )
                }
                HighlightTokenType.FUNCTION_NAME -> {
                    builder.addStyle(
                        SpanStyle(color = palette.defKeywordColor, fontWeight = FontWeight.SemiBold),
                        start, end
                    )
                }
                HighlightTokenType.STRING -> {
                    builder.addStyle(
                        SpanStyle(color = palette.stringColor),
                        start, end
                    )
                }
                HighlightTokenType.NUMBER -> {
                    builder.addStyle(
                        SpanStyle(color = palette.numberColor, fontWeight = FontWeight.SemiBold),
                        start, end
                    )
                }
                HighlightTokenType.COMMENT -> {
                    builder.addStyle(
                        SpanStyle(color = palette.commentColor, fontStyle = FontStyle.Italic),
                        start, end
                    )
                }
                HighlightTokenType.BUILTIN -> {
                    builder.addStyle(
                        SpanStyle(color = palette.builtinColor, fontWeight = FontWeight.SemiBold),
                        start, end
                    )
                }
                HighlightTokenType.STDLIB_MODULE -> {
                    builder.addStyle(
                        SpanStyle(color = palette.stdLibColor, fontWeight = FontWeight.SemiBold),
                        start, end
                    )
                }
                HighlightTokenType.OPERATOR -> {
                    builder.addStyle(
                        SpanStyle(color = palette.operatorColor),
                        start, end
                    )
                }
                HighlightTokenType.SYNTAX_ERROR -> {
                    builder.addStyle(
                        SpanStyle(
                            color = palette.errorUnderlineColor,
                            background = palette.errorBackgroundColor,
                            textDecoration = TextDecoration.Underline,
                            fontWeight = FontWeight.Bold
                        ),
                        start, end
                    )
                }
                HighlightTokenType.PUNCTUATION -> {
                    builder.addStyle(
                        SpanStyle(color = palette.operatorColor),
                        start, end
                    )
                }
                HighlightTokenType.IDENTIFIER -> {
                    builder.addStyle(
                        SpanStyle(color = palette.textPrimary),
                        start, end
                    )
                }
                HighlightTokenType.WHITESPACE -> {
                    // No styling needed
                }
            }
        }

        // 2. Add error spans for any diagnostics detected by the tokenizer
        for (diag in diagnostics) {
            val s = diag.startOffset.coerceIn(0, code.length)
            val e = diag.endOffset.coerceIn(0, code.length)
            if (s < e) {
                builder.addStyle(
                    SpanStyle(
                        color = palette.errorUnderlineColor,
                        background = palette.errorBackgroundColor,
                        textDecoration = TextDecoration.Underline
                    ),
                    s, e
                )
            }
        }

        // 3. Add error spans for any lint issues passed in
        val lines = code.lines()
        var currentLineStart = 0
        for ((lineIdx, lineText) in lines.withIndex()) {
            val lineNum = lineIdx + 1
            val matchingIssues = lintIssues.filter { it.line == lineNum }
            for (issue in matchingIssues) {
                val start = (currentLineStart + (issue.column - 1).coerceAtLeast(0)).coerceIn(0, code.length)
                val end = (currentLineStart + lineText.length).coerceIn(start, code.length)
                if (start < end) {
                    builder.addStyle(
                        SpanStyle(
                            color = palette.errorUnderlineColor,
                            background = palette.errorBackgroundColor,
                            textDecoration = TextDecoration.Underline
                        ),
                        start, end
                    )
                }
            }
            currentLineStart += lineText.length + 1 // +1 for '\n'
        }

        return builder.toAnnotatedString()
    }
}
