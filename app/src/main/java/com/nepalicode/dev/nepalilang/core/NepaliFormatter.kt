package com.nepalicode.dev.nepalilang.core

object NepaliFormatter {

    fun format(source: String): String {
        val lines = source.lines()
        val formattedLines = mutableListOf<String>()
        var indentLevel = 0

        for (rawLine in lines) {
            val trimmed = rawLine.trim()

            if (trimmed.isEmpty()) {
                formattedLines.add("")
                continue
            }

            // Dedent keywords
            if (trimmed.startsWith("natra:") || trimmed.startsWith("else:") ||
                trimmed.startsWith("natra ") || trimmed.startsWith("else ") ||
                trimmed.startsWith("natabhaye ") || trimmed.startsWith("athawa ") ||
                trimmed.startsWith("elif ") || trimmed.startsWith("samau ") ||
                trimmed.startsWith("except ") || trimmed.startsWith("antya:") ||
                trimmed.startsWith("finally:")
            ) {
                val currentIndent = (indentLevel - 1).coerceAtLeast(0)
                val lineFormatted = formatLineTokens(trimmed)
                formattedLines.add("    ".repeat(currentIndent) + lineFormatted)
                if (trimmed.endsWith(":")) {
                    // stays at indentLevel for inner block
                }
                continue
            }

            // Handle dedent if line was indented but previous block closed
            val lineFormatted = formatLineTokens(trimmed)
            formattedLines.add("    ".repeat(indentLevel) + lineFormatted)

            if (trimmed.endsWith(":")) {
                indentLevel++
            } else if (trimmed.startsWith("firta ") || trimmed.startsWith("return ") ||
                trimmed == "firta" || trimmed == "return" ||
                trimmed == "rok" || trimmed == "break" ||
                trimmed == "agadi" || trimmed == "continue"
            ) {
                // Return/break at end of single block might decrease indent if next line isn't indented
            }
        }

        // Post-process indentation consistency
        return normalizeIndentation(formattedLines.joinToString("\n"))
    }

    private fun formatLineTokens(line: String): String {
        if (line.startsWith("#")) return line

        val commentIdx = findCommentIndex(line)
        val codePart = if (commentIdx != -1) line.substring(0, commentIdx) else line
        val commentPart = if (commentIdx != -1) line.substring(commentIdx) else ""

        var formatted = codePart

        // Normalize commas: comma followed by space
        formatted = formatted.replace(Regex(",\\s*"), ", ")

        // Normalize binary operator spaces
        val operators = listOf("==", "!=", "<=", ">=", "+=", "-=", "*=", "/=", " = ", " + ", " - ", " * ", " / ", " % ", " < ", " > ")
        // Ensure single space around comparison operators
        formatted = formatted.replace(Regex("\\s*==\\s*"), " == ")
        formatted = formatted.replace(Regex("\\s*!=\\s*"), " != ")
        formatted = formatted.replace(Regex("\\s*<=\\s*"), " <= ")
        formatted = formatted.replace(Regex("\\s*>=\\s*"), " >= ")
        formatted = formatted.replace(Regex("(?<=[^=!<>\n ])=(?=[^=])"), " = ")

        // Clean up redundant spaces around parentheses
        formatted = formatted.replace(Regex("\\(\\s+"), "(")
        formatted = formatted.replace(Regex("\\s+\\)"), ")")
        formatted = formatted.replace(Regex("\\[\\s+"), "[")
        formatted = formatted.replace(Regex("\\s+\\]"), "]")

        // Ensure colon at end has no preceding space
        formatted = formatted.replace(Regex("\\s+:$"), ":")

        // Combine with comment
        return if (commentPart.isNotEmpty()) {
            "${formatted.trimEnd()}  $commentPart"
        } else {
            formatted.trimEnd()
        }
    }

    private fun findCommentIndex(line: String): Int {
        var inSingleQuote = false
        var inDoubleQuote = false
        for (i in line.indices) {
            val c = line[i]
            if (c == '\'' && !inDoubleQuote) inSingleQuote = !inSingleQuote
            else if (c == '"' && !inSingleQuote) inDoubleQuote = !inDoubleQuote
            else if (c == '#' && !inSingleQuote && !inDoubleQuote) return i
        }
        return -1
    }

    private fun normalizeIndentation(code: String): String {
        val lines = code.lines()
        val result = mutableListOf<String>()
        var expectedIndent = 0

        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isEmpty()) {
                result.add("")
                continue
            }

            // Detect if dedent keyword
            val isDedent = trimmed.startsWith("natra") || trimmed.startsWith("else") ||
                    trimmed.startsWith("athawa") || trimmed.startsWith("natabhaye") ||
                    trimmed.startsWith("elif") || trimmed.startsWith("samau") ||
                    trimmed.startsWith("except") || trimmed.startsWith("antya") ||
                    trimmed.startsWith("finally")

            val lineIndent = if (isDedent) (expectedIndent - 1).coerceAtLeast(0) else expectedIndent
            result.add("    ".repeat(lineIndent) + trimmed)

            if (trimmed.endsWith(":")) {
                expectedIndent = lineIndent + 1
            }
        }

        return result.joinToString("\n").trimEnd() + "\n"
    }
}
