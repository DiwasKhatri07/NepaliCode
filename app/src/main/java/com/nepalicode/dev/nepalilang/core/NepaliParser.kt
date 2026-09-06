package com.nepalicode.dev.nepalilang.core

class NepaliParseException(
    val line: Int,
    val column: Int,
    override val message: String,
    val suggestion: String = "Check syntax and indentation."
) : Exception("$message at line $line, column $column")

class NepaliParser(private val tokens: List<Token>) {
    private var current = 0

    fun parse(): Program {
        val statements = mutableListOf<Stmt>()
        while (!isAtEnd()) {
            skipNewlines()
            if (isAtEnd()) break
            val stmt = parseStatement()
            if (stmt != null) {
                statements.add(stmt)
            }
            skipNewlines()
        }
        return Program(statements)
    }

    private fun parseStatement(): Stmt? {
        skipNewlines()
        if (isAtEnd()) return null

        val token = peek()
        return when (token.type) {
            TokenType.IF, TokenType.YEDI -> parseIfStatement()
            TokenType.WHILE, TokenType.JABASAMMA -> parseWhileStatement()
            TokenType.FOR, TokenType.KO_LAGI -> parseForStatement()
            TokenType.DEF, TokenType.KAAM -> parseFunctionDef()
            TokenType.CLASS, TokenType.KAKSHYA -> parseClassDef()
            TokenType.BREAK -> {
                val t = advance()
                skipStatementEnd()
                BreakStmt(t)
            }
            TokenType.CONTINUE -> {
                val t = advance()
                skipStatementEnd()
                ContinueStmt(t)
            }
            TokenType.RETURN, TokenType.FIRTA -> parseReturnStatement()
            TokenType.IMPORT, TokenType.LYAU -> parseImportStatement()
            TokenType.FROM, TokenType.BATA -> parseFromImportStatement()
            TokenType.TRY, TokenType.KOSHISH -> parseTryExceptStatement()
            TokenType.ASSERT -> parseAssertStatement()
            TokenType.PASS -> {
                advance()
                skipStatementEnd()
                null
            }
            else -> parseExpressionOrAssignStatement()
        }
    }

    private fun parseIfStatement(): Stmt {
        val keyword = advance() // if / yedi
        val condition = parseExpression()
        consume(TokenType.COLON, "Expected ':' after if/yedi condition")
        val thenBranch = parseBlockOrSingleStmt()

        val elifBranches = mutableListOf<ElifBranch>()
        while (check(TokenType.ELIF) || check(TokenType.NATABHAYE)) {
            advance()
            val elifCond = parseExpression()
            consume(TokenType.COLON, "Expected ':' after elif/natabhaye condition")
            val elifBody = parseBlockOrSingleStmt()
            elifBranches.add(ElifBranch(elifCond, elifBody))
        }

        var elseBranch: List<Stmt>? = null
        if (check(TokenType.ELSE) || check(TokenType.NATRA)) {
            advance()
            consume(TokenType.COLON, "Expected ':' after else/natra")
            elseBranch = parseBlockOrSingleStmt()
        }

        return IfStmt(condition, thenBranch, elifBranches, elseBranch)
    }

    private fun parseWhileStatement(): Stmt {
        val keyword = advance()
        val condition = parseExpression()
        consume(TokenType.COLON, "Expected ':' after while/jabasamma condition")
        val body = parseBlockOrSingleStmt()
        return WhileStmt(condition, body)
    }

    private fun parseForStatement(): Stmt {
        val keyword = advance()
        val variableToken = consume(TokenType.IDENTIFIER, "Expected loop variable name after for/ko_lagi")
        val inToken = if (check(TokenType.IN) || check(TokenType.MA)) {
            advance()
        } else {
            throw error(peek(), "Expected 'in' or 'ma' in for loop", "Use syntax: for i in 10: or ko_lagi i ma list:")
        }
        val iterable = parseExpression()
        consume(TokenType.COLON, "Expected ':' after for/ko_lagi header")
        val body = parseBlockOrSingleStmt()
        return ForStmt(variableToken.lexeme, iterable, body)
    }

