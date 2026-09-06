"""
NepaliLang Main Entry Point
CLI interface for running .np files and REPL.
"""

import sys
import os
import io

# Set UTF-8 encoding for stdout to handle Unicode
if sys.platform == 'win32':
    sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8', errors='replace')
    sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding='utf-8', errors='replace')

# Add parent directory to path for imports
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from src.lexer import tokenize
from src.parser import parse
from src.interpreter import interpret, NepaliRuntimeError


def run_file(filename: str):
    """Run a .np file."""
    try:
        with open(filename, 'r', encoding='utf-8') as f:
            source = f.read()
        
        tokens = tokenize(source)
        ast = parse(tokens)
        interpret(ast)
        
    except FileNotFoundError:
        print(f"Error: File '{filename}' not found.")
        sys.exit(1)
    except SyntaxError as e:
        print(f"Syntax Error: {e}")
        sys.exit(1)
    except NepaliRuntimeError as e:
        print(f"Runtime Error: {e}")
        sys.exit(1)
    except Exception as e:
        print(f"Error: {e}")
        sys.exit(1)


def run_repl():
    """Run the interactive REPL."""
    print("NepaliLang 0.1.0")
    print("Made by Diwas Khatri")
    print("NepaliSource")
    print()
    print("Type 'exit' to quit, 'help' for commands")
    print()
    
    while True:
        try:
            line = input(">>> ")
            
            if line.strip() == 'exit':
                break
            
            if line.strip() == 'help':
                print("Available commands:")
                print("  exit  - Exit the REPL")
                print("  help  - Show this help message")
                print()
                continue
            
            if line.strip() == '':
                continue
            
            # Tokenize, parse, and interpret
            tokens = tokenize(line)
            ast = parse(tokens)
            result = interpret(ast)
            
            if result is not None:
                print(result)
        
        except KeyboardInterrupt:
            print()
            print("Use 'exit' to quit.")
        except EOFError:
            print()
            break
        except SyntaxError as e:
            print(f"Syntax Error: {e}")
        except NepaliRuntimeError as e:
            print(f"Runtime Error: {e}")
        except Exception as e:
            print(f"Error: {e}")


def main():
    """Main entry point."""
    if len(sys.argv) < 2:
        run_repl()
    else:
        command = sys.argv[1]
        
        if command == '--version':
            print("NepaliLang 0.1.0")
            print("Runtime 0.1.0")
            print("NPPM 0.1.0")
        elif command == '--help':
            print("NepaliLang - A simple programming language from Nepal")
            print()
            print("Usage:")
            print("  nepali              Start REPL")
            print("  nepali <file.np>    Run a .np file")
            print("  nepali --version    Show version")
            print("  nepali --help       Show this help")
        else:
            # Assume it's a file
            run_file(command)


if __name__ == '__main__':
    main()