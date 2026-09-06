package com.nepalicode.dev.nepalicode.editor

enum class HighlightTokenType {
    KEYWORD_NEPALI,
    KEYWORD_ENGLISH,
    DEF_KEYWORD,
    FUNCTION_NAME,
    STRING,
    NUMBER,
    COMMENT,
    BUILTIN,
    STDLIB_MODULE,
    OPERATOR,
    PUNCTUATION,
    IDENTIFIER,
    SYNTAX_ERROR,
    WHITESPACE
}

data class HighlightToken(
    val type: HighlightTokenType,
    val text: String,
    val start: Int,
    val end: Int,
    val errorMessage: String? = null
)

data class SyntaxDiagnostic(
    val line: Int,
    val column: Int,
    val startOffset: Int,
    val endOffset: Int,
    val message: String,
    val isError: Boolean = true
)

class NepaliTokenizer(private val source: String) {

    private var cursor = 0
    private val length = source.length
    private val tokens = mutableListOf<HighlightToken>()
    private val diagnostics = mutableListOf<SyntaxDiagnostic>()

    companion object {
        val NEPALI_KEYWORDS = setOf(
            "yedi", "natra", "natabhaye", "jabasamma", "ko_lagi", "kaam", "firta",
            "kakshya", "lyau", "bata", "koshish", "samau", "antya", "uthau", "bhitra",
            "ma", "ra", "wa", "athawa", "hoina", "sacho", "jhut", "khali",
            "rok", "jaari", "chhoda", "shabda", "ank", "dashamlav", "suchi", "kosh", "bool"
        )

        val ENGLISH_KEYWORDS = setOf(
            "if", "else", "elif", "for", "while", "def", "return", "class", "import", "from",
            "try", "except", "finally", "raise", "with", "in", "is", "and", "or", "not",
            "True", "False", "None", "assert", "break", "continue", "pass"
        )

        val DEF_KEYWORDS = setOf("def", "kaam")

        val BUILTIN_FUNCTIONS = setOf(
            "print", "dekha", "input", "leu", "len", "lambai", "yo", "type", "prakaar", "prakar",
            "range", "daura", "str", "shabda", "number", "ank", "decimal", "dashamlav",
            "bool", "satya_jhut", "list", "suchi", "map", "kosh",
            "sum", "jod", "jod_sabai", "min", "max", "abs", "round", "sorted", "kram", "ulta",
            "extract", "extract_links", "extract_text", "extract_table", "khoj"
        )

        val STDLIB_MODULES = setOf(
            "anurodh", "web", "browser", "automation", "file", "folder", "system",
            "process", "ganit", "samaya", "json", "random", "database", "terminal",
            "log", "config", "env", "regex", "khoj", "csv", "archive", "email", "task"
        )

        val OPERATORS = setOf(
            "+", "-", "*", "/", "%", "**", "//",
            "==", "!=", "<", ">", "<=", ">=", "=", "+=", "-=", "*=", "/=",
            "->", "&&", "||", "!"
        )
    }

    fun tokenize(): Pair<List<HighlightToken>, List<SyntaxDiagnostic>> {
        cursor = 0
        tokens.clear()
        diagnostics.clear()

        var previousToken: HighlightToken? = null

        while (cursor < length) {
            val start = cursor
            val c = source[cursor]

            when {
                // 1. Whitespace
                c.isWhitespace() -> {
                    while (cursor < length && source[cursor].isWhitespace()) {
                        cursor++
                    }
                    val text = source.substring(start, cursor)
                    tokens.add(HighlightToken(HighlightTokenType.WHITESPACE, text, start, cursor))
                }

                // 2. Comments: # to end of line
                c == '#' -> {
                    while (cursor < length && source[cursor] != '\n') {
                        cursor++
                    }
                    val text = source.substring(start, cursor)
                    val token = HighlightToken(HighlightTokenType.COMMENT, text, start, cursor)
                    tokens.add(token)
                    previousToken = token
                }

                // 3. Strings: """...""", '''...''', "...", '...', f"..."
                c == '"' || c == '\'' || ((c == 'f' || c == 'F') && cursor + 1 < length && (source[cursor + 1] == '"' || source[cursor + 1] == '\'')) -> {
                    val token = scanString(start)
                    tokens.add(token)
                    previousToken = token
                }

                // 4. Numbers
                c.isDigit() || (c == '.' && cursor + 1 < length && source[cursor + 1].isDigit()) -> {
                    val token = scanNumber(start)
                    tokens.add(token)
                    previousToken = token
                }

                // 5. Identifiers, Keywords, Builtins
                isIdentifierStart(c) -> {
                    val token = scanIdentifier(start, previousToken)
                    tokens.add(token)
                    previousToken = token
                }

                // 6. Two-char or single-char operators
                else -> {
                    val token = scanOperatorOrPunctuation(start)
                    tokens.add(token)
                    previousToken = token
                }
            }
        }

        return Pair(tokens, diagnostics)
    }

