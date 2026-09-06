package com.nepalicode.dev.nepalicode.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nepalicode.dev.nepalicode.ai.AiAssistantResponse
import com.nepalicode.dev.nepalicode.ai.NepaliAiAssistant
import com.nepalicode.dev.nepalicode.data.ProjectFile
import com.nepalicode.dev.nepalicode.data.ProjectManager
import com.nepalicode.dev.nepalicode.editor.CodeSuggestion
import com.nepalicode.dev.nepalicode.editor.IdeThemeMode
import com.nepalicode.dev.nepalicode.editor.NepaliCompletionEngine
import com.nepalicode.dev.nepalilang.core.LintIssue
import com.nepalicode.dev.nepalilang.core.NepaliConverter
import com.nepalicode.dev.nepalilang.core.NepaliFormatter
import com.nepalicode.dev.nepalilang.core.NepaliIdeCallbacks
import com.nepalicode.dev.nepalilang.core.NepaliInterpreter
import com.nepalicode.dev.nepalilang.core.NepaliLexer
import com.nepalicode.dev.nepalilang.core.NepaliLinter
import com.nepalicode.dev.nepalilang.core.NepaliParseException
import com.nepalicode.dev.nepalilang.core.NepaliParser
import com.nepalicode.dev.nepalilang.core.NepaliRuntimeException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

enum class IdeTab {
    EDITOR,
    TERMINAL,
    WEB_AUTOMATION,
    FILES,
    NPPM,
    DOCS
}

enum class TerminalEntryType {
    STDOUT,
    STDERR,
    SYSTEM,
    INPUT_PROMPT,
    REPL_PROMPT
}

