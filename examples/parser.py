"""
NepaliLang Parser
Builds an Abstract Syntax Tree (AST) from tokens.
"""

from typing import List, Optional, Any
from dataclasses import dataclass
from src.lexer import Token, TokenType


# AST Node Types
@dataclass
class NumberNode:
    value: float


@dataclass
class StringNode:
    value: str


@dataclass
class BooleanNode:
    value: bool


@dataclass
class NullNode:
    pass


@dataclass
class VariableNode:
    name: str


@dataclass
class BinaryOpNode:
    left: Any
    operator: str
    right: Any


@dataclass
class UnaryOpNode:
    operator: str
    operand: Any


@dataclass
class AssignmentNode:
    name: str
    value: Any


@dataclass
class FunctionCallNode:
    function: Any
    arguments: List[Any]


@dataclass
class FunctionDefNode:
    name: str
    parameters: List[str]
    body: List[Any]


@dataclass
class ReturnNode:
    value: Any


@dataclass
class IfNode:
    condition: Any
    body: List[Any]
    else_body: Optional[List[Any]] = None
    elif_branches: Optional[List[tuple]] = None


@dataclass
class WhileNode:
    condition: Any
    body: List[Any]


@dataclass
class ForNode:
    variable: str
    iterable: Any
    body: List[Any]


@dataclass
class ListNode:
    elements: List[Any]


@dataclass
class MapNode:
    pairs: List[tuple]  # List of (key, value) tuples


@dataclass
class IndexNode:
    target: Any
    index: Any


@dataclass
class ImportNode:
    module: str
    names: Optional[List[str]] = None


@dataclass
class PrintNode:
    value: Any