    private fun scanString(start: Int): HighlightToken {
        var isFormatted = false
        if (source[cursor] == 'f' || source[cursor] == 'F') {
            isFormatted = true
            cursor++
        }

        val quoteChar = source[cursor]
        // Check multiline quotes: """ or '''
        val isTriple = cursor + 2 < length &&
                source[cursor + 1] == quoteChar &&
                source[cursor + 2] == quoteChar

        if (isTriple) {
            cursor += 3
            var closed = false
            while (cursor + 2 < length) {
                if (source[cursor] == '\\') {
                    cursor += 2
                    continue
                }
                if (source[cursor] == quoteChar && source[cursor + 1] == quoteChar && source[cursor + 2] == quoteChar) {
                    cursor += 3
                    closed = true
                    break
                }
                cursor++
            }
            if (!closed) {
                cursor = length
                val text = source.substring(start, cursor)
                val diag = createDiagnostic(start, cursor, "Unclosed multi-line string literal")
                diagnostics.add(diag)
                return HighlightToken(HighlightTokenType.SYNTAX_ERROR, text, start, cursor, "Unclosed string")
            }
            val text = source.substring(start, cursor)
            return HighlightToken(HighlightTokenType.STRING, text, start, cursor)
        } else {
            cursor++ // skip opening quote
            var closed = false
            while (cursor < length) {
                val ch = source[cursor]
                if (ch == '\\') {
                    cursor += 2
                    continue
                }
                if (ch == quoteChar) {
                    cursor++
                    closed = true
                    break
                }
                if (ch == '\n') {
                    break // Single-line strings cannot span unescaped newlines
                }
                cursor++
            }
            val text = source.substring(start, cursor)
            if (!closed) {
                val diag = createDiagnostic(start, cursor, "Unclosed string literal")
                diagnostics.add(diag)
                return HighlightToken(HighlightTokenType.SYNTAX_ERROR, text, start, cursor, "Unclosed string literal")
            }
            return HighlightToken(HighlightTokenType.STRING, text, start, cursor)
        }
    }

    private fun scanNumber(start: Int): HighlightToken {
        if (source[start] == '0' && cursor + 1 < length) {
            val next = source[cursor + 1]
            if (next == 'x' || next == 'X') { // Hexadecimal
                cursor += 2
                while (cursor < length && isHexDigit(source[cursor])) {
                    cursor++
                }
                val text = source.substring(start, cursor)
                return HighlightToken(HighlightTokenType.NUMBER, text, start, cursor)
            } else if (next == 'b' || next == 'B') { // Binary
                cursor += 2
                while (cursor < length && (source[cursor] == '0' || source[cursor] == '1')) {
                    cursor++
                }
                val text = source.substring(start, cursor)
                return HighlightToken(HighlightTokenType.NUMBER, text, start, cursor)
            }
        }

        // Standard decimal / float
        while (cursor < length && source[cursor].isDigit()) {
            cursor++
        }
        if (cursor < length && source[cursor] == '.' && cursor + 1 < length && source[cursor + 1].isDigit()) {
            cursor++ // skip dot
            while (cursor < length && source[cursor].isDigit()) {
                cursor++
            }
        }
        // Exponent notation e.g. 1e10
        if (cursor < length && (source[cursor] == 'e' || source[cursor] == 'E')) {
            cursor++
            if (cursor < length && (source[cursor] == '+' || source[cursor] == '-')) {
                cursor++
            }
            while (cursor < length && source[cursor].isDigit()) {
                cursor++
            }
        }

        val text = source.substring(start, cursor)
        return HighlightToken(HighlightTokenType.NUMBER, text, start, cursor)
    }

    private fun scanIdentifier(start: Int, previousToken: HighlightToken?): HighlightToken {
        while (cursor < length && isIdentifierPart(source[cursor])) {
            cursor++
        }
        val text = source.substring(start, cursor)

        // Check if preceding token was 'def' or 'kaam'
        val isAfterDef = previousToken?.type == HighlightTokenType.DEF_KEYWORD

        val tokenType = when {
            isAfterDef -> HighlightTokenType.FUNCTION_NAME
            DEF_KEYWORDS.contains(text) -> HighlightTokenType.DEF_KEYWORD
            NEPALI_KEYWORDS.contains(text) -> HighlightTokenType.KEYWORD_NEPALI
            ENGLISH_KEYWORDS.contains(text) -> HighlightTokenType.KEYWORD_ENGLISH
            BUILTIN_FUNCTIONS.contains(text) -> HighlightTokenType.BUILTIN
            STDLIB_MODULES.contains(text) -> HighlightTokenType.STDLIB_MODULE
            else -> HighlightTokenType.IDENTIFIER
        }

        return HighlightToken(tokenType, text, start, cursor)
    }

    private fun scanOperatorOrPunctuation(start: Int): HighlightToken {
        val c = source[cursor]

        // Try double character operators
        if (cursor + 1 < length) {
            val twoChar = source.substring(cursor, cursor + 2)
            if (OPERATORS.contains(twoChar)) {
                cursor += 2
                return HighlightToken(HighlightTokenType.OPERATOR, twoChar, start, cursor)
            }
        }

        cursor++
        val oneChar = c.toString()
        val type = when {
            OPERATORS.contains(oneChar) -> HighlightTokenType.OPERATOR
            c in "():,;[]{}" -> HighlightTokenType.PUNCTUATION
            else -> HighlightTokenType.OPERATOR
        }
        return HighlightToken(type, oneChar, start, cursor)
    }

    private fun isIdentifierStart(c: Char): Boolean {
        return c.isLetter() || c == '_' || c.category in listOf(
            CharCategory.UPPERCASE_LETTER,
            CharCategory.LOWERCASE_LETTER,
            CharCategory.TITLECASE_LETTER,
            CharCategory.MODIFIER_LETTER,
            CharCategory.OTHER_LETTER
        )
    }

    private fun isIdentifierPart(c: Char): Boolean {
        return isIdentifierStart(c) || c.isDigit()
    }

    private fun isHexDigit(c: Char): Boolean {
        return c.isDigit() || c in 'a'..'f' || c in 'A'..'F'
    }

    private fun createDiagnostic(start: Int, end: Int, msg: String): SyntaxDiagnostic {
        var line = 1
        var col = 1
        for (i in 0 until start.coerceAtMost(source.length)) {
            if (source[i] == '\n') {
                line++
                col = 1
            } else {
                col++
            }
        }
        return SyntaxDiagnostic(line, col, start, end, msg, isError = true)
    }
}
