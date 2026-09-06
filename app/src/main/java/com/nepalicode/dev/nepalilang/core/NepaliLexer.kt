package com.nepalicode.dev.nepalilang.core

class NepaliLexer(private val source: String) {
    private val tokens = mutableListOf<Token>()
    private var start = 0
    private var current = 0
    private var line = 1
    private var column = 1
    private var lineStart = 0

    private val indentStack = mutableListOf(0)

    private val keywords = mapOf(
        // English keywords
        "if" to TokenType.IF,
        "else" to TokenType.ELSE,
        "elif" to TokenType.ELIF,
        "for" to TokenType.FOR,
        "while" to TokenType.WHILE,
        "def" to TokenType.DEF,
        "return" to TokenType.RETURN,
        "class" to TokenType.CLASS,
        "import" to TokenType.IMPORT,
        "from" to TokenType.FROM,
        "try" to TokenType.TRY,
        "except" to TokenType.EXCEPT,
        "finally" to TokenType.FINALLY,
        "raise" to TokenType.RAISE,
        "with" to TokenType.WITH,
        "in" to TokenType.IN,
        "is" to TokenType.IS,
        "and" to TokenType.AND,
        "or" to TokenType.OR,
        "not" to TokenType.NOT,
        "True" to TokenType.BOOLEAN,
        "False" to TokenType.BOOLEAN,
        "None" to TokenType.NULL,
        "assert" to TokenType.ASSERT,
        "break" to TokenType.BREAK,
        "continue" to TokenType.CONTINUE,
        "pass" to TokenType.PASS,

        // Nepali keyword aliases
        "yedi" to TokenType.YEDI,
        "natra" to TokenType.NATRA,
        "natabhaye" to TokenType.NATABHAYE,
        "athawa" to TokenType.NATABHAYE,
        "jabasamma" to TokenType.JABASAMMA,
        "ko_lagi" to TokenType.KO_LAGI,
        "kaam" to TokenType.KAAM,
        "firta" to TokenType.FIRTA,
        "kakshya" to TokenType.KAKSHYA,
        "lyau" to TokenType.LYAU,
        "bata" to TokenType.BATA,
        "koshish" to TokenType.KOSHISH,
        "samau" to TokenType.SAMAU,
        "antya" to TokenType.ANTYA,
        "uthau" to TokenType.UTHAU,
        "bhitra" to TokenType.BHITRA,
        "ma" to TokenType.MA,
        "ra" to TokenType.RA,
        "wa" to TokenType.WA,
        "hoina" to TokenType.HOINA,
        "sacho" to TokenType.SACHO,
        "jhut" to TokenType.JHUT,
        "khali" to TokenType.KHALI,
        "rok" to TokenType.BREAK,
        "agadi" to TokenType.CONTINUE,
        "jaari" to TokenType.CONTINUE,
        "chhoda" to TokenType.PASS
    )

    fun tokenize(): List<Token> {
        var atLineStart = true

        while (!isAtEnd()) {
            if (atLineStart) {
                // Count leading indentation (spaces / tabs)
                var indentLevel = 0
                val indentStartCol = column
                while (!isAtEnd()) {
                    val c = peek()
                    if (c == ' ') {
                        indentLevel++
                        advance()
                    } else if (c == '\t') {
                        indentLevel += 4
                        advance()
                    } else {
                        break
                    }
                }

                // If line is blank or just a comment, ignore indent
                if (peek() == '\n' || peek() == '\r' || peek() == '#' || isAtEnd()) {
                    if (peek() == '#') {
                        skipComment()
                    }
                    if (match('\r')) { /* ignore */ }
                    if (match('\n')) {
                        line++
                        lineStart = current
                        column = 1
                    }
                    continue
                }

                val currentIndent = indentStack.last()
                if (indentLevel > currentIndent) {
                    indentStack.add(indentLevel)
                    tokens.add(Token(TokenType.INDENT, "", null, line, indentStartCol))
                } else if (indentLevel < currentIndent) {
                    while (indentStack.isNotEmpty() && indentLevel < indentStack.last()) {
                        indentStack.removeAt(indentStack.size - 1)
                        tokens.add(Token(TokenType.DEDENT, "", null, line, indentStartCol))
                    }
                }
                atLineStart = false
            }

            start = current
            scanToken()

            if (tokens.isNotEmpty() && tokens.last().type == TokenType.NEWLINE) {
                atLineStart = true
            }
        }

        // Close all pending indentations
        while (indentStack.size > 1) {
            indentStack.removeAt(indentStack.size - 1)
            tokens.add(Token(TokenType.DEDENT, "", null, line, column))
        }

        tokens.add(Token(TokenType.EOF, "", null, line, column))
        return tokens
    }