data class TerminalEntry(
    val type: TerminalEntryType,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class AutomationLogItem(
    val action: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class IdeUiState(
    val currentTab: IdeTab = IdeTab.EDITOR,
    val files: List<ProjectFile> = emptyList(),
    val activeFileName: String = "main.np",
    val activeCode: String = "",
    val isRunning: Boolean = false,
    val terminalEntries: List<TerminalEntry> = emptyList(),
    val isWaitingForInput: Boolean = false,
    val inputPrompt: String = "",
    val webPreviewUrl: String = "https://example.com",
    val webPageTitle: String = "Example Domain",
    val automationLogs: List<AutomationLogItem> = emptyList(),
    val lintIssues: List<LintIssue> = emptyList(),
    val suggestions: List<CodeSuggestion> = emptyList(),
    val cursorPosition: Int = 0,
    val replHistory: List<String> = emptyList(),
    val replInput: String = "",
    val themeMode: IdeThemeMode = IdeThemeMode.HIGH_DENSITY_DARK,
    val fontSizeSp: Float = 13f,
    val showDevCreditsDialog: Boolean = false,
    val showThemeSelectorDialog: Boolean = false,
    val showNewFileDialog: Boolean = false,
    val showAiAssistantDialog: Boolean = false,
    val aiAssistantResponse: AiAssistantResponse? = null,
    val showCommandPalette: Boolean = false,
    val commandPaletteQuery: String = "",
    val showQuickExamplesSheet: Boolean = false,
    val isSidebarOpen: Boolean = false,
    val sidebarSearchQuery: String = "",
    val fileToRename: String? = null,
    val fileToDelete: String? = null,
    val saveNotification: String? = null
)

class NepaliCodeViewModel(application: Application) : AndroidViewModel(application), NepaliIdeCallbacks {
    private val projectManager = ProjectManager(application)

    private val _uiState = MutableStateFlow(IdeUiState())
    val uiState: StateFlow<IdeUiState> = _uiState.asStateFlow()

    private var executionJob: Job? = null
    private var inputContinuation: Continuation<String>? = null
    private var sharedInterpreter: NepaliInterpreter? = null

    init {
        loadProject()
    }

    private fun loadProject() {
        val files = projectManager.loadFiles()
        val defaultFile = files.firstOrNull { it.name == "main.np" } ?: files.firstOrNull()
        val code = defaultFile?.content.orEmpty()
        val issues = NepaliLinter.lint(code)

        _uiState.update {
            it.copy(
                files = files,
                activeFileName = defaultFile?.name ?: "main.np",
                activeCode = code,
                lintIssues = issues,
                terminalEntries = listOf(
                    TerminalEntry(
                        TerminalEntryType.SYSTEM,
                        "NepaliLang 2.4.0 Mobile IDE (High-Density)\nCreated by Diwas Khatri (diwaskhatri935@gmail.com)\nType 'help' or 'credits' in REPL, or run .np scripts\n"
                    )
                ),
                suggestions = NepaliCompletionEngine.getSuggestions(code, 0)
            )
        }
    }

    fun selectTab(tab: IdeTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun setTheme(mode: IdeThemeMode) {
        _uiState.update { it.copy(themeMode = mode) }
    }

    fun setFontSize(size: Float) {
        _uiState.update { it.copy(fontSizeSp = size.coerceIn(10f, 22f)) }
    }

    fun increaseFontSize() {
        _uiState.update { it.copy(fontSizeSp = (it.fontSizeSp + 1f).coerceAtMost(22f)) }
    }

    fun decreaseFontSize() {
        _uiState.update { it.copy(fontSizeSp = (it.fontSizeSp - 1f).coerceAtLeast(10f)) }
    }

    fun openDevCredits() {
        _uiState.update { it.copy(showDevCreditsDialog = true) }
    }

    fun closeDevCredits() {
        _uiState.update { it.copy(showDevCreditsDialog = false) }
    }

    fun openThemeSelector() {
        _uiState.update { it.copy(showThemeSelectorDialog = true) }
    }

    fun closeThemeSelector() {
        _uiState.update { it.copy(showThemeSelectorDialog = false) }
    }

    fun openNewFileDialog() {
        _uiState.update { it.copy(showNewFileDialog = true) }
    }

    fun closeNewFileDialog() {
        _uiState.update { it.copy(showNewFileDialog = false) }
    }

    fun insertDevCreditsSnippet() {
        val snippet = "\n# NepaliLang (.np) by Diwas Khatri\ndekha(\"🇳🇵 NepaliLang v2.4.0 created by Diwas Khatri\")\n"
        insertQuickSymbol(snippet)
    }

    fun onCodeChanged(newCode: String, cursorIndex: Int) {
        val issues = NepaliLinter.lint(newCode)
        _uiState.update {
            it.copy(
                activeCode = newCode,
                cursorPosition = cursorIndex,
                lintIssues = issues,
                suggestions = NepaliCompletionEngine.getSuggestions(newCode, cursorIndex)
            )
        }
        // Auto-save modified content
        projectManager.saveFile(_uiState.value.activeFileName, newCode)
    }

    fun applySuggestion(suggestion: CodeSuggestion) {
        val currentCode = _uiState.value.activeCode
        val pos = _uiState.value.cursorPosition.coerceIn(0, currentCode.length)
        val textBefore = currentCode.substring(0, pos)
        val textAfter = currentCode.substring(pos)

        // If suggestion is preceded by word match, replace prefix
        val wordMatch = Regex("""([\p{L}_][\p{L}\p{N}_]*)$""").find(textBefore)
        val newBefore = if (wordMatch != null) {
            textBefore.substring(0, wordMatch.range.first) + suggestion.insertText
        } else {
            textBefore + suggestion.insertText
        }
        val fullNewCode = newBefore + textAfter
        onCodeChanged(fullNewCode, newBefore.length)
    }

    fun insertQuickSymbol(symbol: String) {
        val currentCode = _uiState.value.activeCode
        val pos = _uiState.value.cursorPosition.coerceIn(0, currentCode.length)
        val newBefore = currentCode.substring(0, pos) + symbol
        val newAfter = currentCode.substring(pos)
        val fullCode = newBefore + newAfter
        onCodeChanged(fullCode, newBefore.length)
    }

    fun runCurrentCode() {
        val fileName = _uiState.value.activeFileName
        val code = _uiState.value.activeCode

        // Switch to Terminal Tab to see output
        _uiState.update {
            it.copy(
                currentTab = IdeTab.TERMINAL,
                isRunning = true
            )
        }
        appendTerminalEntry(TerminalEntryType.SYSTEM, "\n$ nepali $fileName\n")

        executionJob?.cancel()
        executionJob = viewModelScope.launch {
            try {
                val startTime = System.currentTimeMillis()
                val lexer = NepaliLexer(code)
                val tokens = lexer.tokenize()

                val parser = NepaliParser(tokens)
                val ast = parser.parse()

                val interpreter = NepaliInterpreter(getApplication(), this@NepaliCodeViewModel, fileName)
                sharedInterpreter = interpreter
                interpreter.execute(ast)

                val elapsed = System.currentTimeMillis() - startTime
                appendTerminalEntry(TerminalEntryType.SYSTEM, "\n[Program finished in ${elapsed}ms]\n")
            } catch (e: NepaliParseException) {
                appendTerminalEntry(
                    TerminalEntryType.STDERR,
                    "\nNepaliLang Syntax Error:\n${e.message}\nLine ${e.line}, Col ${e.column}\nSuggestion: ${e.suggestion}\n"
                )
            } catch (e: NepaliRuntimeException) {
                appendTerminalEntry(TerminalEntryType.STDERR, "\n" + e.formatFormattedError() + "\n")
            } catch (e: Exception) {
                if (e !is kotlinx.coroutines.CancellationException) {
                    appendTerminalEntry(TerminalEntryType.STDERR, "\nRuntime Error: ${e.message ?: e.toString()}\n")
                }
            } finally {
                _uiState.update { it.copy(isRunning = false, isWaitingForInput = false) }
            }
        }
    }

    fun stopExecution() {
        executionJob?.cancel()
        inputContinuation?.resume("")
        inputContinuation = null
        appendTerminalEntry(TerminalEntryType.STDERR, "\n[Execution stopped by user]\n")
        _uiState.update { it.copy(isRunning = false, isWaitingForInput = false) }
    }

    fun submitRepl(command: String) {
        val trimmed = command.trim()
        if (trimmed.isEmpty()) return

        appendTerminalEntry(TerminalEntryType.REPL_PROMPT, ">>> $trimmed")

        if (trimmed == "clear" || trimmed == "safaa") {
            clearTerminal()
            return
        }

        if (trimmed == "help") {
            appendTerminalEntry(
                TerminalEntryType.SYSTEM,
                "NepaliLang Interactive Shell (REPL)\n" +
                    "Keywords: yedi, natra, natabhaye, kaam, firta, ko_lagi, jabasamma, lyau, sacho, jhut\n" +
                    "Types:    ank, shabda, dashamlav, satya_jhut, suchi, kosh\n" +
                    "Modules:  anurodh, web, browser, regex, automation, database, file, ganit, samaya\n" +
                    "Commands: 'credits', 'vars', 'modules', 'syntax', 'clear', 'help'\n"
            )
            return
        }

        if (trimmed == "modules") {
            appendTerminalEntry(
                TerminalEntryType.SYSTEM,
                "📦 Available NepaliLang Libraries & Modules:\n" +
                    " • anurodh     - HTTP Client (GET, POST, PUT, DELETE)\n" +
                    " • web         - Web Scraping & CSS Selector Extraction\n" +
                    " • browser     - Virtual Browser & DOM Automation\n" +
                    " • regex/khoj  - Regular Expressions & Pattern Matching\n" +
                    " • database    - SQLite Embedded Relational DB\n" +
                    " • automation  - Mouse, Keyboard & System Actions\n" +
                    " • ganit       - Math, Trig, Sqrt & Statistics\n" +
                    " • samaya      - Date, Clock, Epoch & Timers\n" +
                    " • file/folder - Virtual File System I/O\n" +
                    " • json        - JSON Serialization & Parsing\n" +
                    " • random      - Random Numbers & Choices\n" +
                    " • system      - Platform & OS Environment\n"
            )
            return
        }

        if (trimmed == "syntax") {
            appendTerminalEntry(
                TerminalEntryType.SYSTEM,
                "🇳🇵 NepaliLang Syntax Quick Reference:\n" +
                    " Function:    kaam jod(a, b): firta a + b\n" +
                    " Conditional: yedi x > 0: dekha('Positive') natra: dekha('Negative')\n" +
                    " Loop:        ko_lagi i ma range(5): dekha(i)\n" +
                    " While:       jabasamma a < 10: a = a + 1\n" +
                    " Types:       shabda('abc'), ank('42'), dashamlav('3.14'), suchi([1,2]), kosh({'k':'v'})\n" +
                    " Web Scrape:  page = web.get(url); headings = page.extract('h1')\n" +
                    " Regex:       regex.khoj('\\\\d+', 'Age 22')\n"
            )
            return
        }

        if (trimmed == "vars") {
            val envVars = sharedInterpreter?.globalEnv?.allVariables().orEmpty()
            val userVars = envVars.filterKeys { k ->
                !k.startsWith("__") && k !in listOf("print", "dekha", "input", "leu", "range", "daura", "yo", "len", "lambai", "type", "prakar", "prakaar", "sum", "jod", "min", "max", "abs", "round", "sorted", "kram", "ulta", "str", "shabda", "number", "ank", "decimal", "dashamlav", "bool", "satya_jhut", "list", "suchi", "map", "kosh")
            }
            if (userVars.isEmpty()) {
                appendTerminalEntry(TerminalEntryType.SYSTEM, "No active user variables. Assign variables like `x = 42`.\n")
            } else {
                val formatted = userVars.entries.joinToString("\n") { (k, v) ->
                    " • $k: ${v.typeName()} = ${v.toDisplayString()}"
                }
                appendTerminalEntry(TerminalEntryType.SYSTEM, "Active Variables:\n$formatted\n")
            }
            return
        }

        if (trimmed == "credits" || trimmed == "dev" || trimmed == "author" || trimmed == "diwas") {
            appendTerminalEntry(
                TerminalEntryType.SYSTEM,
                "╔═══════════════════════════════════════════════════════╗\n" +
                "║               NEPALILANG (.np) CREATOR                ║\n" +
                "║                                                       ║\n" +
                "║  Lead Architect: Diwas Khatri                         ║\n" +
                "║  Email:          diwaskhatri935@gmail.com             ║\n" +
                "║  Version:        2.4.0 High-Density Edition           ║\n" +
                "║  Ecosystem:      Compiler, Lexer, REPL, Web Automation║\n" +
                "║                  SQLite Virtual DB, NPPM Packages     ║\n" +
                "╚═══════════════════════════════════════════════════════╝\n"
            )
            return
        }

        viewModelScope.launch {
            try {
                if (sharedInterpreter == null) {
                    sharedInterpreter = NepaliInterpreter(getApplication(), this@NepaliCodeViewModel, "repl.np")
                }
                val interpreter = sharedInterpreter!!

                // Wrap expression in print if not a statement
                val codeToRun = if (!trimmed.startsWith("kaam") && !trimmed.startsWith("def") &&
                    !trimmed.startsWith("yedi") && !trimmed.startsWith("if") &&
                    !trimmed.startsWith("ko_lagi") && !trimmed.startsWith("for") &&
                    !trimmed.startsWith("import") && !trimmed.startsWith("lyau") &&
                    !trimmed.contains("=") && !trimmed.startsWith("print") && !trimmed.startsWith("dekha")
                ) {
                    "print($trimmed)"
                } else {
                    trimmed
                }

                val tokens = NepaliLexer(codeToRun).tokenize()
                val ast = NepaliParser(tokens).parse()
                interpreter.execute(ast)
            } catch (e: Exception) {
                appendTerminalEntry(TerminalEntryType.STDERR, e.message ?: "Error in REPL")
            }
        }
    }

    fun submitInput(response: String) {
        appendTerminalEntry(TerminalEntryType.STDOUT, response)
        _uiState.update { it.copy(isWaitingForInput = false, inputPrompt = "") }
        inputContinuation?.resume(response)
        inputContinuation = null
    }

    fun formatCode() {
        val current = _uiState.value.activeCode
        val formatted = NepaliFormatter.format(current)
        onCodeChanged(formatted, _uiState.value.cursorPosition)
        appendTerminalEntry(TerminalEntryType.SYSTEM, "✨ [Formatted: standardized 4-space indentations and operator spacing]\n")
    }

    fun convertPythonToNepali() {
        val current = _uiState.value.activeCode
        val converted = NepaliConverter.pythonToNepali(current)
        onCodeChanged(converted, _uiState.value.cursorPosition)
        appendTerminalEntry(TerminalEntryType.SYSTEM, "🔄 [Converted Python syntax -> Native NepaliCode]\n")
    }

    fun convertNepaliToPython() {
        val current = _uiState.value.activeCode
        val converted = NepaliConverter.nepaliToPython(current)
        onCodeChanged(converted, _uiState.value.cursorPosition)
        appendTerminalEntry(TerminalEntryType.SYSTEM, "🔄 [Converted NepaliCode syntax -> Python/English]\n")
    }

    fun openCommandPalette() {
        _uiState.update { it.copy(showCommandPalette = true, commandPaletteQuery = "") }
    }

    fun closeCommandPalette() {
        _uiState.update { it.copy(showCommandPalette = false) }
    }

    fun updateCommandQuery(query: String) {
        _uiState.update { it.copy(commandPaletteQuery = query) }
    }

    fun openAiAssistant(mode: String) {
        val code = _uiState.value.activeCode
        val response = when (mode) {
            "explain" -> NepaliAiAssistant.explainCode(code)
            "improve" -> NepaliAiAssistant.improveCode(code)
            "tests" -> NepaliAiAssistant.generateTests(code)
            "fix" -> NepaliAiAssistant.fixErrors(code, _uiState.value.lintIssues)
            else -> NepaliAiAssistant.explainCode(code)
        }
        _uiState.update {
            it.copy(
                showAiAssistantDialog = true,
                aiAssistantResponse = response,
                showCommandPalette = false
            )
        }
    }

    fun closeAiAssistant() {
        _uiState.update { it.copy(showAiAssistantDialog = false, aiAssistantResponse = null) }
    }

    fun applyAiSuggestedCode() {
        val response = _uiState.value.aiAssistantResponse ?: return
        val newCode = response.suggestedCode ?: return
        onCodeChanged(newCode, 0)
        closeAiAssistant()
        appendTerminalEntry(TerminalEntryType.SYSTEM, "🤖 [Applied AI Assistant optimization to active file]\n")
    }

    fun openQuickExamples() {
        _uiState.update { it.copy(showQuickExamplesSheet = true, showCommandPalette = false) }
    }

    fun closeQuickExamples() {
        _uiState.update { it.copy(showQuickExamplesSheet = false) }
    }

    fun checkLint() {
        val current = _uiState.value.activeCode
        val issues = NepaliLinter.lint(current)
        _uiState.update { it.copy(lintIssues = issues) }
        if (issues.isEmpty()) {
            appendTerminalEntry(TerminalEntryType.SYSTEM, "✓ nepali check: 0 errors or warnings found in ${_uiState.value.activeFileName}\n")
        } else {
            appendTerminalEntry(
                TerminalEntryType.STDERR,
                "⚠ nepali check: Found ${issues.size} issues in ${_uiState.value.activeFileName}:\n" +
                    issues.joinToString("\n") { "Line ${it.line}: ${it.message} (${it.suggestion})" } + "\n"
            )
        }
    }

    fun switchFile(fileName: String) {
        val file = _uiState.value.files.firstOrNull { it.name == fileName } ?: return
        _uiState.update {
            it.copy(
                activeFileName = fileName,
                activeCode = file.content,
                suggestions = NepaliCompletionEngine.getSuggestions(file.content, 0),
                currentTab = IdeTab.EDITOR
            )
        }
    }

    fun openSidebar() {
        _uiState.update { it.copy(isSidebarOpen = true) }
    }

    fun closeSidebar() {
        _uiState.update { it.copy(isSidebarOpen = false) }
    }

    fun toggleSidebar() {
        _uiState.update { it.copy(isSidebarOpen = !it.isSidebarOpen) }
    }

    fun updateSidebarSearch(query: String) {
        _uiState.update { it.copy(sidebarSearchQuery = query) }
    }

    fun promptRenameFile(fileName: String?) {
        _uiState.update { it.copy(fileToRename = fileName) }
    }

    fun promptDeleteFile(fileName: String?) {
        _uiState.update { it.copy(fileToDelete = fileName) }
    }

    fun saveCurrentFile() {
        val fileName = _uiState.value.activeFileName
        val content = _uiState.value.activeCode
        projectManager.saveFile(fileName, content)
        val files = projectManager.loadFiles()
        val lineCount = content.lines().size
        val byteCount = content.toByteArray().size
        val message = "Saved $fileName ($lineCount lines, $byteCount bytes)"
        _uiState.update {
            it.copy(
                files = files,
                saveNotification = message
            )
        }
        appendTerminalEntry(TerminalEntryType.SYSTEM, "💾 [File Explorer] $message\n")
    }

    fun saveFile(fileName: String, content: String) {
        projectManager.saveFile(fileName, content)
        val files = projectManager.loadFiles()
        val message = "Saved $fileName"
        _uiState.update {
            it.copy(
                files = files,
                saveNotification = message
            )
        }
        appendTerminalEntry(TerminalEntryType.SYSTEM, "💾 [File Explorer] $message\n")
    }

    fun dismissSaveNotification() {
        _uiState.update { it.copy(saveNotification = null) }
    }

    fun renameFile(oldName: String, newName: String): Boolean {
        val trimmed = newName.trim()
        if (trimmed.isEmpty()) return false
        val finalNewName = if (!trimmed.contains(".")) "$trimmed.np" else trimmed

        if (finalNewName == oldName) {
            promptRenameFile(null)
            return true
        }

        val success = projectManager.renameFile(oldName, finalNewName)
        if (success) {
            val files = projectManager.loadFiles()
            val wasActive = _uiState.value.activeFileName == oldName
            _uiState.update {
                it.copy(
                    files = files,
                    activeFileName = if (wasActive) finalNewName else it.activeFileName,
                    fileToRename = null,
                    saveNotification = "Renamed $oldName to $finalNewName"
                )
            }
            appendTerminalEntry(TerminalEntryType.SYSTEM, "✏️ [File Explorer] Renamed $oldName ➔ $finalNewName\n")
            return true
        } else {
            appendTerminalEntry(TerminalEntryType.STDERR, "⚠ [File Explorer] Cannot rename: file with name '$finalNewName' may already exist.\n")
            return false
        }
    }

    fun createNewFile(fileName: String, content: String = "") {
        val trimmed = fileName.trim()
        val baseContent = if (content.isNotBlank()) content else {
            """# 🇳🇵 NepaliLang (.np) Script
# Created: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date())}

kaam main():
    dekha("Namaste from $trimmed!")
    firta 0

main()
"""
        }
        val nameWithExt = if (!trimmed.contains(".")) "$trimmed.np" else trimmed
        projectManager.saveFile(nameWithExt, baseContent)
        val files = projectManager.loadFiles()
        _uiState.update {
            it.copy(
                files = files,
                activeFileName = nameWithExt,
                activeCode = baseContent,
                currentTab = IdeTab.EDITOR,
                isSidebarOpen = false,
                saveNotification = "Created and opened $nameWithExt"
            )
        }
        appendTerminalEntry(TerminalEntryType.SYSTEM, "📄 [File Explorer] Created new file $nameWithExt\n")
    }

    fun deleteFile(fileName: String) {
        if (_uiState.value.files.size <= 1) {
            appendTerminalEntry(TerminalEntryType.STDERR, "⚠ [File Explorer] Cannot delete the only remaining file in the project.\n")
            _uiState.update { it.copy(fileToDelete = null) }
            return
        }
        projectManager.deleteFile(fileName)
        val files = projectManager.loadFiles()
        val nextFile = if (_uiState.value.activeFileName == fileName) {
            files.firstOrNull { it.name != fileName } ?: files.first()
        } else null

        _uiState.update {
            it.copy(
                files = files,
                activeFileName = nextFile?.name ?: it.activeFileName,
                activeCode = nextFile?.content ?: it.activeCode,
                fileToDelete = null,
                saveNotification = "Deleted $fileName"
            )
        }
        appendTerminalEntry(TerminalEntryType.SYSTEM, "🗑️ [File Explorer] Deleted $fileName\n")
    }

    fun clearTerminal() {
        _uiState.update { it.copy(terminalEntries = emptyList()) }
    }

    private fun appendTerminalEntry(type: TerminalEntryType, text: String) {
        _uiState.update {
            it.copy(terminalEntries = it.terminalEntries + TerminalEntry(type, text))
        }
    }

    // NepaliIdeCallbacks Implementation
    override fun onPrint(text: String) {
        appendTerminalEntry(TerminalEntryType.STDOUT, text)
    }

    override fun onLog(level: String, text: String) {
        val entryType = when (level) {
            "ERROR" -> TerminalEntryType.STDERR
            else -> TerminalEntryType.SYSTEM
        }
        appendTerminalEntry(entryType, "[$level] $text")
    }

    override fun onTerminalClear() {
        clearTerminal()
    }

    override suspend fun onInput(prompt: String): String = suspendCoroutine { cont ->
        inputContinuation = cont
        _uiState.update {
            it.copy(
                isWaitingForInput = true,
                inputPrompt = prompt,
                currentTab = IdeTab.TERMINAL
            )
        }
        appendTerminalEntry(TerminalEntryType.INPUT_PROMPT, prompt)
    }

    override fun onWebOpen(url: String) {
        _uiState.update {
            it.copy(webPreviewUrl = url)
        }
    }

    override fun onAutomationAction(action: String, details: String) {
        _uiState.update {
            val updated = it.automationLogs + AutomationLogItem(action, details)
            it.copy(automationLogs = updated.takeLast(50))
        }
    }
}