class Parser:
    def __init__(self, tokens: List[Token]):
        self.tokens = tokens
        self.pos = 0
    
    def current_token(self) -> Optional[Token]:
        if self.pos < len(self.tokens):
            return self.tokens[self.pos]
        return None
    
    def advance(self) -> Optional[Token]:
        token = self.current_token()
        if token:
            self.pos += 1
        return token
    
    def peek(self, offset: int = 1) -> Optional[Token]:
        peek_pos = self.pos + offset
        if peek_pos < len(self.tokens):
            return self.tokens[peek_pos]
        return None
    
    def expect(self, token_type: TokenType) -> Token:
        token = self.current_token()
        if token and token.type == token_type:
            return self.advance()
        raise SyntaxError(f"Expected {token_type}, got {token.type if token else 'EOF'}")
    
    def parse(self) -> List[Any]:
        """Parse the entire token list into a list of statements."""
        statements = []
        
        while self.current_token() and self.current_token().type != TokenType.EOF:
            # Skip newlines
            if self.current_token().type == TokenType.NEWLINE:
                self.advance()
                continue
            
            statement = self.parse_statement()
            if statement:
                statements.append(statement)
        
        return statements
    
    def parse_statement(self) -> Any:
        """Parse a single statement."""
        token = self.current_token()
        
        if not token:
            return None
        
        # Import statements
        if token.type in (TokenType.IMPORT, TokenType.LYAU):
            return self.parse_import()
        
        # Function definitions
        if token.type in (TokenType.DEF, TokenType.KAAM):
            return self.parse_function_def()
        
        # Return statements
        if token.type in (TokenType.RETURN, TokenType.FIRTA):
            return self.parse_return()
        
        # If statements
        if token.type in (TokenType.IF, TokenType.YEDI):
            return self.parse_if()
        
        # While statements
        if token.type in (TokenType.WHILE, TokenType.JABASAMMA):
            return self.parse_while()
        
        # For statements
        if token.type in (TokenType.FOR, TokenType.KO_LAGI):
            return self.parse_for()
        
        # Variable assignment
        if self.peek_is_assignment():
            return self.parse_assignment()
        
        # Function calls (including print)
        expr = self.parse_expression()
        
        # Check if this is a print statement
        if isinstance(expr, FunctionCallNode):
            if isinstance(expr.function, VariableNode) and expr.function.name == 'print':
                return PrintNode(expr.arguments[0] if expr.arguments else None)
        
        return expr
    
    def parse_import(self) -> ImportNode:
        """Parse import statement."""
        token = self.advance()
        
        # Check if it's "from X import Y" style
        if token.type in (TokenType.FROM, TokenType.BATA):
            module = self.expect(TokenType.IDENTIFIER).value
            self.expect(TokenType.IMPORT if self.current_token().type == TokenType.IMPORT else TokenType.LYAU)
            
            names = []
            while self.current_token() and self.current_token().type == TokenType.IDENTIFIER:
                names.append(self.advance().value)
                if self.current_token() and self.current_token().type == TokenType.COMMA:
                    self.advance()
            
            return ImportNode(module, names)
        
        # Simple "import X" style
        module = self.expect(TokenType.IDENTIFIER).value
        return ImportNode(module)
    
    def parse_function_def(self) -> FunctionDefNode:
        """Parse function definition."""
        self.advance()  # Skip 'def' or 'kaam'
        name = self.expect(TokenType.IDENTIFIER).value
        
        self.expect(TokenType.LPAREN)
        parameters = []
        
        if self.current_token() and self.current_token().type == TokenType.IDENTIFIER:
            parameters.append(self.advance().value)
            while self.current_token() and self.current_token().type == TokenType.COMMA:
                self.advance()
                if self.current_token() and self.current_token().type == TokenType.IDENTIFIER:
                    parameters.append(self.advance().value)
        
        self.expect(TokenType.RPAREN)
        self.expect(TokenType.COLON)
        
        # Skip newline after colon
        if self.current_token() and self.current_token().type == TokenType.NEWLINE:
            self.advance()
        
        body = self.parse_block()
        
        return FunctionDefNode(name, parameters, body)
    
    def parse_return(self) -> ReturnNode:
        """Parse return statement."""
        self.advance()  # Skip 'return' or 'firta'
        
        if self.current_token() and self.current_token().type not in (TokenType.NEWLINE, TokenType.EOF):
            value = self.parse_expression()
        else:
            value = None
        
        return ReturnNode(value)
    
    def parse_if(self) -> IfNode:
        """Parse if/elif/else statement."""
        self.advance()  # Skip 'if' or 'yedi'
        
        condition = self.parse_expression()
        self.expect(TokenType.COLON)
        
        # Skip newline after colon
        if self.current_token() and self.current_token().type == TokenType.NEWLINE:
            self.advance()
        
        body = []
        # Parse body until we hit else/elif/EOF
        while self.current_token() and self.current_token().type not in (TokenType.ELSE, TokenType.NATRA, 
                                                                           TokenType.ELIF, TokenType.NATABHAYE,
                                                                           TokenType.EOF):
            if self.current_token().type == TokenType.NEWLINE:
                self.advance()
                continue
            statement = self.parse_statement()
            if statement:
                body.append(statement)
        
        elif_branches = []
        else_body = None
        
        # Check for elif
        while self.current_token() and self.current_token().type in (TokenType.ELIF, TokenType.NATABHAYE):
            self.advance()
            elif_condition = self.parse_expression()
            self.expect(TokenType.COLON)
            
            # Skip newline after colon
            if self.current_token() and self.current_token().type == TokenType.NEWLINE:
                self.advance()
            
            elif_body = []
            while self.current_token() and self.current_token().type not in (TokenType.ELSE, TokenType.NATRA, 
                                                                               TokenType.ELIF, TokenType.NATABHAYE,
                                                                               TokenType.EOF):
                if self.current_token().type == TokenType.NEWLINE:
                    self.advance()
                    continue
                statement = self.parse_statement()
                if statement:
                    elif_body.append(statement)
            
            elif_branches.append((elif_condition, elif_body))
        
        # Check for else
        if self.current_token() and self.current_token().type in (TokenType.ELSE, TokenType.NATRA):
            self.advance()
            self.expect(TokenType.COLON)
            
            # Skip newline after colon
            if self.current_token() and self.current_token().type == TokenType.NEWLINE:
                self.advance()
            
            else_body = []
            while self.current_token() and self.current_token().type != TokenType.EOF:
                if self.current_token().type == TokenType.NEWLINE:
                    self.advance()
                    continue
                statement = self.parse_statement()
                if statement:
                    else_body.append(statement)
        
        return IfNode(condition, body, else_body, elif_branches)
    
    def parse_while(self) -> WhileNode:
        """Parse while loop."""
        self.advance()  # Skip 'while' or 'jabasamma'
        
        condition = self.parse_expression()
        self.expect(TokenType.COLON)
        
        # Skip newline after colon
        if self.current_token() and self.current_token().type == TokenType.NEWLINE:
            self.advance()
        
        body = self.parse_block()
        
        return WhileNode(condition, body)
    
    def parse_for(self) -> ForNode:
        """Parse for loop."""
        self.advance()  # Skip 'for' or 'ko_lagi'
        
        variable = self.expect(TokenType.IDENTIFIER).value
        self.expect(TokenType.IN if self.current_token().type == TokenType.IN else TokenType.MA)
        
        iterable = self.parse_expression()
        self.expect(TokenType.COLON)
        
        # Skip newline after colon
        if self.current_token() and self.current_token().type == TokenType.NEWLINE:
            self.advance()
        
        body = self.parse_block()
        
        return ForNode(variable, iterable, body)
    
    def parse_assignment(self) -> AssignmentNode:
        """Parse variable assignment."""
        name = self.expect(TokenType.IDENTIFIER).value
        
        # Check for compound assignment
        if self.current_token().type == TokenType.PLUS_ASSIGN:
            self.advance()
            value = BinaryOpNode(VariableNode(name), '+', self.parse_expression())
        elif self.current_token().type == TokenType.MINUS_ASSIGN:
            self.advance()
            value = BinaryOpNode(VariableNode(name), '-', self.parse_expression())
        else:
            self.expect(TokenType.ASSIGN)
            value = self.parse_expression()
        
        return AssignmentNode(name, value)
    
    def peek_is_assignment(self) -> bool:
        """Check if the current position looks like an assignment."""
        if not self.current_token() or self.current_token().type != TokenType.IDENTIFIER:
            return False
        
        next_token = self.peek()
        if not next_token:
            return False
        
        return next_token.type in (TokenType.ASSIGN, TokenType.PLUS_ASSIGN, TokenType.MINUS_ASSIGN)
    
    def parse_block(self) -> List[Any]:
        """Parse a block of statements with simple indentation tracking."""
        statements = []
        
        # Skip newline after colon
        if self.current_token() and self.current_token().type == TokenType.NEWLINE:
            self.advance()
        
        # For now, we'll use a count-based approach with proper stopping conditions
        # This is a simplification - proper implementation would track actual indentation
        statement_count = 0
        max_statements = 10  # Safety limit to prevent consuming whole file
        
        while self.current_token() and self.current_token().type != TokenType.EOF and statement_count < max_statements:
            if self.current_token().type == TokenType.NEWLINE:
                self.advance()
                continue
            
            # Stop at block-level keywords that might indicate end of block
            if self.current_token() and self.current_token().type in (
                TokenType.ELSE, TokenType.NATRA, 
                TokenType.ELIF, TokenType.NATABHAYE
            ):
                break
            
            statement = self.parse_statement()
            if statement:
                statements.append(statement)
                statement_count += 1
                
                # If we parsed a return statement, the function body is done
                if isinstance(statement, ReturnNode):
                    break
        
        return statements
    
    def parse_expression(self) -> Any:
        """Parse an expression."""
        return self.parse_comparison()
    
    def parse_comparison(self) -> Any:
        """Parse comparison expressions."""
        left = self.parse_additive()
        
        while self.current_token() and self.current_token().type in (
            TokenType.EQUAL, TokenType.NOT_EQUAL,
            TokenType.LESS, TokenType.LESS_EQUAL,
            TokenType.GREATER, TokenType.GREATER_EQUAL
        ):
            op_token = self.advance()
            right = self.parse_additive()
            left = BinaryOpNode(left, op_token.value, right)
        
        return left
    
    def parse_additive(self) -> Any:
        """Parse additive expressions (+, -)."""
        left = self.parse_multiplicative()
        
        while self.current_token() and self.current_token().type in (TokenType.PLUS, TokenType.MINUS):
            op_token = self.advance()
            right = self.parse_multiplicative()
            left = BinaryOpNode(left, op_token.value, right)
        
        return left
    
    def parse_multiplicative(self) -> Any:
        """Parse multiplicative expressions (*, /, %)."""
        left = self.parse_power()
        
        while self.current_token() and self.current_token().type in (TokenType.STAR, TokenType.SLASH, TokenType.PERCENT):
            op_token = self.advance()
            right = self.parse_power()
            left = BinaryOpNode(left, op_token.value, right)
        
        return left
    
    def parse_power(self) -> Any:
        """Parse power expressions (**)."""
        left = self.parse_unary()
        
        if self.current_token() and self.current_token().type == TokenType.POWER:
            op_token = self.advance()
            right = self.parse_unary()
            left = BinaryOpNode(left, op_token.value, right)
        
        return left
    
    def parse_unary(self) -> Any:
        """Parse unary expressions (-, not)."""
        if self.current_token() and self.current_token().type in (TokenType.MINUS, TokenType.NOT, TokenType.HOINA):
            op_token = self.advance()
            operand = self.parse_unary()
            return UnaryOpNode(op_token.value, operand)
        
        return self.parse_primary()
    
    def parse_primary(self) -> Any:
        """Parse primary expressions."""
        token = self.current_token()
        
        if not token:
            raise SyntaxError("Unexpected end of input")
        
        # Number
        if token.type == TokenType.NUMBER:
            self.advance()
            return NumberNode(token.value)
        
        # String
        if token.type == TokenType.STRING:
            self.advance()
            return StringNode(token.value)
        
        # Boolean
        if token.type == TokenType.BOOLEAN:
            self.advance()
            return BooleanNode(token.value)
        
        # Null
        if token.type == TokenType.NULL:
            self.advance()
            return NullNode()
        
        # Identifier or function call
        if token.type == TokenType.IDENTIFIER:
            name = self.advance().value
            
            # Check if it's a function call
            if self.current_token() and self.current_token().type == TokenType.LPAREN:
                return self.parse_function_call(VariableNode(name))
            
            # Check if it's an index operation
            if self.current_token() and self.current_token().type == TokenType.LBRACKET:
                return self.parse_index(VariableNode(name))
            
            # Check if it's member access (dot notation)
            if self.current_token() and self.current_token().type == TokenType.DOT:
                return self.parse_member_access(VariableNode(name))
            
            return VariableNode(name)
        
        # Parenthesized expression
        if token.type == TokenType.LPAREN:
            self.advance()
            expr = self.parse_expression()
            self.expect(TokenType.RPAREN)
            return expr
        
        # List
        if token.type == TokenType.LBRACKET:
            return self.parse_list()
        
        # Map
        if token.type == TokenType.LBRACE:
            return self.parse_map()
        
        # yo() function call
        if token.type == TokenType.YO:
            self.advance()
            return self.parse_function_call(VariableNode('yo'))
        
        raise SyntaxError(f"Unexpected token: {token.type}")
    
    def parse_function_call(self, function: Any) -> FunctionCallNode:
        """Parse function call."""
        self.expect(TokenType.LPAREN)
        
        arguments = []
        if self.current_token() and self.current_token().type != TokenType.RPAREN:
            arguments.append(self.parse_expression())
            while self.current_token() and self.current_token().type == TokenType.COMMA:
                self.advance()
                if self.current_token() and self.current_token().type != TokenType.RPAREN:
                    arguments.append(self.parse_expression())
        
        self.expect(TokenType.RPAREN)
        
        return FunctionCallNode(function, arguments)
    
    def parse_index(self, target: Any) -> IndexNode:
        """Parse index operation."""
        self.expect(TokenType.LBRACKET)
        index = self.parse_expression()
        self.expect(TokenType.RBRACKET)
        
        return IndexNode(target, index)
    
    def parse_member_access(self, target: Any) -> Any:
        """Parse member access (dot notation)."""
        self.expect(TokenType.DOT)
        member = self.expect(TokenType.IDENTIFIER).value
        
        # Check for chain: module.function()
        if self.current_token() and self.current_token().type == TokenType.LPAREN:
            # Return a function call with the dotted name
            function_name = f"{target.name}.{member}"
            return self.parse_function_call(VariableNode(function_name))
        
        # Otherwise, return as member access variable
        return VariableNode(f"{target.name}.{member}")
    
    def parse_list(self) -> ListNode:
        """Parse list literal."""
        self.expect(TokenType.LBRACKET)
        
        elements = []
        if self.current_token() and self.current_token().type != TokenType.RBRACKET:
            elements.append(self.parse_expression())
            while self.current_token() and self.current_token().type == TokenType.COMMA:
                self.advance()
                if self.current_token() and self.current_token().type != TokenType.RBRACKET:
                    elements.append(self.parse_expression())
        
        self.expect(TokenType.RBRACKET)
        
        return ListNode(elements)
    
    def parse_map(self) -> MapNode:
        """Parse map literal."""
        self.expect(TokenType.LBRACE)
        
        pairs = []
        if self.current_token() and self.current_token().type != TokenType.RBRACE:
            key = self.parse_expression()
            self.expect(TokenType.COLON)
            value = self.parse_expression()
            pairs.append((key, value))
            
            while self.current_token() and self.current_token().type == TokenType.COMMA:
                self.advance()
                if self.current_token() and self.current_token().type != TokenType.RBRACE:
                    key = self.parse_expression()
                    self.expect(TokenType.COLON)
                    value = self.parse_expression()
                    pairs.append((key, value))
        
        self.expect(TokenType.RBRACE)
        
        return MapNode(pairs)


def parse(tokens: List[Token]) -> List[Any]:
    """Convenience function to parse tokens into AST."""
    parser = Parser(tokens)
    return parser.parse()