    private fun parseFunctionDef(): Stmt {
        advance() // def / kaam
        val nameToken = consume(TokenType.IDENTIFIER, "Expected function name")
        consume(TokenType.LPAREN, "Expected '(' after function name")
        val params = mutableListOf<String>()
        if (!check(TokenType.RPAREN)) {
            do {
                val paramToken = consume(TokenType.IDENTIFIER, "Expected parameter name")
                params.add(paramToken.lexeme)
            } while (match(TokenType.COMMA))
        }
        consume(TokenType.RPAREN, "Expected ')' after parameters")
        consume(TokenType.COLON, "Expected ':' after function signature")
        val body = parseBlockOrSingleStmt()
        return FunctionDefStmt(nameToken.lexeme, params, body)
    }

    private fun parseClassDef(): Stmt {
        val classToken = advance() // class / kakshya
        val nameToken = consume(TokenType.IDENTIFIER, "Expected class name after class/kakshya")
        consume(TokenType.COLON, "Expected ':' after class name")
        val methods = mutableListOf<FunctionDefStmt>()
        skipNewlines()
        if (match(TokenType.INDENT)) {
            while (!check(TokenType.DEDENT) && !isAtEnd()) {
                skipNewlines()
                if (check(TokenType.DEDENT) || isAtEnd()) break
                if (check(TokenType.DEF) || check(TokenType.KAAM)) {
                    val fn = parseFunctionDef()
                    if (fn is FunctionDefStmt) methods.add(fn)
                } else if (check(TokenType.PASS)) {
                    advance()
                    skipStatementEnd()
                } else {
                    parseStatement()
                }
                skipNewlines()
            }
            consume(TokenType.DEDENT, "Expected dedent at end of class body")
        } else {
            if (check(TokenType.DEF) || check(TokenType.KAAM)) {
                val fn = parseFunctionDef()
                if (fn is FunctionDefStmt) methods.add(fn)
            } else if (check(TokenType.PASS)) {
                advance()
                skipStatementEnd()
            }
        }
        return ClassDefStmt(nameToken.lexeme, methods, classToken)
    }

    private fun parseReturnStatement(): Stmt {
        val token = advance() // return / firta
        val value = if (check(TokenType.NEWLINE) || check(TokenType.EOF) || check(TokenType.DEDENT)) {
            null
        } else {
            parseExpression()
        }
        skipStatementEnd()
        return ReturnStmt(value, token)
    }

    private fun parseImportStatement(): Stmt {
        advance() // import / lyau
        val moduleToken = consume(TokenType.IDENTIFIER, "Expected module name to import")
        var alias: String? = null
        if (check(TokenType.IDENTIFIER) && peek().lexeme == "as") {
            advance()
            val aliasToken = consume(TokenType.IDENTIFIER, "Expected alias name after 'as'")
            alias = aliasToken.lexeme
        }
        skipStatementEnd()
        return ImportStmt(module = moduleToken.lexeme, alias = alias)
    }

    private fun parseFromImportStatement(): Stmt {
        advance() // from / bata
        val moduleToken = consume(TokenType.IDENTIFIER, "Expected module name after 'from'/'bata'")
        if (check(TokenType.IMPORT) || check(TokenType.LYAU)) {
            advance()
        } else {
            throw error(peek(), "Expected 'import' or 'lyau' after module name", "Use: from anurodh import get or bata anurodh lyau get")
        }
        val symbols = mutableListOf<String>()
        do {
            val sym = consume(TokenType.IDENTIFIER, "Expected imported symbol name")
            symbols.add(sym.lexeme)
        } while (match(TokenType.COMMA))
        skipStatementEnd()
        return ImportStmt(module = moduleToken.lexeme, symbols = symbols)
    }

    private fun parseTryExceptStatement(): Stmt {
        advance() // try / koshish
        consume(TokenType.COLON, "Expected ':' after try/koshish")
        val tryBlock = parseBlockOrSingleStmt()

        if (!check(TokenType.EXCEPT) && !check(TokenType.SAMAU)) {
            throw error(peek(), "Expected 'except' or 'samau' block", "Add except: or samau: block")
        }
        advance() // except / samau
        var exceptVar: String? = null
        if (check(TokenType.IDENTIFIER)) {
            exceptVar = advance().lexeme
        }
        consume(TokenType.COLON, "Expected ':' after except/samau")
        val exceptBlock = parseBlockOrSingleStmt()

        var finallyBlock: List<Stmt>? = null
        if (check(TokenType.FINALLY) || check(TokenType.ANTYA)) {
            advance()
            consume(TokenType.COLON, "Expected ':' after finally/antya")
            finallyBlock = parseBlockOrSingleStmt()
        }

        return TryExceptStmt(tryBlock, exceptBlock, exceptVar, finallyBlock)
    }

