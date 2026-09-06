package com.nepalicode.dev.nepalilang.core

enum class TokenType {
    // Literals
    NUMBER,
    STRING,
    BOOLEAN,
    NULL,
    IDENTIFIER,

    // English keywords
    IF, ELSE, ELIF, FOR, WHILE, DEF, RETURN, CLASS, IMPORT, FROM,
    TRY, EXCEPT, FINALLY, RAISE, WITH, IN, IS, AND, OR, NOT,
    ASSERT, BREAK, CONTINUE, PASS,

    // Nepali keyword aliases
    YEDI, NATRA, NATABHAYE, JABASAMMA, KO_LAGI, KAAM, FIRTA,
    KAKSHYA, LYAU, BATA, KOSHISH, SAMAU, ANTYA, UTHAU, BHITRA,
    MA, RA, WA, HOINA, SACHO, JHUT, KHALI,

    // Operators & Punctuation
    PLUS, MINUS, STAR, SLASH, PERCENT, POWER,
    ASSIGN, EQUAL, NOT_EQUAL, LESS, LESS_EQUAL, GREATER, GREATER_EQUAL,
    LPAREN, RPAREN, LBRACKET, RBRACKET, LBRACE, RBRACE,
    COMMA, DOT, COLON, SEMICOLON, ARROW,

    // Layout
    NEWLINE,
    INDENT,
    DEDENT,
    EOF
}

data class Token(
    val type: TokenType,
    val lexeme: String,
    val literal: Any? = null,
    val line: Int = 1,
    val column: Int = 1
)
