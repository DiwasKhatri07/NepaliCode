package com.nepalicode.dev.nepalilang.core

data class LintIssue(
    val line: Int,
    val column: Int,
    val severity: LintSeverity,
    val message: String,
    val suggestion: String
)

enum class LintSeverity {
    ERROR, WARNING, INFO
}

object NepaliLinter {
    fun lint(source: String): List<LintIssue> {
        val issues = mutableListOf<LintIssue>()
        try {
            val lexer = NepaliLexer(source)
            val tokens = lexer.tokenize()
            val parser = NepaliParser(tokens)
            parser.parse()
        } catch (e: NepaliParseException) {
            issues.add(
                LintIssue(
                    line = e.line,
                    column = e.column,
                    severity = LintSeverity.ERROR,
                    message = e.message,
                    suggestion = e.suggestion
                )
            )
        } catch (e: Exception) {
            issues.add(
                LintIssue(
                    line = 1,
                    column = 1,
                    severity = LintSeverity.ERROR,
                    message = e.message ?: "Syntax check failed",
                    suggestion = "Check code structure and matching brackets."
                )
            )
        }

        // Additional static checks
        val lines = source.lines()
        for ((idx, line) in lines.withIndex()) {
            val lineNum = idx + 1
            val trimmed = line.trim()
            if (trimmed.startsWith("print ") || trimmed.startsWith("dekha ")) {
                issues.add(
                    LintIssue(
                        line = lineNum,
                        column = 1,
                        severity = LintSeverity.WARNING,
                        message = "print is a function in NepaliLang",
                        suggestion = "Use parentheses like print(\"...\") or dekha(\"...\")"
                    )
                )
            }
            if (trimmed.startsWith("if ") && !trimmed.endsWith(":") && !trimmed.contains("#")) {
                issues.add(
                    LintIssue(
                        line = lineNum,
                        column = line.length,
                        severity = LintSeverity.ERROR,
                        message = "Missing ':' after condition",
                        suggestion = "Add ':' at end of line (e.g. if x > 0:)"
                    )
                )
            }
            if (trimmed.startsWith("yedi ") && !trimmed.endsWith(":") && !trimmed.contains("#")) {
                issues.add(
                    LintIssue(
                        line = lineNum,
                        column = line.length,
                        severity = LintSeverity.ERROR,
                        message = "Missing ':' after yedi condition",
                        suggestion = "Add ':' at end of line (e.g. yedi x > 0:)"
                    )
                )
            }
            if (trimmed.startsWith("def ") && !trimmed.endsWith(":") && !trimmed.contains("#")) {
                issues.add(
                    LintIssue(
                        line = lineNum,
                        column = line.length,
                        severity = LintSeverity.ERROR,
                        message = "Missing ':' after function definition",
                        suggestion = "Add ':' at end of line (e.g. def foo(): or kaam foo():)"
                    )
                )
            }
            if (trimmed.startsWith("kaam ") && !trimmed.endsWith(":") && !trimmed.contains("#")) {
                issues.add(
                    LintIssue(
                        line = lineNum,
                        column = line.length,
                        severity = LintSeverity.ERROR,
                        message = "Missing ':' after kaam definition",
                        suggestion = "Add ':' at end of line (e.g. kaam jod(a, b):)"
                    )
                )
            }
        }

        return issues
    }

    fun format(source: String): String {
        val lines = source.lines()
        val result = mutableListOf<String>()
        var indentLevel = 0

        for (rawLine in lines) {
            val trimmed = rawLine.trim()
            if (trimmed.isEmpty()) {
                result.add("")
                continue
            }

            // Check if dedenting line like natra:, else:, except:, samau:, antya:, etc.
            val dedentKeywords = listOf("else:", "natra:", "elif ", "natabhaye ", "except:", "samau:", "finally:", "antya:")
            val shouldTemporarilyDedent = dedentKeywords.any { trimmed.startsWith(it) }

            val currentIndent = if (shouldTemporarilyDedent && indentLevel > 0) indentLevel - 1 else indentLevel
            val indentStr = "    ".repeat(maxOf(0, currentIndent))

            // Standardize spaces around common operators if simple
            result.add(indentStr + trimmed)

            if (trimmed.endsWith(":")) {
                indentLevel++
            } else if (trimmed.startsWith("return ") || trimmed.startsWith("firta ") || trimmed == "pass") {
                // Return often concludes a branch, but we don't automatically dedent without scope
            }
        }

        return result.joinToString("\n")
    }
}
