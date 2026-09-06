"""
NepaliLang Lexer
Tokenizes .np source code into tokens for parsing.
"""

import re
from enum import Enum
from dataclasses import dataclass
from typing import Optional, List


class TokenType(Enum):
    # Literals
    NUMBER = "NUMBER"
    STRING = "STRING"
    BOOLEAN = "BOOLEAN"
    NULL = "NULL"
    
    # Identifiers and Keywords
    IDENTIFIER = "IDENTIFIER"
    
    # Keywords - English
    IF = "IF"
    ELSE = "ELSE"
    ELIF = "ELIF"
    FOR = "FOR"
    WHILE = "WHILE"
    DEF = "DEF"
    RETURN = "RETURN"
    CLASS = "CLASS"
    IMPORT = "IMPORT"
    FROM = "FROM"
    TRY = "TRY"
    EXCEPT = "EXCEPT"
    FINALLY = "FINALLY"
    RAISE = "RAISE"
    WITH = "WITH"
    IN = "IN"
    IS = "IS"
    AND = "AND"
    OR = "OR"
    NOT = "NOT"
    TRUE = "TRUE"
    FALSE = "FALSE"
    NONE = "NONE"
    
    # Keywords - Nepali
    YEDI = "YEDI"           # if
    NATRA = "NATRA"         # else
    NATABHAYE = "NATABHAYE" # elif
    JABASAMMA = "JABASAMMA" # while
    KO_LAGI = "KO_LAGI"     # for
    KAAM = "KAAM"           # def
    FIRTA = "FIRTA"         # return
    KAKSHYA = "KAKSHYA"     # class
    LYAU = "LYAU"           # import
    BATA = "BATA"           # from
    KOSHISH = "KOSHISH"     # try
    SAMAU = "SAMAU"         # except
    ANTYA = "ANTYA"         # finally
    UTHAU = "UTHAU"         # raise
    BHITRA = "BHITRA"       # with
    MA = "MA"               # in
    RA = "RA"               # and
    WA = "WA"               # or
    HOINA = "HOINA"         # not
    SACHO = "SACHO"         # True
    JHUT = "JHUT"           # False
    KHALI = "KHALI"         # None
    
    # Operators
    PLUS = "PLUS"
    MINUS = "MINUS"
    STAR = "STAR"
    SLASH = "SLASH"
    PERCENT = "PERCENT"
    POWER = "POWER"
    
    # Comparison
    EQUAL = "EQUAL"
    NOT_EQUAL = "NOT_EQUAL"
    LESS = "LESS"
    LESS_EQUAL = "LESS_EQUAL"
    GREATER = "GREATER"
    GREATER_EQUAL = "GREATER_EQUAL"
    
    # Assignment
    ASSIGN = "ASSIGN"
    PLUS_ASSIGN = "PLUS_ASSIGN"
    MINUS_ASSIGN = "MINUS_ASSIGN"
    
    # Delimiters
    LPAREN = "LPAREN"
    RPAREN = "RPAREN"
    LBRACE = "LBRACE"
    RBRACE = "RBRACE"
    LBRACKET = "LBRACKET"
    RBRACKET = "RBRACKET"
    COLON = "COLON"
    COMMA = "COMMA"
    DOT = "DOT"
    
    # Special
    NEWLINE = "NEWLINE"
    INDENT = "INDENT"
    DEDENT = "DEDENT"
    EOF = "EOF"
    
    # Special function
    YO = "YO"  # Convenience constructor


@dataclass
class Token:
    type: TokenType
    value: any
    line: int
    column: int


