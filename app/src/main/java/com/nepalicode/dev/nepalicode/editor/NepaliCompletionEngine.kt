package com.nepalicode.dev.nepalicode.editor

data class CodeSuggestion(
    val label: String,
    val insertText: String,
    val detail: String,
    val category: SuggestionCategory,
    val iconText: String = "λ"
)

enum class SuggestionCategory {
    KEYWORD_NEPALI,
    KEYWORD_ENGLISH,
    BUILTIN,
    STDLIB_MODULE,
    METHOD,
    SNIPPET
}

object NepaliCompletionEngine {

    private val snippets = listOf(
        CodeSuggestion(
            label = "kaam (define function)",
            insertText = "kaam function_name(a, b):\n    firta a + b\n",
            detail = "Nepali function with return value",
            category = SuggestionCategory.SNIPPET,
            iconText = "fn"
        ),
        CodeSuggestion(
            label = "def (define function)",
            insertText = "def add(a, b):\n    return a + b\n",
            detail = "Standard function definition",
            category = SuggestionCategory.SNIPPET,
            iconText = "fn"
        ),
        CodeSuggestion(
            label = "yedi ... natra (if/else)",
            insertText = "yedi x > 0:\n    dekha(\"Positive\")\nnatra:\n    dekha(\"Zero or Negative\")\n",
            detail = "Nepali conditional branch",
            category = SuggestionCategory.SNIPPET,
            iconText = "if"
        ),
        CodeSuggestion(
            label = "yedi ... natabhaye ... natra (if/elif/else)",
            insertText = "yedi marks >= 80:\n    dekha(\"Distinction\")\nnatabhaye marks >= 60:\n    dekha(\"First Division\")\nnatra:\n    dekha(\"Pass\")\n",
            detail = "Nepali multi-condition branch",
            category = SuggestionCategory.SNIPPET,
            iconText = "if"
        ),
        CodeSuggestion(
            label = "ko_lagi loop (for)",
            insertText = "ko_lagi i ma range(1, 11):\n    dekha(\"Ganti:\", i)\n",
            detail = "Nepali loop 1 to 10",
            category = SuggestionCategory.SNIPPET,
            iconText = "for"
        ),
        CodeSuggestion(
            label = "jabasamma loop (while)",
            insertText = "count = 0\njabasamma count < 5:\n    dekha(\"Paila:\", count)\n    count = count + 1\n",
            detail = "Nepali while loop",
            category = SuggestionCategory.SNIPPET,
            iconText = "whl"
        ),
        CodeSuggestion(
            label = "anurodh GET request",
            insertText = "lyau anurodh\nres = anurodh.get(\"https://api.github.com\")\ndekha(\"Status:\", res.status)\ndekha(\"Body:\", res.text)\n",
            detail = "HTTP GET request with anurodh",
            category = SuggestionCategory.SNIPPET,
            iconText = "api"
        ),
        CodeSuggestion(
            label = "anurodh POST JSON",
            insertText = "lyau anurodh\ndata = {\"name\": \"Diwas\", \"lang\": \"NepaliLang\"}\nres = anurodh.post(\"https://httpbin.org/post\", data=data)\ndekha(res.text)\n",
            detail = "HTTP POST JSON request with anurodh",
            category = SuggestionCategory.SNIPPET,
            iconText = "api"
        ),
        CodeSuggestion(
            label = "web browser open",
            insertText = "lyau web\nweb.khol(\"https://google.com\")\n",
            detail = "Open web page in browser preview",
            category = SuggestionCategory.SNIPPET,
            iconText = "web"
        ),
        CodeSuggestion(
            label = "web extraction (scraper)",
            insertText = "lyau web\npage = web.get(\"https://example.com\")\nheadings = page.extract(\"h1\")\nlinks = page.extract_links()\ndekha(\"Headings:\", headings)\ndekha(\"Links:\", links)\n",
            detail = "Scrape headings & links using CSS selectors",
            category = SuggestionCategory.SNIPPET,
            iconText = "web"
        ),
        CodeSuggestion(
            label = "regex pattern matching (khoj)",
            insertText = "lyau regex\npatro = \"Phone: +977-9812345678, Code: NP-2026\"\nmatch = regex.khoj(\"\\+977-\\d+\", patro)\ndekha(\"Found contact:\", match[\"match\"])\n",
            detail = "Regex search and pattern match",
            category = SuggestionCategory.SNIPPET,
            iconText = "re"
        ),
        CodeSuggestion(
            label = "browser automation",
            insertText = "lyau browser\nsite = browser.khol(\"https://example.com\")\nsite.click(\"#login\")\nsite.type(\"#username\", \"Diwas\")\nsite.wait(1)\nsite.close()\n",
            detail = "Automate browser click and typing",
            category = SuggestionCategory.SNIPPET,
            iconText = "bot"
        ),
        CodeSuggestion(
            label = "database SQLite",
            insertText = "lyau database\ndb = database.open(\"app.db\")\ndb.execute(\"CREATE TABLE IF NOT EXISTS users (id INTEGER, name TEXT)\")\ndb.execute(\"INSERT INTO users VALUES (?, ?)\", [1, \"Diwas Khatri\"])\nrows = db.query(\"SELECT * FROM users\")\ndekha(\"Records:\", rows)\ndb.close()\n",
            detail = "SQLite database operations",
            category = SuggestionCategory.SNIPPET,
            iconText = "db"
        ),
        CodeSuggestion(
            label = "automation mouse & keyboard",
            insertText = "lyau automation\nautomation.mouse.move(500, 300)\nautomation.mouse.click()\nautomation.keyboard.write(\"Namaste Nepal 🇳🇵\")\nautomation.wait(1)\n",
            detail = "Desktop & device automation",
            category = SuggestionCategory.SNIPPET,
            iconText = "act"
        ),
        CodeSuggestion(
            label = "koshish ... samau (try/except)",
            insertText = "koshish:\n    x = 10 / 2\n    dekha(\"Phal:\", x)\nsamau truti:\n    dekha(\"Truti aayo:\", truti)\nantya:\n    dekha(\"Samapta bhayo.\")\n",
            detail = "Nepali exception handling block",
            category = SuggestionCategory.SNIPPET,
            iconText = "try"
        ),
        CodeSuggestion(
            label = "file write & read",
            insertText = "lyau file\nfile.write(\"data.txt\", \"Namaste Nepal!\")\npatho = file.read(\"data.txt\")\ndekha(\"Padiyo:\", patho)\n",
            detail = "Virtual file system I/O",
            category = SuggestionCategory.SNIPPET,
            iconText = "io"
        ),
        CodeSuggestion(
            label = "ganit calculations",
            insertText = "lyau ganit\njod_phal = ganit.jod(15, 27)\nsqrt_phal = ganit.sqrt(144)\npow_phal = ganit.power(2, 8)\ndekha(\"Jod:\", jod_phal, \"Sqrt:\", sqrt_phal, \"Power:\", pow_phal)\n",
            detail = "Math calculations with ganit",
            category = SuggestionCategory.SNIPPET,
            iconText = "math"
        ),
        CodeSuggestion(
            label = "samaya date & time",
            insertText = "lyau samaya\naaja = samaya.today()\nsamaya_ahile = samaya.now()\ndekha(\"Aaja:\", aaja, \"Samaya:\", samaya_ahile)\n",
            detail = "Date and time operations",
            category = SuggestionCategory.SNIPPET,
            iconText = "clk"
        ),
        CodeSuggestion(
            label = "json parse & stringify",
            insertText = "lyau json\nraw = '{\"name\": \"Diwas\", \"role\": \"Creator\"}'\nparsed = json.parse(raw)\ndekha(\"Parsed Name:\", parsed[\"name\"])\nencoded = json.stringify(parsed)\ndekha(\"Encoded:\", encoded)\n",
            detail = "JSON parsing and serialization",
            category = SuggestionCategory.SNIPPET,
            iconText = "json"
        ),
        CodeSuggestion(
            label = "developer credits snippet",
            insertText = "# NepaliLang (.np) by Diwas Khatri\ndekha(\"🇳🇵 NepaliLang v2.4.0 High-Density IDE\")\ndekha(\"Created by Diwas Khatri (diwaskhatri935@gmail.com)\")\n",
            detail = "Display developer credits",
            category = SuggestionCategory.SNIPPET,
            iconText = "dev"
        )
    )