    private fun parseAssertStatement(): Stmt {
        val token = advance()
        val condition = parseExpression()
        var message: Expr? = null
        if (match(TokenType.COMMA)) {
            message = parseExpression()
        }
        skipStatementEnd()
        return AssertStmt(condition, message, token)
    }

    private fun parseExpressionOrAssignStatement(): Stmt {
        val expr = parseExpression()

        if (match(TokenType.ASSIGN)) {
            val value = parseExpression()
            skipStatementEnd()
            return AssignStmt(expr, value, previous())
        }

        skipStatementEnd()
        return ExprStmt(expr)
    }

    private fun parseBlockOrSingleStmt(): List<Stmt> {
        skipNewlines()
        val statements = mutableListOf<Stmt>()

        if (match(TokenType.INDENT)) {
            while (!check(TokenType.DEDENT) && !isAtEnd()) {
                skipNewlines()
                if (check(TokenType.DEDENT) || isAtEnd()) break
                val s = parseStatement()
                if (s != null) statements.add(s)
                skipNewlines()
            }
            consume(TokenType.DEDENT, "Expected dedent at end of block")
        } else {
            // Single-line statement following colon
            val s = parseStatement()
            if (s != null) statements.add(s)
        }
        return statements
    }

    // Expressions
    private fun parseExpression(): Expr = parseOr()

    private fun parseOr(): Expr {
        var expr = parseAnd()
        while (check(TokenType.OR) || check(TokenType.WA)) {
            val opToken = advance()
            val right = parseAnd()
            expr = BinaryExpr(expr, TokenType.OR, right, opToken)
        }
        return expr
    }

    private fun parseAnd(): Expr {
        var expr = parseEquality()
        while (check(TokenType.AND) || check(TokenType.RA)) {
            val opToken = advance()
            val right = parseEquality()
            expr = BinaryExpr(expr, TokenType.AND, right, opToken)
        }
        return expr
    }

    private fun parseEquality(): Expr {
        var expr = parseComparison()
        while (match(TokenType.EQUAL, TokenType.NOT_EQUAL, TokenType.IS)) {
            val op = previous()
            val right = parseComparison()
            expr = BinaryExpr(expr, op.type, right, op)
        }
        return expr
    }

