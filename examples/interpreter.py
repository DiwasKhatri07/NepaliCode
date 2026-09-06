"""
NepaliLang Interpreter
Executes the Abstract Syntax Tree (AST) produced by the parser.
"""

from typing import Any, Dict, List, Optional
from src.parser import (
    NumberNode, StringNode, BooleanNode, NullNode, VariableNode,
    BinaryOpNode, UnaryOpNode, AssignmentNode, FunctionCallNode,
    FunctionDefNode, ReturnNode, IfNode, WhileNode, ForNode,
    ListNode, MapNode, IndexNode, ImportNode, PrintNode
)


class NepaliRuntimeError(Exception):
    """Custom error for NepaliLang runtime errors."""
    def __init__(self, message: str, line: int = 0, column: int = 0):
        self.message = message
        self.line = line
        self.column = column
        super().__init__(f"Line {line}, Column {column}: {message}")


class ReturnValue:
    """Used to handle return statements in functions."""
    def __init__(self, value: Any):
        self.value = value


class Interpreter:
    def __init__(self):
        self.variables: Dict[str, Any] = {}
        self.functions: Dict[str, FunctionDefNode] = {}
        self.modules: Dict[str, Any] = {}
        
        # Built-in functions
        self.builtins = {
            'print': self.builtin_print,
            'yo': self.builtin_yo,
            'len': self.builtin_len,
            'type': self.builtin_type,
            'str': self.builtin_str,
            'int': self.builtin_int,
            'float': self.builtin_float,
            'bool': self.builtin_bool,
            'list': self.builtin_list,
            'range': self.builtin_range,
            'sum': self.builtin_sum,
            'min': self.builtin_min,
            'max': self.builtin_max,
            'abs': self.builtin_abs,
            'round': self.builtin_round,
        }
    
    def interpret(self, statements: List[Any]) -> Any:
        """Interpret a list of statements."""
        result = None
        for statement in statements:
            result = self.execute(statement)
        return result
    
    def execute(self, node: Any) -> Any:
        """Execute a single AST node."""
        if isinstance(node, NumberNode):
            return node.value
        
        if isinstance(node, StringNode):
            return node.value
        
        if isinstance(node, BooleanNode):
            return node.value
        
        if isinstance(node, NullNode):
            return None
        
        if isinstance(node, VariableNode):
            return self.get_variable(node.name)
        
        if isinstance(node, BinaryOpNode):
            return self.execute_binary_op(node)
        
        if isinstance(node, UnaryOpNode):
            return self.execute_unary_op(node)
        
        if isinstance(node, AssignmentNode):
            return self.execute_assignment(node)
        
        if isinstance(node, FunctionCallNode):
            return self.execute_function_call(node)
        
        if isinstance(node, FunctionDefNode):
            return self.execute_function_def(node)
        
        if isinstance(node, ReturnNode):
            return ReturnValue(self.evaluate(node.value) if node.value else None)
        
        if isinstance(node, IfNode):
            return self.execute_if(node)
        
        if isinstance(node, WhileNode):
            return self.execute_while(node)
        
        if isinstance(node, ForNode):
            return self.execute_for(node)
        
        if isinstance(node, ListNode):
            return self.execute_list(node)
        
        if isinstance(node, MapNode):
            return self.execute_map(node)
        
        if isinstance(node, IndexNode):
            return self.execute_index(node)
        
        if isinstance(node, ImportNode):
            return self.execute_import(node)
        
        if isinstance(node, PrintNode):
            return self.execute_print(node)
        
        raise NepaliRuntimeError(f"Unknown node type: {type(node)}")
    
    def evaluate(self, node: Any) -> Any:
        """Evaluate an expression node."""
        return self.execute(node)
    
    def get_variable(self, name: str) -> Any:
        """Get a variable value."""
        # Handle member access (module.function)
        if '.' in name:
            parts = name.split('.')
            module_name = parts[0]
            member_name = parts[1]
            
            # Get the module
            if module_name in self.modules:
                module = self.modules[module_name]
                if member_name in module:
                    return module[member_name]
            
            raise NepaliRuntimeError(f"Undefined member: {name}")
        
        if name in self.variables:
            return self.variables[name]
        if name in self.functions:
            return self.functions[name]
        if name in self.builtins:
            return self.builtins[name]
        raise NepaliRuntimeError(f"Undefined variable: {name}")
    
    def set_variable(self, name: str, value: Any):
        """Set a variable value."""
        self.variables[name] = value
    
    def execute_binary_op(self, node: BinaryOpNode) -> Any:
        """Execute binary operation."""
        left = self.evaluate(node.left)
        right = self.evaluate(node.right)
        
        if node.operator == '+':
            if isinstance(left, str) or isinstance(right, str):
                return str(left) + str(right)
            return left + right
        
        if node.operator == '-':
            return left - right
        
        if node.operator == '*':
            return left * right
        
        if node.operator == '/':
            if right == 0:
                raise NepaliRuntimeError("Division by zero")
            return left / right
        
        if node.operator == '%':
            return left % right
        
        if node.operator == '**':
            return left ** right
        
        if node.operator == '==':
            return left == right
        
        if node.operator == '!=':
            return left != right
        
        if node.operator == '<':
            return left < right
        
        if node.operator == '<=':
            return left <= right
        
        if node.operator == '>':
            return left > right
        
        if node.operator == '>=':
            return left >= right
        
        raise NepaliRuntimeError(f"Unknown operator: {node.operator}")
    
    def execute_unary_op(self, node: UnaryOpNode) -> Any:
        """Execute unary operation."""
        operand = self.evaluate(node.operand)
        
        if node.operator == '-':
            return -operand
        
        if node.operator in ('not', '!'):
            return not operand
        
        raise NepaliRuntimeError(f"Unknown unary operator: {node.operator}")
    
    def execute_assignment(self, node: AssignmentNode) -> Any:
        """Execute variable assignment."""
        value = self.evaluate(node.value)
        self.set_variable(node.name, value)
        return value
    
    def execute_function_call(self, node: FunctionCallNode) -> Any:
        """Execute function call."""
        function = self.evaluate(node.function)
        
        # Evaluate arguments
        args = [self.evaluate(arg) for arg in node.arguments]
        
        # Built-in function
        if callable(function):
            return function(*args)
        
        # User-defined function
        if isinstance(function, FunctionDefNode):
            return self.call_user_function(function, args)
        
        # Module function (Python callable)
        if hasattr(function, '__call__'):
            return function(*args)
        
        raise NepaliRuntimeError(f"'{function}' is not callable")
    
    def execute_function_def(self, node: FunctionDefNode) -> FunctionDefNode:
        """Execute function definition."""
        self.functions[node.name] = node
        return node
    
    def call_user_function(self, function: FunctionDefNode, args: List[Any]) -> Any:
        """Call a user-defined function."""
        # Create new scope
        old_variables = self.variables.copy()
        
        # Bind parameters
        for i, param in enumerate(function.parameters):
            if i < len(args):
                self.variables[param] = args[i]
            else:
                self.variables[param] = None
        
        # Execute function body
        try:
            result = None
            for statement in function.body:
                result = self.execute(statement)
                
                # Handle return statement
                if isinstance(result, ReturnValue):
                    return result.value
        finally:
            # Restore scope
            self.variables = old_variables
        
        return result
    
    def execute_if(self, node: IfNode) -> Any:
        """Execute if statement."""
        condition = self.evaluate(node.condition)
        
        if condition:
            return self.interpret(node.body)
        
        # Check elif branches
        if node.elif_branches:
            for elif_condition, elif_body in node.elif_branches:
                if self.evaluate(elif_condition):
                    return self.interpret(elif_body)
        
        # Check else
        if node.else_body:
            return self.interpret(node.else_body)
        
        return None
    
    def execute_while(self, node: WhileNode) -> Any:
        """Execute while loop."""
        result = None
        while self.evaluate(node.condition):
            result = self.interpret(node.body)
        return result
    
    def execute_for(self, node: ForNode) -> Any:
        """Execute for loop."""
        iterable = self.evaluate(node.iterable)
        result = None
        
        # Handle range-like iteration
        if isinstance(iterable, int):
            for i in range(iterable):
                self.set_variable(node.variable, i)
                result = self.interpret(node.body)
        # Handle list/set iteration
        elif isinstance(iterable, (list, set)):
            for item in iterable:
                self.set_variable(node.variable, item)
                result = self.interpret(node.body)
        # Handle string iteration
        elif isinstance(iterable, str):
            for char in iterable:
                self.set_variable(node.variable, char)
                result = self.interpret(node.body)
        else:
            raise NepaliRuntimeError(f"Cannot iterate over {type(iterable)}")
        
        return result
    
    def execute_list(self, node: ListNode) -> List[Any]:
        """Execute list literal."""
        return [self.evaluate(element) for element in node.elements]
    
    def execute_map(self, node: MapNode) -> Dict[str, Any]:
        """Execute map literal."""
        result = {}
        for key, value in node.pairs:
            key_value = self.evaluate(key)
            value_value = self.evaluate(value)
            result[str(key_value)] = value_value
        return result
    
    def execute_index(self, node: IndexNode) -> Any:
        """Execute index operation."""
        target = self.evaluate(node.target)
        index = self.evaluate(node.index)
        
        if isinstance(target, (list, str)):
            return target[index]
        
        if isinstance(target, dict):
            return target[str(index)]
        
        raise NepaliRuntimeError(f"Cannot index {type(target)}")
    
    def execute_import(self, node: ImportNode) -> Any:
        """Execute import statement."""
        module_name = node.module
        
        # Check if it's a standard library module
        if module_name in self.modules:
            return self.modules[module_name]
        
        # Try to load from stdlib manually
        try:
            import sys
            import os
            
            # Add stdlib to path
            stdlib_path = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), 'stdlib')
            if stdlib_path not in sys.path:
                sys.path.insert(0, stdlib_path)
            
            # Try to import the module
            module = __import__(module_name)
            
            # Create a namespace for the module
            namespace = {}
            for name in dir(module):
                if not name.startswith('_'):
                    namespace[name] = getattr(module, name)
            
            self.modules[module_name] = namespace
            return namespace
            
        except ImportError:
            # Create a simple module placeholder if not found
            module = {'__name__': module_name}
            self.modules[module_name] = module
            return module
    
    def execute_print(self, node: PrintNode) -> Any:
        """Execute print statement."""
        if node.value:
            value = self.evaluate(node.value)
            print(value)
        else:
            print()
        return None
    
    # Built-in functions
    def builtin_print(self, *args) -> None:
        """Built-in print function."""
        print(*args)
    
    def builtin_yo(self, value: Any) -> Any:
        """Convenience constructor - returns value as-is."""
        return value
    
    def builtin_len(self, obj: Any) -> int:
        """Built-in len function."""
        return len(obj)
    
    def builtin_type(self, obj: Any) -> str:
        """Built-in type function."""
        return type(obj).__name__
    
    def builtin_str(self, obj: Any) -> str:
        """Built-in str function."""
        return str(obj)
    
    def builtin_int(self, obj: Any) -> int:
        """Built-in int function."""
        return int(obj)
    
    def builtin_float(self, obj: Any) -> float:
        """Built-in float function."""
        return float(obj)
    
    def builtin_bool(self, obj: Any) -> bool:
        """Built-in bool function."""
        return bool(obj)
    
    def builtin_list(self, obj: Any) -> list:
        """Built-in list function."""
        return list(obj)
    
    def builtin_range(self, *args) -> list:
        """Built-in range function."""
        return list(range(*args))
    
    def builtin_sum(self, obj: Any) -> Any:
        """Built-in sum function."""
        return sum(obj)
    
    def builtin_min(self, *args) -> Any:
        """Built-in min function."""
        if len(args) == 1:
            return min(args[0])
        return min(args)
    
    def builtin_max(self, *args) -> Any:
        """Built-in max function."""
        if len(args) == 1:
            return max(args[0])
        return max(args)
    
    def builtin_abs(self, obj: Any) -> Any:
        """Built-in abs function."""
        return abs(obj)
    
    def builtin_round(self, obj: Any, digits: int = 0) -> Any:
        """Built-in round function."""
        return round(obj, digits)


def interpret(statements: List[Any]) -> Any:
    """Convenience function to interpret AST statements."""
    interpreter = Interpreter()
    return interpreter.interpret(statements)