    private val moduleMethods = mapOf(
        "anurodh" to listOf(
            CodeSuggestion("get(url, headers=None)", "get(\"https://example.com\")", "HTTP GET request", SuggestionCategory.METHOD, "GET"),
            CodeSuggestion("post(url, data=None)", "post(\"https://example.com/api\", data={\"key\": \"val\"})", "HTTP POST request", SuggestionCategory.METHOD, "POST"),
            CodeSuggestion("get_json(url)", "get_json(\"https://api.example.com/data\")", "HTTP GET JSON directly", SuggestionCategory.METHOD, "JSON"),
            CodeSuggestion("put(url, data=None)", "put(\"https://example.com/api\", data=data)", "HTTP PUT request", SuggestionCategory.METHOD, "PUT"),
            CodeSuggestion("delete(url)", "delete(\"https://example.com/api/item/1\")", "HTTP DELETE request", SuggestionCategory.METHOD, "DEL")
        ),
        "web" to listOf(
            CodeSuggestion("khol(url)", "khol(\"https://google.com\")", "Open URL in preview", SuggestionCategory.METHOD, "web"),
            CodeSuggestion("open(url)", "open(\"https://google.com\")", "Open URL alias", SuggestionCategory.METHOD, "web"),
            CodeSuggestion("get(url)", "get(\"https://example.com\")", "Scrape web page", SuggestionCategory.METHOD, "get"),
            CodeSuggestion("browser(url)", "browser(\"https://example.com\")", "Create browser automation", SuggestionCategory.METHOD, "bot")
        ),
        "browser" to listOf(
            CodeSuggestion("khol(url)", "khol(\"https://example.com\")", "Open automated browser", SuggestionCategory.METHOD, "bot"),
            CodeSuggestion("click(selector)", "click(\"#button\")", "Click HTML element", SuggestionCategory.METHOD, "clk"),
            CodeSuggestion("type(selector, text)", "type(\"#input\", \"text\")", "Type into input field", SuggestionCategory.METHOD, "typ"),
            CodeSuggestion("wait(seconds)", "wait(2)", "Wait for page / action", SuggestionCategory.METHOD, "wt"),
            CodeSuggestion("title()", "title()", "Get webpage title", SuggestionCategory.METHOD, "tit"),
            CodeSuggestion("close()", "close()", "Close browser session", SuggestionCategory.METHOD, "cls")
        ),
        "automation" to listOf(
            CodeSuggestion("mouse.move(x, y)", "mouse.move(500, 300)", "Move cursor to coordinate", SuggestionCategory.METHOD, "ms"),
            CodeSuggestion("mouse.click()", "mouse.click()", "Click mouse button", SuggestionCategory.METHOD, "ms"),
            CodeSuggestion("keyboard.write(text)", "keyboard.write(\"Hello\")", "Simulate typing", SuggestionCategory.METHOD, "kb"),
            CodeSuggestion("keyboard.press(key)", "keyboard.press(\"enter\")", "Press special key", SuggestionCategory.METHOD, "kb"),
            CodeSuggestion("wait(seconds)", "wait(2)", "Pause automation flow", SuggestionCategory.METHOD, "wt"),
            CodeSuggestion("screen(filename)", "screen(\"screen.png\")", "Capture screenshot", SuggestionCategory.METHOD, "scr"),
            CodeSuggestion("clipboard.set(text)", "clipboard.set(\"Namaste\")", "Set clipboard text", SuggestionCategory.METHOD, "cb"),
            CodeSuggestion("clipboard.get()", "clipboard.get()", "Get clipboard text", SuggestionCategory.METHOD, "cb")
        ),
        "database" to listOf(
            CodeSuggestion("open(filename)", "open(\"app.db\")", "Open SQLite DB", SuggestionCategory.METHOD, "db"),
            CodeSuggestion("execute(sql, params)", "execute(\"INSERT INTO table VALUES (?)\", [value])", "Execute SQL statement", SuggestionCategory.METHOD, "sql"),
            CodeSuggestion("query(sql)", "query(\"SELECT * FROM table\")", "Query SQLite rows", SuggestionCategory.METHOD, "sql"),
            CodeSuggestion("close()", "close()", "Close SQLite connection", SuggestionCategory.METHOD, "db")
        ),
        "file" to listOf(
            CodeSuggestion("write(path, text)", "write(\"data.txt\", \"content\")", "Write text to file", SuggestionCategory.METHOD, "io"),
            CodeSuggestion("read(path)", "read(\"data.txt\")", "Read file contents", SuggestionCategory.METHOD, "io"),
            CodeSuggestion("exists(path)", "exists(\"data.txt\")", "Check file existence", SuggestionCategory.METHOD, "io"),
            CodeSuggestion("delete(path)", "delete(\"data.txt\")", "Delete file", SuggestionCategory.METHOD, "io"),
            CodeSuggestion("copy(src, dst)", "copy(\"src.txt\", \"dst.txt\")", "Copy file", SuggestionCategory.METHOD, "io"),
            CodeSuggestion("move(src, dst)", "move(\"old.txt\", \"new.txt\")", "Move file", SuggestionCategory.METHOD, "io")
        ),
        "folder" to listOf(
            CodeSuggestion("create(name)", "create(\"projects\")", "Create directory", SuggestionCategory.METHOD, "dir"),
            CodeSuggestion("exists(name)", "exists(\"projects\")", "Check directory", SuggestionCategory.METHOD, "dir"),
            CodeSuggestion("list(name)", "list(\".\")", "List files in directory", SuggestionCategory.METHOD, "dir"),
            CodeSuggestion("delete(name)", "delete(\"projects\")", "Delete directory", SuggestionCategory.METHOD, "dir")
        ),
        "ganit" to listOf(
            CodeSuggestion("jod(a, b)", "jod(3, 4)", "Addition (jod)", SuggestionCategory.METHOD, "+"),
            CodeSuggestion("ghata(a, b)", "ghata(10, 4)", "Subtraction (ghata)", SuggestionCategory.METHOD, "-"),
            CodeSuggestion("guna(a, b)", "guna(5, 6)", "Multiplication (guna)", SuggestionCategory.METHOD, "*"),
            CodeSuggestion("bhag(a, b)", "bhag(20, 5)", "Division (bhag)", SuggestionCategory.METHOD, "/"),
            CodeSuggestion("sqrt(x)", "sqrt(25)", "Square root", SuggestionCategory.METHOD, "√"),
            CodeSuggestion("power(a, b)", "power(2, 8)", "Exponentiation", SuggestionCategory.METHOD, "^"),
            CodeSuggestion("abs(x)", "abs(-10)", "Absolute value", SuggestionCategory.METHOD, "|x|"),
            CodeSuggestion("round(x)", "round(4.7)", "Round number", SuggestionCategory.METHOD, "~")
        ),
        "samaya" to listOf(
            CodeSuggestion("now()", "now()", "Current timestamp (ms)", SuggestionCategory.METHOD, "t"),
            CodeSuggestion("today()", "today()", "Current date YYYY-MM-DD", SuggestionCategory.METHOD, "d"),
            CodeSuggestion("sleep(seconds)", "sleep(1)", "Delay execution", SuggestionCategory.METHOD, "z"),
            CodeSuggestion("format(time, pattern)", "format(samaya.now(), \"YYYY-MM-DD\")", "Format timestamp", SuggestionCategory.METHOD, "fmt")
        ),
        "json" to listOf(
            CodeSuggestion("parse(text)", "parse(text)", "Parse JSON string", SuggestionCategory.METHOD, "js"),
            CodeSuggestion("stringify(obj)", "stringify(data)", "Encode JSON string", SuggestionCategory.METHOD, "js"),
            CodeSuggestion("read(path)", "read(\"config.json\")", "Read JSON file", SuggestionCategory.METHOD, "js"),
            CodeSuggestion("write(path, obj)", "write(\"config.json\", data)", "Write JSON file", SuggestionCategory.METHOD, "js")
        ),
        "random" to listOf(
            CodeSuggestion("number(min, max)", "number(1, 100)", "Random integer in range", SuggestionCategory.METHOD, "rnd"),
            CodeSuggestion("choice(list)", "choice([\"a\", \"b\", \"c\"])", "Pick random item", SuggestionCategory.METHOD, "rnd"),
            CodeSuggestion("decimal()", "decimal()", "Random float 0.0..1.0", SuggestionCategory.METHOD, "rnd")
        )
    )