    private fun parseComparison(): Expr {
        var expr = parseTerm()
        while (match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL, TokenType.IN, TokenType.MA)) {
            val op = previous()
            val right = parseTerm()
            expr = BinaryExpr(expr, op.type, right, op)
        }
        return expr
    }

    private fun parseTerm(): Expr {
        var expr = parseFactor()
        while (match(TokenType.PLUS, TokenType.MINUS)) {
            val op = previous()
            val right = parseFactor()
            expr = BinaryExpr(expr, op.type, right, op)
        }
        return expr
    }

    private fun parseFactor(): Expr {
        var expr = parsePower()
        while (match(TokenType.STAR, TokenType.SLASH, TokenType.PERCENT)) {
            val op = previous()
            val right = parsePower()
            expr = BinaryExpr(expr, op.type, right, op)
        }
        return expr
    }

    private fun parsePower(): Expr {
        var expr = parseUnary()
        while (match(TokenType.POWER)) {
            val op = previous()
            val right = parseUnary()
            expr = BinaryExpr(expr, op.type, right, op)
        }
        return expr
    }

    private fun parseUnary(): Expr {
        if (match(TokenType.NOT, TokenType.HOINA, TokenType.MINUS, TokenType.PLUS)) {
            val op = previous()
            val right = parseUnary()
            return UnaryExpr(op.type, right, op)
        }
        return parseCallAndAccess()
    }

    private fun parseCallAndAccess(): Expr {
        var expr = parsePrimary()

        while (true) {
            if (match(TokenType.LPAREN)) {
                // Function call
                val args = mutableListOf<Expr>()
                if (!check(TokenType.RPAREN)) {
                    do {
                        args.add(parseExpression())
                    } while (match(TokenType.COMMA))
                }
                val rparen = consume(TokenType.RPAREN, "Expected ')' after arguments")
                expr = CallExpr(expr, args, rparen)
            } else if (match(TokenType.DOT)) {
                // Member access or method
                val nameToken = consume(TokenType.IDENTIFIER, "Expected member or method name after '.'")
                expr = MemberAccessExpr(expr, nameToken.lexeme, nameToken)
            } else if (match(TokenType.LBRACKET)) {
                // Index access [x]
                val indexExpr = parseExpression()
                val rbracket = consume(TokenType.RBRACKET, "Expected ']' after index")
                expr = IndexExpr(expr, indexExpr, rbracket)
            } else {
                break
            }
        }
        return expr
    }

    private fun parsePrimary(): Expr {
        val token = peek()
        return when (token.type) {
            TokenType.NUMBER -> {
                advance()
                LiteralExpr(token.literal, token)
            }
            TokenType.STRING -> {
                advance()
                LiteralExpr(token.literal, token)
            }
            TokenType.BOOLEAN, TokenType.SACHO, TokenType.JHUT -> {
                advance()
                val v = (token.type == TokenType.SACHO || token.literal == true)
                LiteralExpr(v, token)
            }
            TokenType.NULL, TokenType.KHALI -> {
                advance()
                LiteralExpr(null, token)
            }
            TokenType.IDENTIFIER -> {
                advance()
                IdentifierExpr(token.lexeme, token)
            }
            TokenType.LPAREN -> {
                advance()
                val expr = parseExpression()
                consume(TokenType.RPAREN, "Expected ')' after expression")
                expr
            }
            TokenType.LBRACKET -> {
                // List literal: [1, 2, 3]
                val lbracket = advance()
                val elements = mutableListOf<Expr>()
                if (!check(TokenType.RBRACKET)) {
                    do {
                        skipNewlines()
                        if (check(TokenType.RBRACKET)) break
                        elements.add(parseExpression())
                        skipNewlines()
                    } while (match(TokenType.COMMA))
                }
                skipNewlines()
                consume(TokenType.RBRACKET, "Expected ']' after list elements")
                ListLiteralExpr(elements, lbracket)
            }
            TokenType.LBRACE -> {
                // Map literal: {"a": 1, "b": 2}
                val lbrace = advance()
                val entries = mutableListOf<Pair<Expr, Expr>>()
                if (!check(TokenType.RBRACE)) {
                    do {
                        skipNewlines()
                        if (check(TokenType.RBRACE)) break
                        val key = parseExpression()
                        consume(TokenType.COLON, "Expected ':' after map key")
                        val value = parseExpression()
                        entries.add(key to value)
                        skipNewlines()
                    } while (match(TokenType.COMMA))
                }
                skipNewlines()
                consume(TokenType.RBRACE, "Expected '}' after map entries")
                MapLiteralExpr(entries, lbrace)
            }
            else -> throw error(token, "Unexpected token '${token.lexeme}'", "Ensure expression is valid NepaliLang syntax.")
        }
    }

    private fun skipNewlines() {
        while (check(TokenType.NEWLINE)) {
            advance()
        }
    }

    private fun skipStatementEnd() {
        if (check(TokenType.SEMICOLON)) advance()
        while (check(TokenType.NEWLINE)) advance()
    }

    private fun match(vararg types: TokenType): Boolean {
        for (type in types) {
            if (check(type)) {
                advance()
                return true
            }
        }
        return false
    }

    private fun consume(type: TokenType, message: String): Token {
        if (check(type)) return advance()
        throw error(peek(), message, "Check preceding syntax or missing punctuation.")
    }

    private fun check(type: TokenType): Boolean {
        if (isAtEnd()) return type == TokenType.EOF
        return peek().type == type
    }

    private fun advance(): Token {
        if (!isAtEnd()) current++
        return previous()
    }

    private fun isAtEnd(): Boolean = peek().type == TokenType.EOF

    private fun peek(): Token = tokens[current]

    private fun previous(): Token = tokens[current - 1]

    private fun error(token: Token, message: String, suggestion: String): NepaliParseException {
        return NepaliParseException(token.line, token.column, message, suggestion)
    }
}
