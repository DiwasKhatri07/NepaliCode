# NepaliCode Language Support for VS Code

This extension adds lightweight editor support for NepaliCode and NepaliLang source files. It associates `.np` files with the `nepalicode` language mode and provides TextMate syntax highlighting, bracket handling, indentation hints, code folding markers, and starter snippets.

## Features

| Feature | Details |
|---|---|
| File association | Opens `.np` files as NepaliCode automatically. |
| Syntax highlighting | Highlights Nepali and English control-flow keywords, functions, strings, numbers, constants, operators, built-ins, modules, comments, and decorators. |
| Editor behavior | Adds `#` line comments, auto-closing brackets and quotes, matching brackets, and basic block indentation rules. |
| Snippets | Includes `kaam`, `yedi`, `kolagi`, `lyau`, `koshish`, and `anurodhget` snippets. |

## Local installation

From the repository root, package the extension with the VS Code Extension Manager (`vsce`) and install the generated VSIX:

```bash
npm install --global @vscode/vsce
cd extensions/nepalicode
vsce package
code --install-extension nepalicode-language-support-0.1.0.vsix
```

Alternatively, open the repository in VS Code and press `F5` from an extension-development window. Then open any `.np` file and select **NepaliCode** if the language mode is not detected automatically.

## Example

```nepalicode
lyau anurodh

kaam fetch_title(url):
    res = anurodh.get(url)
    yedi res.status == 200:
        firta res.text
    natra:
        firta khali

print(fetch_title("https://example.com"))
```

The grammar intentionally focuses on editor feedback. It does not execute NepaliCode, provide a compiler, or replace the language runtime.

## Development

The extension is intentionally dependency-free. Grammar changes belong in `syntaxes/nepalicode.tmLanguage.json`; editor behavior belongs in `language-configuration.json`; and reusable templates belong in `snippets/nepalicode.json`.

After changing the grammar, validate the JSON files and open representative `.np` examples in VS Code. New language keywords should be added consistently with the Android tokenizer and documented in the main NepaliCode README.

## License

This extension follows the repository's [MIT License](../../LICENSE).