    private val commonKeywords = listOf(
        // Nepali Keywords
        CodeSuggestion("kaam", "kaam ", "Define function (kaam)", SuggestionCategory.KEYWORD_NEPALI, "fn"),
        CodeSuggestion("firta", "firta ", "Return statement (firta)", SuggestionCategory.KEYWORD_NEPALI, "ret"),
        CodeSuggestion("yedi", "yedi ", "If condition (yedi)", SuggestionCategory.KEYWORD_NEPALI, "if"),
        CodeSuggestion("natra:", "natra:\n    ", "Else branch (natra)", SuggestionCategory.KEYWORD_NEPALI, "else"),
        CodeSuggestion("natabhaye", "natabhaye ", "Elif branch (natabhaye)", SuggestionCategory.KEYWORD_NEPALI, "elif"),
        CodeSuggestion("ko_lagi", "ko_lagi ", "For loop (ko_lagi)", SuggestionCategory.KEYWORD_NEPALI, "for"),
        CodeSuggestion("jabasamma", "jabasamma ", "While loop (jabasamma)", SuggestionCategory.KEYWORD_NEPALI, "whl"),
        CodeSuggestion("lyau", "lyau ", "Import module (lyau)", SuggestionCategory.KEYWORD_NEPALI, "imp"),
        CodeSuggestion("bata", "bata ", "From module (bata)", SuggestionCategory.KEYWORD_NEPALI, "from"),
        CodeSuggestion("koshish:", "koshish:\n    ", "Try block (koshish)", SuggestionCategory.KEYWORD_NEPALI, "try"),
        CodeSuggestion("samau:", "samau:\n    ", "Except/catch block (samau)", SuggestionCategory.KEYWORD_NEPALI, "exc"),
        CodeSuggestion("antya:", "antya:\n    ", "Finally block (antya)", SuggestionCategory.KEYWORD_NEPALI, "fin"),
        CodeSuggestion("sacho", "sacho", "True boolean value", SuggestionCategory.KEYWORD_NEPALI, "true"),
        CodeSuggestion("jhut", "jhut", "False boolean value", SuggestionCategory.KEYWORD_NEPALI, "fals"),
        CodeSuggestion("khali", "khali", "None / null literal", SuggestionCategory.KEYWORD_NEPALI, "null"),
        CodeSuggestion("kakshya", "kakshya ", "Class definition (kakshya)", SuggestionCategory.KEYWORD_NEPALI, "cls"),
        CodeSuggestion("uthau", "uthau ", "Raise exception (uthau)", SuggestionCategory.KEYWORD_NEPALI, "err"),
        CodeSuggestion("bhitra", "bhitra ", "In operator (bhitra)", SuggestionCategory.KEYWORD_NEPALI, "in"),
        CodeSuggestion("ra", " ra ", "Logical AND (ra)", SuggestionCategory.KEYWORD_NEPALI, "and"),
        CodeSuggestion("wa", " wa ", "Logical OR (wa)", SuggestionCategory.KEYWORD_NEPALI, "or"),
        CodeSuggestion("hoina", "hoina ", "Logical NOT (hoina)", SuggestionCategory.KEYWORD_NEPALI, "not"),

        // English Keywords
        CodeSuggestion("def", "def ", "Define function (def)", SuggestionCategory.KEYWORD_ENGLISH, "fn"),
        CodeSuggestion("return", "return ", "Return from function", SuggestionCategory.KEYWORD_ENGLISH, "ret"),
        CodeSuggestion("if", "if ", "If condition", SuggestionCategory.KEYWORD_ENGLISH, "if"),
        CodeSuggestion("else:", "else:\n    ", "Else branch", SuggestionCategory.KEYWORD_ENGLISH, "else"),
        CodeSuggestion("elif", "elif ", "Else if condition", SuggestionCategory.KEYWORD_ENGLISH, "elif"),
        CodeSuggestion("for", "for ", "For loop", SuggestionCategory.KEYWORD_ENGLISH, "for"),
        CodeSuggestion("while", "while ", "While loop", SuggestionCategory.KEYWORD_ENGLISH, "whl"),
        CodeSuggestion("import", "import ", "Import module", SuggestionCategory.KEYWORD_ENGLISH, "imp"),
        CodeSuggestion("from", "from ", "From module import", SuggestionCategory.KEYWORD_ENGLISH, "from"),
        CodeSuggestion("try:", "try:\n    ", "Try exception block", SuggestionCategory.KEYWORD_ENGLISH, "try"),
        CodeSuggestion("except:", "except:\n    ", "Except error block", SuggestionCategory.KEYWORD_ENGLISH, "exc"),
        CodeSuggestion("finally:", "finally:\n    ", "Finally block", SuggestionCategory.KEYWORD_ENGLISH, "fin"),
        CodeSuggestion("True", "True", "Boolean true", SuggestionCategory.KEYWORD_ENGLISH, "true"),
        CodeSuggestion("False", "False", "Boolean false", SuggestionCategory.KEYWORD_ENGLISH, "fals"),
        CodeSuggestion("None", "None", "Null / none object", SuggestionCategory.KEYWORD_ENGLISH, "none"),
        CodeSuggestion("break", "break", "Break out of loop", SuggestionCategory.KEYWORD_ENGLISH, "brk"),
        CodeSuggestion("continue", "continue", "Continue next iteration", SuggestionCategory.KEYWORD_ENGLISH, "cnt"),
        CodeSuggestion("pass", "pass", "No-op pass statement", SuggestionCategory.KEYWORD_ENGLISH, "pass"),

        // Built-ins
        CodeSuggestion("print()", "print()", "Print output to terminal", SuggestionCategory.BUILTIN, "out"),
        CodeSuggestion("dekha()", "dekha()", "Display output (Nepali alias for print)", SuggestionCategory.BUILTIN, "out"),
        CodeSuggestion("input()", "input(\"Prompt: \")", "Read terminal input", SuggestionCategory.BUILTIN, "in"),
        CodeSuggestion("leu()", "leu(\"Prompt: \")", "Read terminal input (Nepali alias)", SuggestionCategory.BUILTIN, "in"),
        CodeSuggestion("len()", "len()", "Length of collection / string", SuggestionCategory.BUILTIN, "len"),
        CodeSuggestion("lambai()", "lambai()", "Length (Nepali alias)", SuggestionCategory.BUILTIN, "len"),
        CodeSuggestion("range()", "range(10)", "Generate number sequence", SuggestionCategory.BUILTIN, "seq"),
        CodeSuggestion("yo()", "yo()", "Create instance / self constructor", SuggestionCategory.BUILTIN, "yo"),
        CodeSuggestion("str()", "str()", "Convert to string", SuggestionCategory.BUILTIN, "str"),
        CodeSuggestion("number()", "number()", "Convert to integer/number", SuggestionCategory.BUILTIN, "num"),
        CodeSuggestion("sum()", "sum()", "Sum of elements", SuggestionCategory.BUILTIN, "sum"),
        CodeSuggestion("min()", "min()", "Minimum element", SuggestionCategory.BUILTIN, "min"),
        CodeSuggestion("max()", "max()", "Maximum element", SuggestionCategory.BUILTIN, "max"),
        CodeSuggestion("abs()", "abs()", "Absolute value", SuggestionCategory.BUILTIN, "abs"),
        CodeSuggestion("round()", "round()", "Round floating point", SuggestionCategory.BUILTIN, "rnd"),

        // Stdlib Modules
        CodeSuggestion("anurodh", "anurodh", "HTTP client module", SuggestionCategory.STDLIB_MODULE, "mod"),
        CodeSuggestion("web", "web", "Web preview and scraping module", SuggestionCategory.STDLIB_MODULE, "mod"),
        CodeSuggestion("browser", "browser", "Browser automation module", SuggestionCategory.STDLIB_MODULE, "mod"),
        CodeSuggestion("automation", "automation", "Desktop & device automation", SuggestionCategory.STDLIB_MODULE, "mod"),
        CodeSuggestion("database", "database", "SQLite virtual database", SuggestionCategory.STDLIB_MODULE, "mod"),
        CodeSuggestion("file", "file", "Virtual file system I/O", SuggestionCategory.STDLIB_MODULE, "mod"),
        CodeSuggestion("folder", "folder", "Directory management module", SuggestionCategory.STDLIB_MODULE, "mod"),
        CodeSuggestion("ganit", "ganit", "Math operations module", SuggestionCategory.STDLIB_MODULE, "mod"),
        CodeSuggestion("samaya", "samaya", "Date and time utilities", SuggestionCategory.STDLIB_MODULE, "mod"),
        CodeSuggestion("json", "json", "JSON encoding and decoding", SuggestionCategory.STDLIB_MODULE, "mod"),
        CodeSuggestion("random", "random", "Pseudorandom generator", SuggestionCategory.STDLIB_MODULE, "mod"),
        CodeSuggestion("system", "system", "System information & runtime", SuggestionCategory.STDLIB_MODULE, "mod"),
        CodeSuggestion("terminal", "terminal", "Terminal control & clear", SuggestionCategory.STDLIB_MODULE, "mod"),
        CodeSuggestion("log", "log", "IDE debug logging module", SuggestionCategory.STDLIB_MODULE, "mod")
    )