    private fun scanToken() {
        val c = advance()
        when (c) {
            '(' -> addToken(TokenType.LPAREN)
            ')' -> addToken(TokenType.RPAREN)
            '[' -> addToken(TokenType.LBRACKET)
            ']' -> addToken(TokenType.RBRACKET)
            '{' -> addToken(TokenType.LBRACE)
            '}' -> addToken(TokenType.RBRACE)
            ',' -> addToken(TokenType.COMMA)
            '.' -> addToken(TokenType.DOT)
            ':' -> addToken(TokenType.COLON)
            ';' -> addToken(TokenType.SEMICOLON)
            '+' -> addToken(TokenType.PLUS)
            '-' -> {
                if (match('>')) addToken(TokenType.ARROW) else addToken(TokenType.MINUS)
            }
            '*' -> {
                if (match('*')) addToken(TokenType.POWER) else addToken(TokenType.STAR)
            }
            '/' -> addToken(TokenType.SLASH)
            '%' -> addToken(TokenType.PERCENT)
            '=' -> {
                if (match('=')) addToken(TokenType.EQUAL) else addToken(TokenType.ASSIGN)
            }
            '!' -> {
                if (match('=')) addToken(TokenType.NOT_EQUAL)
            }
            '<' -> {
                if (match('=')) addToken(TokenType.LESS_EQUAL) else addToken(TokenType.LESS)
            }
            '>' -> {
                if (match('=')) addToken(TokenType.GREATER_EQUAL) else addToken(TokenType.GREATER)
            }
            '#' -> skipComment()
            ' ', '\r', '\t' -> {
                // Ignore inline whitespace
            }
            '\n' -> {
                addToken(TokenType.NEWLINE)
                line++
                lineStart = current
                column = 1
            }
            '"', '\'' -> string(c)
            'f', 'F' -> {
                if (peek() == '"' || peek() == '\'') {
                    val quote = advance()
                    fString(quote)
                } else {
                    identifierOrKeyword('f')
                }
            }
            else -> {
                if (isDigit(c)) {
                    number()
                } else if (isAlphaOrUnicode(c)) {
                    identifierOrKeyword(c)
                }
            }
        }
    }

    private fun skipComment() {
        while (peek() != '\n' && !isAtEnd()) {
            advance()
        }
    }

    private fun string(quote: Char) {
        val sb = StringBuilder()
        while (peek() != quote && !isAtEnd()) {
            if (peek() == '\n') {
                line++
                lineStart = current
                column = 1
            }
            if (peek() == '\\') {
                advance()
                if (!isAtEnd()) {
                    when (val esc = advance()) {
                        'n' -> sb.append('\n')
                        't' -> sb.append('\t')
                        'r' -> sb.append('\r')
                        '\\' -> sb.append('\\')
                        '"' -> sb.append('"')
                        '\'' -> sb.append('\'')
                        else -> sb.append(esc)
                    }
                }
            } else {
                sb.append(advance())
            }
        }
        if (isAtEnd()) {
            // Unclosed string
            addToken(TokenType.STRING, sb.toString())
            return
        }
        advance() // Closing quote
        addToken(TokenType.STRING, sb.toString())
    }

    private fun fString(quote: Char) {
        // Simple f-string support by preserving text
        val sb = StringBuilder()
        while (peek() != quote && !isAtEnd()) {
            sb.append(advance())
        }
        if (!isAtEnd()) advance()
        // We will interpolate f-strings at runtime
        addToken(TokenType.STRING, sb.toString())
    }

    private fun number() {
        while (isDigit(peek())) advance()
        var isDecimal = false
        if (peek() == '.' && isDigit(peekNext())) {
            isDecimal = true
            advance() // Consume '.'
            while (isDigit(peek())) advance()
        }
        val text = source.substring(start, current)
        val num: Any = if (isDecimal) text.toDouble() else text.toLong()
        addToken(TokenType.NUMBER, num)
    }

    private fun identifierOrKeyword(first: Char) {
        while (isAlphaNumericOrUnicode(peek())) {
            advance()
        }
        val text = source.substring(start, current)
        val type = keywords[text] ?: TokenType.IDENTIFIER

        val literal = when (type) {
            TokenType.BOOLEAN -> (text == "True" || text == "sacho")
            TokenType.SACHO -> true
            TokenType.JHUT -> false
            TokenType.KHALI, TokenType.NULL -> null
            else -> text
        }
        addToken(type, literal)
    }

    private fun isDigit(c: Char) = c in '0'..'9'

    private fun isAlphaOrUnicode(c: Char): Boolean {
        return c in 'a'..'z' || c in 'A'..'Z' || c == '_' || Character.isLetter(c)
    }

    private fun isAlphaNumericOrUnicode(c: Char): Boolean {
        return isAlphaOrUnicode(c) || isDigit(c)
    }

    private fun advance(): Char {
        current++
        column++
        return source[current - 1]
    }

    private fun match(expected: Char): Boolean {
        if (isAtEnd() || source[current] != expected) return false
        current++
        column++
        return true
    }

    private fun peek(): Char = if (isAtEnd()) '\u0000' else source[current]

    private fun peekNext(): Char = if (current + 1 >= source.length) '\u0000' else source[current + 1]

    private fun isAtEnd(): Boolean = current >= source.length

    private fun addToken(type: TokenType, literal: Any? = null) {
        val text = source.substring(start, current)
        val tokenCol = column - (current - start)
        tokens.add(Token(type, text, literal, line, maxOf(1, tokenCol)))
    }
}
