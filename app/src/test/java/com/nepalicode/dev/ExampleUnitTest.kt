package com.nepalicode.dev

import com.example.nepalicode.editor.HighlightTokenType
import com.example.nepalicode.editor.IdeThemeMode
import com.example.nepalicode.editor.IdeThemeRegistry
import com.example.nepalicode.editor.NepaliCompletionEngine
import com.example.nepalicode.editor.NepaliSyntaxHighlighter
import com.example.nepalicode.editor.NepaliTokenizer
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun tokenizer_identifiesNepaliKeywordsAndStrings() {
    val code = "kaam jod(a, b):\n    firta a + b\nnaam = \"Diwas\"\n# comment"
    val tokenizer = NepaliTokenizer(code)
    val (tokens, diagnostics) = tokenizer.tokenize()

    assertTrue(tokens.any { it.type == HighlightTokenType.DEF_KEYWORD && it.text == "kaam" })
    assertTrue(tokens.any { it.type == HighlightTokenType.FUNCTION_NAME && it.text == "jod" })
    assertTrue(tokens.any { it.type == HighlightTokenType.KEYWORD_NEPALI && it.text == "firta" })
    assertTrue(tokens.any { it.type == HighlightTokenType.STRING && it.text == "\"Diwas\"" })
    assertTrue(tokens.any { it.type == HighlightTokenType.COMMENT })
    assertEquals(0, diagnostics.size)
  }

  @Test
  fun tokenizer_identifiesUnclosedStringError() {
    val code = "x = \"unclosed string"
    val tokenizer = NepaliTokenizer(code)
    val (tokens, diagnostics) = tokenizer.tokenize()

    assertTrue(tokens.any { it.type == HighlightTokenType.SYNTAX_ERROR })
    assertTrue(diagnostics.isNotEmpty())
  }

  @Test
  fun syntaxHighlighter_producesAnnotatedString() {
    val code = "yedi x > 0:\n    dekha(\"Namaste\")"
    val annotated = NepaliSyntaxHighlighter.highlight(code, IdeThemeRegistry.HighDensityDark)
    assertEquals(code, annotated.text)
    assertTrue(annotated.spanStyles.isNotEmpty())
  }

  @Test
  fun completionEngine_providesSuggestions() {
    val code = "anurodh."
    val suggestions = NepaliCompletionEngine.getSuggestions(code, code.length)
    assertTrue(suggestions.isNotEmpty())
    assertTrue(suggestions.any { it.label.startsWith("get") })
  }

  @Test
  fun themeRegistry_providesAllThemes() {
    for (mode in IdeThemeMode.values()) {
      val palette = IdeThemeRegistry.getPalette(mode)
      assertNotNull(palette)
      assertEquals(mode, palette.mode)
    }
  }
}