    fun getSuggestions(code: String, cursorPosition: Int): List<CodeSuggestion> {
        val safePos = cursorPosition.coerceIn(0, code.length)
        val textBefore = code.substring(0, safePos)

        // Check for member dot completion: e.g. "anurodh." or "web."
        val dotMatch = Regex("""([\p{L}_][\p{L}\p{N}_]*)\.\s*([\p{L}_]*)$""").find(textBefore)
        if (dotMatch != null) {
            val moduleName = dotMatch.groups[1]?.value ?: ""
            val prefix = dotMatch.groups[2]?.value ?: ""
            val methods = moduleMethods[moduleName]
            if (methods != null) {
                return if (prefix.isEmpty()) {
                    methods
                } else {
                    methods.filter { it.label.startsWith(prefix, ignoreCase = true) }
                }
            }
        }

        // Get word prefix at cursor
        val wordMatch = Regex("""([\p{L}_][\p{L}\p{N}_]*)$""").find(textBefore)
        val prefix = wordMatch?.value.orEmpty()

        if (prefix.isEmpty()) {
            return snippets.take(4) + commonKeywords.take(4)
        }

        val matchedKeywords = commonKeywords.filter { it.label.startsWith(prefix, ignoreCase = true) }
        val matchedSnippets = snippets.filter { it.label.contains(prefix, ignoreCase = true) }

        val combined = matchedKeywords + matchedSnippets
        return combined.take(12)
    }
}