class Lexer:
    def __init__(self, source: str):
        self.source = source
        self.pos = 0
        self.line = 1
        self.column = 1
        self.tokens: List[Token] = []
        
        # Keyword mappings
        self.keywords = {
            # English
            'if': TokenType.IF,
            'else': TokenType.ELSE,
            'elif': TokenType.ELIF,
            'for': TokenType.FOR,
            'while': TokenType.WHILE,
            'def': TokenType.DEF,
            'return': TokenType.RETURN,
            'class': TokenType.CLASS,
            'import': TokenType.IMPORT,
            'from': TokenType.FROM,
            'try': TokenType.TRY,
            'except': TokenType.EXCEPT,
            'finally': TokenType.FINALLY,
            'raise': TokenType.RAISE,
            'with': TokenType.WITH,
            'in': TokenType.IN,
            'is': TokenType.IS,
            'and': TokenType.AND,
            'or': TokenType.OR,
            'not': TokenType.NOT,
            'true': TokenType.TRUE,
            'false': TokenType.FALSE,
            'none': TokenType.NONE,
            'True': TokenType.TRUE,
            'False': TokenType.FALSE,
            'None': TokenType.NONE,
            # Nepali
            'yedi': TokenType.YEDI,
            'natra': TokenType.NATRA,
            'natabhaye': TokenType.NATABHAYE,
            'jabasamma': TokenType.JABASAMMA,
            'ko_lagi': TokenType.KO_LAGI,
            'kaam': TokenType.KAAM,
            'firta': TokenType.FIRTA,
            'kakshya': TokenType.KAKSHYA,
            'lyau': TokenType.LYAU,
            'bata': TokenType.BATA,
            'koshish': TokenType.KOSHISH,
            'samau': TokenType.SAMAU,
            'antya': TokenType.ANTYA,
            'uthau': TokenType.UTHAU,
            'bhitra': TokenType.BHITRA,
            'ma': TokenType.MA,
            'ra': TokenType.RA,
            'wa': TokenType.WA,
            'hoina': TokenType.HOINA,
            'sacho': TokenType.SACHO,
            'jhut': TokenType.JHUT,
            'khali': TokenType.KHALI,
            'Sacho': TokenType.SACHO,
            'Jhut': TokenType.JHUT,
            'Khali': TokenType.KHALI,
            'yo': TokenType.YO,
        }
    
    def current_char(self) -> Optional[str]:
        if self.pos < len(self.source):
            return self.source[self.pos]
        return None
    
    def advance(self) -> Optional[str]:
        char = self.current_char()
        if char:
            self.pos += 1
            if char == '\n':
                self.line += 1
                self.column = 1
            else:
                self.column += 1
        return char
    
    def peek(self, offset: int = 1) -> Optional[str]:
        peek_pos = self.pos + offset
        if peek_pos < len(self.source):
            return self.source[peek_pos]
        return None
    
    def skip_whitespace(self):
        while self.current_char() and self.current_char() in ' \t\r':
            self.advance()
    
    def skip_comment(self):
        if self.current_char() == '#':
            while self.current_char() and self.current_char() != '\n':
                self.advance()
    
    def read_number(self) -> str:
        result = ''
        while self.current_char() and (self.current_char().isdigit() or self.current_char() == '.'):
            result += self.advance()
        return result
    
    def read_string(self, quote: str) -> str:
        result = ''
        self.advance()  # Skip opening quote
        while self.current_char() and self.current_char() != quote:
            if self.current_char() == '\\':
                self.advance()
                result += self.advance()
            else:
                result += self.advance()
        self.advance()  # Skip closing quote
        return result
    
    def read_identifier(self) -> str:
        result = ''
        # Allow Unicode identifiers
        while self.current_char() and (self.current_char().isalnum() or 
                                        self.current_char() == '_' or
                                        ord(self.current_char()) > 127):
            result += self.advance()
        return result
    
    def tokenize(self) -> List[Token]:
        while self.pos < len(self.source):
            self.skip_whitespace()
            self.skip_comment()
            
            if self.pos >= len(self.source):
                break
            
            char = self.current_char()
            line = self.line
            column = self.column
            
            # Newline
            if char == '\n':
                self.tokens.append(Token(TokenType.NEWLINE, '\\n', line, column))
                self.advance()
                continue
            
            # Numbers
            if char.isdigit():
                number = self.read_number()
                if '.' in number:
                    self.tokens.append(Token(TokenType.NUMBER, float(number), line, column))
                else:
                    self.tokens.append(Token(TokenType.NUMBER, int(number), line, column))
                continue
            
            # Strings
            if char in '"\'':
                string = self.read_string(char)
                self.tokens.append(Token(TokenType.STRING, string, line, column))
                continue
            
            # Identifiers and keywords
            if char.isalpha() or char == '_' or ord(char) > 127:
                identifier = self.read_identifier()
                token_type = self.keywords.get(identifier, TokenType.IDENTIFIER)
                
                # Handle boolean and null literals
                if token_type == TokenType.TRUE:
                    self.tokens.append(Token(TokenType.BOOLEAN, True, line, column))
                elif token_type == TokenType.FALSE:
                    self.tokens.append(Token(TokenType.BOOLEAN, False, line, column))
                elif token_type == TokenType.SACHO:
                    self.tokens.append(Token(TokenType.BOOLEAN, True, line, column))
                elif token_type == TokenType.JHUT:
                    self.tokens.append(Token(TokenType.BOOLEAN, False, line, column))
                elif token_type == TokenType.NONE:
                    self.tokens.append(Token(TokenType.NULL, None, line, column))
                elif token_type == TokenType.KHALI:
                    self.tokens.append(Token(TokenType.NULL, None, line, column))
                else:
                    self.tokens.append(Token(token_type, identifier, line, column))
                continue
            
            # Operators and delimiters
            if char == '+':
                if self.peek() == '=':
                    self.advance()
                    self.tokens.append(Token(TokenType.PLUS_ASSIGN, '+=', line, column))
                else:
                    self.tokens.append(Token(TokenType.PLUS, '+', line, column))
                self.advance()
                continue
            
            if char == '-':
                if self.peek() == '=':
                    self.advance()
                    self.tokens.append(Token(TokenType.MINUS_ASSIGN, '-=', line, column))
                else:
                    self.tokens.append(Token(TokenType.MINUS, '-', line, column))
                self.advance()
                continue
            
            if char == '*':
                if self.peek() == '*':
                    self.advance()
                    self.tokens.append(Token(TokenType.POWER, '**', line, column))
                else:
                    self.tokens.append(Token(TokenType.STAR, '*', line, column))
                self.advance()
                continue
            
            if char == '/':
                self.tokens.append(Token(TokenType.SLASH, '/', line, column))
                self.advance()
                continue
            
            if char == '%':
                self.tokens.append(Token(TokenType.PERCENT, '%', line, column))
                self.advance()
                continue
            
            if char == '=':
                if self.peek() == '=':
                    self.advance()
                    self.tokens.append(Token(TokenType.EQUAL, '==', line, column))
                else:
                    self.tokens.append(Token(TokenType.ASSIGN, '=', line, column))
                self.advance()
                continue
            
            if char == '!':
                if self.peek() == '=':
                    self.advance()
                    self.tokens.append(Token(TokenType.NOT_EQUAL, '!=', line, column))
                else:
                    self.tokens.append(Token(TokenType.NOT, '!', line, column))
                self.advance()
                continue
            
            if char == '<':
                if self.peek() == '=':
                    self.advance()
                    self.tokens.append(Token(TokenType.LESS_EQUAL, '<=', line, column))
                else:
                    self.tokens.append(Token(TokenType.LESS, '<', line, column))
                self.advance()
                continue
            
            if char == '>':
                if self.peek() == '=':
                    self.advance()
                    self.tokens.append(Token(TokenType.GREATER_EQUAL, '>=', line, column))
                else:
                    self.tokens.append(Token(TokenType.GREATER, '>', line, column))
                self.advance()
                continue
            
            if char == '(':
                self.tokens.append(Token(TokenType.LPAREN, '(', line, column))
                self.advance()
                continue
            
            if char == ')':
                self.tokens.append(Token(TokenType.RPAREN, ')', line, column))
                self.advance()
                continue
            
            if char == '{':
                self.tokens.append(Token(TokenType.LBRACE, '{', line, column))
                self.advance()
                continue
            
            if char == '}':
                self.tokens.append(Token(TokenType.RBRACE, '}', line, column))
                self.advance()
                continue
            
            if char == '[':
                self.tokens.append(Token(TokenType.LBRACKET, '[', line, column))
                self.advance()
                continue
            
            if char == ']':
                self.tokens.append(Token(TokenType.RBRACKET, ']', line, column))
                self.advance()
                continue
            
            if char == ':':
                self.tokens.append(Token(TokenType.COLON, ':', line, column))
                self.advance()
                continue
            
            if char == ',':
                self.tokens.append(Token(TokenType.COMMA, ',', line, column))
                self.advance()
                continue
            
            if char == '.':
                self.tokens.append(Token(TokenType.DOT, '.', line, column))
                self.advance()
                continue
            
            # Unknown character
            raise SyntaxError(f"Unexpected character '{char}' at line {line}, column {column}")
        
        self.tokens.append(Token(TokenType.EOF, None, self.line, self.column))
        return self.tokens


def tokenize(source: str) -> List[Token]:
    """Convenience function to tokenize source code."""
    lexer = Lexer(source)
    return lexer.tokenize()