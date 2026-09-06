package com.nepalicode.dev.nepalilang.core

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import android.util.Base64
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan
import kotlin.random.Random

interface NepaliIdeCallbacks {
    fun onPrint(text: String)
    fun onLog(level: String, text: String)
    fun onTerminalClear()
    suspend fun onInput(prompt: String): String
    fun onWebOpen(url: String)
    fun onAutomationAction(action: String, details: String)
}

class NepaliStdLib(
    private val context: Context,
    private val callbacks: NepaliIdeCallbacks
) {
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val workspaceDir: File by lazy {
        val dir = File(context.filesDir, "nepali_workspace")
        if (!dir.exists()) dir.mkdirs()
        dir
    }

    private var virtualClipboard = "Namaste Nepal"
    private val environmentVariables = mutableMapOf(
        "API_KEY" to "np_key_demo_935",
        "ENV" to "development",
        "APP_NAME" to "NepaliCode",
        "AUTHOR" to "Diwas Khatri"
    )

    fun registerStandardModules(env: NepaliEnvironment) {
        env.define("anurodh", createAnurodhModule())
        env.define("web", createWebModule())
        env.define("browser", createBrowserModule())
        env.define("automation", createAutomationModule())
        env.define("file", createFileModule())
        env.define("folder", createFolderModule())
        env.define("system", createSystemModule())
        env.define("ganit", createGanitModule())
        env.define("samaya", createSamayaModule())
        env.define("json", createJsonModule())
        env.define("random", createRandomModule())
        env.define("database", createDatabaseModule())
        env.define("terminal", createTerminalModule())
        env.define("log", createLogModule())
        env.define("env", createEnvModule())
        env.define("regex", createRegexModule())
        env.define("khoj", createRegexModule())
        env.define("csv", createCsvModule())
        env.define("path", createPathModule())
        env.define("command", createCommandModule())
        env.define("process", createProcessModule())
        env.define("suraksha", createSurakshaModule())
        env.define("hash", createSurakshaModule())
        env.define("email", createEmailModule())
        env.define("zip", createZipModule())
    }

    // --- anurodh (HTTP) ---
    private fun createAnurodhModule(): NepaliModule {
        val module = NepaliModule("anurodh")

        module.members["get"] = NepaliBuiltin("get") { args, _ ->
            val url = (args.getOrNull(0) as? NepaliString)?.value
                ?: throw IllegalArgumentException("anurodh.get expects url string")
            val headers = (args.getOrNull(1) as? NepaliMap)?.entries
            executeHttpRequest("GET", url, null, headers)
        }

        module.members["post"] = NepaliBuiltin("post") { args, _ ->
            val url = (args.getOrNull(0) as? NepaliString)?.value
                ?: throw IllegalArgumentException("anurodh.post expects url string")
            val data = args.getOrNull(1)
            executeHttpRequest("POST", url, data, null)
        }

        module.members["get_json"] = NepaliBuiltin("get_json") { args, _ ->
            val url = (args.getOrNull(0) as? NepaliString)?.value
                ?: throw IllegalArgumentException("anurodh.get_json expects url string")
            val resp = executeHttpRequest("GET", url, null, null)
            val jsonField = resp.fields["json"] ?: resp.fields["text"] ?: NepaliNull
            if (jsonField is NepaliFunction) {
                // Return parsed map
                resp.fields["data"] ?: NepaliString(resp.fields["text"]?.toDisplayString() ?: "")
            } else {
                jsonField
            }
        }

        return module
    }

    private suspend fun executeHttpRequest(
        method: String,
        url: String,
        bodyData: NepaliValue?,
        headersMap: Map<String, NepaliValue>?
    ): NepaliObject = withContext(Dispatchers.IO) {
        callbacks.onAutomationAction("HTTP Request", "$method $url")
        val reqBuilder = Request.Builder().url(url)

        headersMap?.forEach { (k, v) ->
            reqBuilder.addHeader(k, v.toDisplayString())
        }

        if (method == "POST") {
            val jsonStr = when (bodyData) {
                is NepaliMap -> nepaliMapToJsonObject(bodyData).toString()
                is NepaliString -> bodyData.value
                else -> "{}"
            }
            reqBuilder.post(jsonStr.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull()))
        }

        try {
            val response = httpClient.newCall(reqBuilder.build()).execute()
            val code = response.code
            val body = response.body?.string().orEmpty()
            val isOk = response.isSuccessful

            val respObj = NepaliObject("HttpResponse")
            respObj.fields["status"] = NepaliNumber(code.toDouble(), isInt = true)
            respObj.fields["text"] = NepaliString(body)
            respObj.fields["ok"] = NepaliBool(isOk)

            // Parse json if possible
            val parsedJson = parseJsonStringToNepaliValue(body)
            respObj.fields["data"] = parsedJson
            respObj.fields["json"] = NepaliBuiltin("json") { _, _ -> parsedJson }

            respObj
        } catch (e: Exception) {
            val respObj = NepaliObject("HttpResponse")
            respObj.fields["status"] = NepaliNumber(500.0, isInt = true)
            respObj.fields["text"] = NepaliString("Network error: ${e.message}")
            respObj.fields["ok"] = NepaliBool(false)
            respObj.fields["data"] = NepaliMap()
            respObj.fields["json"] = NepaliBuiltin("json") { _, _ -> NepaliMap() }
            respObj
        }
    }

    // --- web ---
    private fun createWebModule(): NepaliModule {
        val module = NepaliModule("web")

        val openFunc = NepaliBuiltin("khol") { args, _ ->
            val url = (args.getOrNull(0) as? NepaliString)?.value ?: "https://example.com"
            callbacks.onWebOpen(url)
            callbacks.onAutomationAction("Web Browser", "Opened URL: $url")
            NepaliString("Opened $url in browser preview")
        }

        module.members["khol"] = openFunc
        module.members["open"] = openFunc
        module.members["website_kholne"] = openFunc

        module.members["get"] = NepaliBuiltin("get") { args, _ ->
            val url = (args.getOrNull(0) as? NepaliString)?.value ?: "https://example.com"
            callbacks.onWebOpen(url)
            val resp = executeHttpRequest("GET", url, null, null)
            val html = resp.fields["text"]?.toDisplayString() ?: ""
            val titleMatch = Regex("<title>(.*?)</title>", RegexOption.IGNORE_CASE).find(html)
            val title = titleMatch?.groups?.get(1)?.value ?: "Example Domain"

            val pageObj = NepaliObject("WebPage")
            pageObj.fields["title_text"] = NepaliString(title)
            pageObj.fields["html"] = NepaliString(html)
            pageObj.fields["title"] = NepaliBuiltin("title") { _, _ -> NepaliString(title) }
            pageObj.fields["extract"] = NepaliBuiltin("extract") { exArgs, _ ->
                val sel = exArgs.getOrNull(0)?.toDisplayString() ?: "h1"
                val list = extractHtmlElements(html, sel)
                callbacks.onAutomationAction("Web Extract", "Selector '$sel' found ${list.size} matches")
                NepaliList(list.map { NepaliString(it) }.toMutableList())
            }
            pageObj.fields["extract_links"] = NepaliBuiltin("extract_links") { _, _ ->
                val links = extractHtmlLinks(html)
                callbacks.onAutomationAction("Web Links", "Extracted ${links.size} hyperlinks")
                NepaliList(links.toMutableList())
            }
            pageObj.fields["extract_text"] = NepaliBuiltin("extract_text") { _, _ ->
                NepaliString(cleanHtmlText(html))
            }
            pageObj.fields["extract_table"] = NepaliBuiltin("extract_table") { _, _ ->
                extractHtmlTable(html)
            }
            pageObj
        }

        module.members["extract"] = NepaliBuiltin("extract") { args, _ ->
            val target = args.getOrNull(0)?.toDisplayString() ?: ""
            val selector = args.getOrNull(1)?.toDisplayString() ?: "h1"
            val html = if (target.startsWith("http://") || target.startsWith("https://")) {
                val resp = executeHttpRequest("GET", target, null, null)
                resp.fields["text"]?.toDisplayString() ?: ""
            } else {
                target
            }
            val list = extractHtmlElements(html, selector)
            callbacks.onAutomationAction("Web Extract", "Selector '$selector' found ${list.size} items")
            NepaliList(list.map { NepaliString(it) }.toMutableList())
        }

        module.members["extract_links"] = NepaliBuiltin("extract_links") { args, _ ->
            val target = args.getOrNull(0)?.toDisplayString() ?: ""
            val html = if (target.startsWith("http://") || target.startsWith("https://")) {
                val resp = executeHttpRequest("GET", target, null, null)
                resp.fields["text"]?.toDisplayString() ?: ""
            } else {
                target
            }
            val links = extractHtmlLinks(html)
            callbacks.onAutomationAction("Extract Links", "Found ${links.size} hyperlinks")
            NepaliList(links.toMutableList())
        }

        module.members["extract_text"] = NepaliBuiltin("extract_text") { args, _ ->
            val target = args.getOrNull(0)?.toDisplayString() ?: ""
            val html = if (target.startsWith("http://") || target.startsWith("https://")) {
                val resp = executeHttpRequest("GET", target, null, null)
                resp.fields["text"]?.toDisplayString() ?: ""
            } else {
                target
            }
            NepaliString(cleanHtmlText(html))
        }

        module.members["extract_table"] = NepaliBuiltin("extract_table") { args, _ ->
            val target = args.getOrNull(0)?.toDisplayString() ?: ""
            val html = if (target.startsWith("http://") || target.startsWith("https://")) {
                val resp = executeHttpRequest("GET", target, null, null)
                resp.fields["text"]?.toDisplayString() ?: ""
            } else {
                target
            }
            extractHtmlTable(html)
        }

        module.members["browser"] = NepaliBuiltin("browser") { args, _ ->
            val url = (args.getOrNull(0) as? NepaliString)?.value ?: "https://example.com"
            callbacks.onWebOpen(url)
            createBrowserSession(url)
        }

        return module
    }

    // --- browser ---
    private fun createBrowserModule(): NepaliModule {
        val module = NepaliModule("browser")
        module.members["khol"] = NepaliBuiltin("khol") { args, _ ->
            val url = (args.getOrNull(0) as? NepaliString)?.value ?: "https://example.com"
            callbacks.onWebOpen(url)
            createBrowserSession(url)
        }
        module.members["open"] = module.members["khol"]!!
        return module
    }

    private fun createBrowserSession(initialUrl: String): NepaliObject {
        var currentUrl = initialUrl
        var currentTitle = "Example Domain"
        var currentHtml = "<html><head><title>Example Domain</title></head><body><h1>Example Domain</h1><p>This is a simulated browser session.</p><a href='https://example.com/docs'>Documentation</a></body></html>"
        val session = NepaliObject("BrowserSession")

        session.fields["url"] = NepaliBuiltin("url") { args, _ ->
            val newUrl = (args.getOrNull(0) as? NepaliString)?.value ?: ""
            if (newUrl.isNotEmpty()) {
                currentUrl = newUrl
                callbacks.onWebOpen(currentUrl)
                callbacks.onAutomationAction("Browser Navigate", currentUrl)
            }
            NepaliString(currentUrl)
        }

        session.fields["click"] = NepaliBuiltin("click") { args, _ ->
            val target = args.getOrNull(0)?.toDisplayString() ?: "element"
            callbacks.onAutomationAction("Browser Click", "Clicked target: $target")
            NepaliNull
        }

        session.fields["type"] = NepaliBuiltin("type") { args, _ ->
            val selector = args.getOrNull(0)?.toDisplayString() ?: "#input"
            val text = args.getOrNull(1)?.toDisplayString() ?: ""
            callbacks.onAutomationAction("Browser Type", "Selector: $selector | Text: '$text'")
            NepaliNull
        }

        session.fields["wait"] = NepaliBuiltin("wait") { args, _ ->
            val secs = (args.getOrNull(0) as? NepaliNumber)?.value?.toLong() ?: 1L
            callbacks.onAutomationAction("Browser Wait", "Waiting ${secs}s...")
            delay(minOf(secs, 3L) * 500)
            NepaliNull
        }

        session.fields["title"] = NepaliBuiltin("title") { _, _ ->
            NepaliString(currentTitle)
        }

        session.fields["extract"] = NepaliBuiltin("extract") { args, _ ->
            val selector = args.getOrNull(0)?.toDisplayString() ?: "h1"
            val results = extractHtmlElements(currentHtml, selector)
            callbacks.onAutomationAction("Browser Extract", "Selector: '$selector' | Count: ${results.size}")
            NepaliList(results.map { NepaliString(it) }.toMutableList())
        }

        session.fields["extract_links"] = NepaliBuiltin("extract_links") { _, _ ->
            val links = extractHtmlLinks(currentHtml)
            callbacks.onAutomationAction("Browser Extract Links", "Found: ${links.size} links")
            NepaliList(links.toMutableList())
        }

        session.fields["screenshot"] = NepaliBuiltin("screenshot") { args, _ ->
            val filename = args.getOrNull(0)?.toDisplayString() ?: "screenshot.png"
            callbacks.onAutomationAction("Screenshot", "Captured view into $filename")
            NepaliString("Saved screenshot: $filename")
        }

        session.fields["scroll"] = NepaliBuiltin("scroll") { args, _ ->
            val y = (args.getOrNull(1) as? NepaliNumber)?.value?.toLong() ?: 500L
            callbacks.onAutomationAction("Browser Scroll", "Scrolled Y: ${y}px")
            NepaliNull
        }

        session.fields["evaluate"] = NepaliBuiltin("evaluate") { args, _ ->
            val js = args.getOrNull(0)?.toDisplayString() ?: "document.title"
            callbacks.onAutomationAction("JavaScript Eval", js)
            NepaliString(currentTitle)
        }

        session.fields["close"] = NepaliBuiltin("close") { _, _ ->
            callbacks.onAutomationAction("Browser Close", "Closed browser session")
            NepaliNull
        }

        return session
    }

    // --- automation ---
    private fun createAutomationModule(): NepaliModule {
        val module = NepaliModule("automation")

        val mouseObj = NepaliObject("Mouse")
        mouseObj.fields["move"] = NepaliBuiltin("move") { args, _ ->
            val x = (args.getOrNull(0) as? NepaliNumber)?.toLong() ?: 0L
            val y = (args.getOrNull(1) as? NepaliNumber)?.toLong() ?: 0L
            callbacks.onAutomationAction("Mouse Move", "Coordinates: ($x, $y)")
            NepaliNull
        }
        mouseObj.fields["click"] = NepaliBuiltin("click") { _, _ ->
            callbacks.onAutomationAction("Mouse Click", "Primary Click executed")
            NepaliNull
        }
        module.members["mouse"] = mouseObj

        val keyboardObj = NepaliObject("Keyboard")
        keyboardObj.fields["write"] = NepaliBuiltin("write") { args, _ ->
            val text = args.getOrNull(0)?.toDisplayString() ?: ""
            callbacks.onAutomationAction("Keyboard Write", "Typed: '$text'")
            NepaliNull
        }
        keyboardObj.fields["press"] = NepaliBuiltin("press") { args, _ ->
            val key = args.getOrNull(0)?.toDisplayString() ?: "enter"
            callbacks.onAutomationAction("Keyboard Press", "Key: '$key'")
            NepaliNull
        }
        module.members["keyboard"] = keyboardObj

        module.members["wait"] = NepaliBuiltin("wait") { args, _ ->
            val secs = (args.getOrNull(0) as? NepaliNumber)?.value?.toLong() ?: 1L
            callbacks.onAutomationAction("Automation Wait", "Waiting ${secs}s...")
            delay(minOf(secs, 3L) * 500)
            NepaliNull
        }

        module.members["screen"] = NepaliBuiltin("screen") { args, _ ->
            val file = args.getOrNull(0)?.toDisplayString() ?: "screen.png"
            callbacks.onAutomationAction("Screenshot Capture", "Saved to $file")
            NepaliString(file)
        }

        val clipboardObj = NepaliObject("Clipboard")
        clipboardObj.fields["set"] = NepaliBuiltin("set") { args, _ ->
            val t = args.getOrNull(0)?.toDisplayString() ?: ""
            virtualClipboard = t
            callbacks.onAutomationAction("Clipboard Set", "'$t'")
            NepaliNull
        }
        clipboardObj.fields["get"] = NepaliBuiltin("get") { _, _ ->
            callbacks.onAutomationAction("Clipboard Get", "'$virtualClipboard'")
            NepaliString(virtualClipboard)
        }
        module.members["clipboard"] = clipboardObj

        val windowObj = NepaliObject("Window")
        windowObj.fields["list"] = NepaliBuiltin("list") { _, _ ->
            val list = NepaliList(mutableListOf(NepaliString("NepaliCode IDE"), NepaliString("Terminal"), NepaliString("Web Preview")))
            list
        }
        windowObj.fields["focus"] = NepaliBuiltin("focus") { args, _ ->
            val name = args.getOrNull(0)?.toDisplayString() ?: "App"
            callbacks.onAutomationAction("Window Focus", "Switched focus to: $name")
            NepaliNull
        }
        module.members["window"] = windowObj

        return module
    }

    // --- file ---
    private fun createFileModule(): NepaliModule {
        val module = NepaliModule("file")

        module.members["write"] = NepaliBuiltin("write") { args, _ ->
            val path = args.getOrNull(0)?.toDisplayString() ?: "output.txt"
            val text = args.getOrNull(1)?.toDisplayString() ?: ""
            val f = File(workspaceDir, path)
            f.parentFile?.mkdirs()
            f.writeText(text)
            callbacks.onAutomationAction("File Write", "Wrote ${text.length} chars to $path")
            NepaliBool(true)
        }

        module.members["read"] = NepaliBuiltin("read") { args, _ ->
            val path = args.getOrNull(0)?.toDisplayString() ?: ""
            val f = File(workspaceDir, path)
            if (f.exists()) {
                NepaliString(f.readText())
            } else {
                throw IllegalArgumentException("File '$path' not found in workspace.")
            }
        }

        module.members["exists"] = NepaliBuiltin("exists") { args, _ ->
            val path = args.getOrNull(0)?.toDisplayString() ?: ""
            NepaliBool(File(workspaceDir, path).exists())
        }

        module.members["delete"] = NepaliBuiltin("delete") { args, _ ->
            val path = args.getOrNull(0)?.toDisplayString() ?: ""
            NepaliBool(File(workspaceDir, path).delete())
        }

        module.members["copy"] = NepaliBuiltin("copy") { args, _ ->
            val src = args.getOrNull(0)?.toDisplayString() ?: ""
            val dst = args.getOrNull(1)?.toDisplayString() ?: ""
            val srcFile = File(workspaceDir, src)
            val dstFile = File(workspaceDir, dst)
            dstFile.parentFile?.mkdirs()
            if (srcFile.exists()) {
                srcFile.copyTo(dstFile, overwrite = true)
                NepaliBool(true)
            } else {
                NepaliBool(false)
            }
        }

        module.members["move"] = NepaliBuiltin("move") { args, _ ->
            val src = args.getOrNull(0)?.toDisplayString() ?: ""
            val dst = args.getOrNull(1)?.toDisplayString() ?: ""
            val srcFile = File(workspaceDir, src)
            val dstFile = File(workspaceDir, dst)
            dstFile.parentFile?.mkdirs()
            if (srcFile.exists()) {
                srcFile.copyTo(dstFile, overwrite = true)
                srcFile.delete()
                NepaliBool(true)
            } else {
                NepaliBool(false)
            }
        }

        return module
    }

    // --- folder ---
    private fun createFolderModule(): NepaliModule {
        val module = NepaliModule("folder")

        module.members["create"] = NepaliBuiltin("create") { args, _ ->
            val name = args.getOrNull(0)?.toDisplayString() ?: "folder"
            val f = File(workspaceDir, name)
            NepaliBool(f.mkdirs())
        }

        module.members["exists"] = NepaliBuiltin("exists") { args, _ ->
            val name = args.getOrNull(0)?.toDisplayString() ?: ""
            val f = File(workspaceDir, name)
            NepaliBool(f.exists() && f.isDirectory)
        }

        module.members["list"] = NepaliBuiltin("list") { args, _ ->
            val name = args.getOrNull(0)?.toDisplayString() ?: "."
            val f = if (name == ".") workspaceDir else File(workspaceDir, name)
            val items: MutableList<NepaliValue> = f.list()?.map { NepaliString(it) }?.toMutableList() ?: mutableListOf()
            NepaliList(items)
        }

        module.members["delete"] = NepaliBuiltin("delete") { args, _ ->
            val name = args.getOrNull(0)?.toDisplayString() ?: ""
            val f = File(workspaceDir, name)
            NepaliBool(f.deleteRecursively())
        }

        return module
    }

    // --- system ---
    private fun createSystemModule(): NepaliModule {
        val module = NepaliModule("system")
        module.members["os"] = NepaliBuiltin("os") { _, _ -> NepaliString("Android / Linux") }
        module.members["platform"] = NepaliBuiltin("platform") { _, _ -> NepaliString("NepaliLang 0.1.0 Mobile VM") }
        module.members["hostname"] = NepaliBuiltin("hostname") { _, _ -> NepaliString("android-nepali") }
        module.members["username"] = NepaliBuiltin("username") { _, _ -> NepaliString("diwas") }
        return module
    }

    // --- ganit (math) ---
    private fun createGanitModule(): NepaliModule {
        val module = NepaliModule("ganit")
        module.members["jod"] = NepaliBuiltin("jod") { args, _ ->
            val a = (args.getOrNull(0) as? NepaliNumber)?.value ?: 0.0
            val b = (args.getOrNull(1) as? NepaliNumber)?.value ?: 0.0
            NepaliNumber(a + b, isInt = (a % 1.0 == 0.0 && b % 1.0 == 0.0))
        }
        module.members["ghata"] = NepaliBuiltin("ghata") { args, _ ->
            val a = (args.getOrNull(0) as? NepaliNumber)?.value ?: 0.0
            val b = (args.getOrNull(1) as? NepaliNumber)?.value ?: 0.0
            NepaliNumber(a - b, isInt = (a % 1.0 == 0.0 && b % 1.0 == 0.0))
        }
        module.members["guna"] = NepaliBuiltin("guna") { args, _ ->
            val a = (args.getOrNull(0) as? NepaliNumber)?.value ?: 0.0
            val b = (args.getOrNull(1) as? NepaliNumber)?.value ?: 0.0
            NepaliNumber(a * b, isInt = (a % 1.0 == 0.0 && b % 1.0 == 0.0))
        }
        module.members["bhag"] = NepaliBuiltin("bhag") { args, _ ->
            val a = (args.getOrNull(0) as? NepaliNumber)?.value ?: 0.0
            val b = (args.getOrNull(1) as? NepaliNumber)?.value ?: 1.0
            if (b == 0.0) throw ArithmeticException("Division by zero in ganit.bhag")
            NepaliNumber(a / b)
        }
        module.members["sqrt"] = NepaliBuiltin("sqrt") { args, _ ->
            val a = (args.getOrNull(0) as? NepaliNumber)?.value ?: 0.0
            NepaliNumber(sqrt(a))
        }
        module.members["power"] = NepaliBuiltin("power") { args, _ ->
            val a = (args.getOrNull(0) as? NepaliNumber)?.value ?: 0.0
            val b = (args.getOrNull(1) as? NepaliNumber)?.value ?: 1.0
            NepaliNumber(a.pow(b))
        }
        module.members["abs"] = NepaliBuiltin("abs") { args, _ ->
            val a = (args.getOrNull(0) as? NepaliNumber)?.value ?: 0.0
            NepaliNumber(abs(a))
        }
        module.members["round"] = NepaliBuiltin("round") { args, _ ->
            val a = (args.getOrNull(0) as? NepaliNumber)?.value ?: 0.0
            NepaliNumber(round(a), isInt = true)
        }
        return module
    }

    // --- samaya (time) ---
    private fun createSamayaModule(): NepaliModule {
        val module = NepaliModule("samaya")
        module.members["now"] = NepaliBuiltin("now") { _, _ ->
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            NepaliString(sdf.format(Date()))
        }
        module.members["today"] = NepaliBuiltin("today") { _, _ ->
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            NepaliString(sdf.format(Date()))
        }
        module.members["sleep"] = NepaliBuiltin("sleep") { args, _ ->
            val secs = (args.getOrNull(0) as? NepaliNumber)?.value?.toLong() ?: 1L
            callbacks.onAutomationAction("Sleep", "Sleeping for ${secs}s...")
            delay(minOf(secs, 5L) * 1000)
            NepaliNull
        }
        module.members["format"] = NepaliBuiltin("format") { args, _ ->
            val dateStr = args.getOrNull(0)?.toDisplayString() ?: ""
            val pattern = args.getOrNull(1)?.toDisplayString() ?: "yyyy-MM-dd"
            NepaliString(dateStr)
        }
        return module
    }

    // --- json ---
    private fun createJsonModule(): NepaliModule {
        val module = NepaliModule("json")
        module.members["parse"] = NepaliBuiltin("parse") { args, _ ->
            val text = args.getOrNull(0)?.toDisplayString() ?: "{}"
            parseJsonStringToNepaliValue(text)
        }
        module.members["stringify"] = NepaliBuiltin("stringify") { args, _ ->
            val v = args.getOrNull(0) ?: NepaliNull
            NepaliString(v.toDisplayString())
        }
        module.members["read"] = NepaliBuiltin("read") { args, _ ->
            val path = args.getOrNull(0)?.toDisplayString() ?: "data.json"
            val f = File(workspaceDir, path)
            if (f.exists()) {
                parseJsonStringToNepaliValue(f.readText())
            } else {
                throw IllegalArgumentException("JSON file '$path' not found.")
            }
        }
        module.members["write"] = NepaliBuiltin("write") { args, _ ->
            val path = args.getOrNull(0)?.toDisplayString() ?: "data.json"
            val data = args.getOrNull(1) ?: NepaliNull
            val f = File(workspaceDir, path)
            f.writeText(data.toDisplayString())
            NepaliBool(true)
        }
        return module
    }

    // --- random ---
    private fun createRandomModule(): NepaliModule {
        val module = NepaliModule("random")
        module.members["number"] = NepaliBuiltin("number") { args, _ ->
            val min = (args.getOrNull(0) as? NepaliNumber)?.toLong() ?: 0L
            val max = (args.getOrNull(1) as? NepaliNumber)?.toLong() ?: 100L
            val res = Random.nextLong(min, max + 1)
            NepaliNumber(res.toDouble(), isInt = true)
        }
        module.members["choice"] = NepaliBuiltin("choice") { args, _ ->
            val list = (args.getOrNull(0) as? NepaliList)?.elements ?: emptyList()
            if (list.isNotEmpty()) {
                list[Random.nextInt(list.size)]
            } else {
                NepaliNull
            }
        }
        module.members["shuffle"] = NepaliBuiltin("shuffle") { args, _ ->
            val list = (args.getOrNull(0) as? NepaliList)?.elements
            list?.shuffle()
            NepaliNull
        }
        return module
    }

    // --- database (SQLite) ---
    private fun createDatabaseModule(): NepaliModule {
        val module = NepaliModule("database")
        module.members["open"] = NepaliBuiltin("open") { args, _ ->
            val dbName = args.getOrNull(0)?.toDisplayString() ?: "app.db"
            val dbFile = File(workspaceDir, dbName)
            val sqlite = SQLiteDatabase.openOrCreateDatabase(dbFile, null)
            callbacks.onAutomationAction("Database Open", "Opened SQLite DB: $dbName")

            val dbObj = NepaliObject("DatabaseConnection")
            dbObj.fields["execute"] = NepaliBuiltin("execute") { exArgs, _ ->
                val sql = exArgs.getOrNull(0)?.toDisplayString() ?: ""
                val params = (exArgs.getOrNull(1) as? NepaliList)?.elements?.map { it.toDisplayString() }?.toTypedArray()
                if (params != null && params.isNotEmpty()) {
                    sqlite.execSQL(sql, params)
                } else {
                    sqlite.execSQL(sql)
                }
                callbacks.onAutomationAction("SQL Exec", sql)
                NepaliNull
            }

            dbObj.fields["query"] = NepaliBuiltin("query") { qArgs, _ ->
                val sql = qArgs.getOrNull(0)?.toDisplayString() ?: ""
                val cursor = sqlite.rawQuery(sql, null)
                val rows = mutableListOf<NepaliValue>()
                val colNames = cursor.columnNames
                while (cursor.moveToNext()) {
                    val rowMap = mutableMapOf<String, NepaliValue>()
                    for (i in colNames.indices) {
                        rowMap[colNames[i]] = NepaliString(cursor.getString(i) ?: "")
                    }
                    rows.add(NepaliMap(rowMap))
                }
                cursor.close()
                callbacks.onAutomationAction("SQL Query", "Returned ${rows.size} rows")
                NepaliList(rows)
            }

            dbObj.fields["close"] = NepaliBuiltin("close") { _, _ ->
                if (sqlite.isOpen) sqlite.close()
                callbacks.onAutomationAction("Database Close", "Closed SQLite DB")
                NepaliNull
            }

            dbObj
        }
        return module
    }

    // --- terminal ---
    private fun createTerminalModule(): NepaliModule {
        val module = NepaliModule("terminal")
        module.members["clear"] = NepaliBuiltin("clear") { _, _ ->
            callbacks.onTerminalClear()
            NepaliNull
        }
        module.members["write"] = NepaliBuiltin("write") { args, _ ->
            val t = args.getOrNull(0)?.toDisplayString() ?: ""
            callbacks.onPrint(t)
            NepaliNull
        }
        module.members["ask"] = NepaliBuiltin("ask") { args, _ ->
            val prompt = args.getOrNull(0)?.toDisplayString() ?: ""
            val ans = callbacks.onInput(prompt)
            NepaliString(ans)
        }
        return module
    }

    // --- log ---
    private fun createLogModule(): NepaliModule {
        val module = NepaliModule("log")
        module.members["info"] = NepaliBuiltin("info") { args, _ ->
            val m = args.getOrNull(0)?.toDisplayString() ?: ""
            callbacks.onLog("INFO", m)
            NepaliNull
        }
        module.members["warning"] = NepaliBuiltin("warning") { args, _ ->
            val m = args.getOrNull(0)?.toDisplayString() ?: ""
            callbacks.onLog("WARNING", m)
            NepaliNull
        }
        module.members["error"] = NepaliBuiltin("error") { args, _ ->
            val m = args.getOrNull(0)?.toDisplayString() ?: ""
            callbacks.onLog("ERROR", m)
            NepaliNull
        }
        return module
    }

    // --- env ---
    private fun createEnvModule(): NepaliModule {
        val module = NepaliModule("env")
        module.members["get"] = NepaliBuiltin("get") { args, _ ->
            val key = args.getOrNull(0)?.toDisplayString() ?: ""
            val v = environmentVariables[key]
            if (v != null) NepaliString(v) else NepaliNull
        }
        module.members["set"] = NepaliBuiltin("set") { args, _ ->
            val key = args.getOrNull(0)?.toDisplayString() ?: ""
            val value = args.getOrNull(1)?.toDisplayString() ?: ""
            environmentVariables[key] = value
            NepaliNull
        }
        return module
    }

    // Helpers
    private fun parseJsonStringToNepaliValue(json: String): NepaliValue {
        val trimmed = json.trim()
        return try {
            when {
                trimmed.startsWith("{") -> {
                    val obj = JSONObject(trimmed)
                    val map = mutableMapOf<String, NepaliValue>()
                    for (key in obj.keys()) {
                        map[key] = jsonElementToNepaliValue(obj.get(key))
                    }
                    NepaliMap(map)
                }
                trimmed.startsWith("[") -> {
                    val arr = JSONArray(trimmed)
                    val list = mutableListOf<NepaliValue>()
                    for (i in 0 until arr.length()) {
                        list.add(jsonElementToNepaliValue(arr.get(i)))
                    }
                    NepaliList(list)
                }
                else -> NepaliString(trimmed)
            }
        } catch (e: Exception) {
            NepaliString(trimmed)
        }
    }

    private fun jsonElementToNepaliValue(elem: Any?): NepaliValue {
        return when (elem) {
            is JSONObject -> {
                val map = mutableMapOf<String, NepaliValue>()
                for (k in elem.keys()) {
                    map[k] = jsonElementToNepaliValue(elem.get(k))
                }
                NepaliMap(map)
            }
            is JSONArray -> {
                val list = mutableListOf<NepaliValue>()
                for (i in 0 until elem.length()) {
                    list.add(jsonElementToNepaliValue(elem.get(i)))
                }
                NepaliList(list)
            }
            is Number -> NepaliNumber(elem.toDouble(), isInt = (elem is Int || elem is Long))
            is Boolean -> NepaliBool(elem)
            is String -> NepaliString(elem)
            null, JSONObject.NULL -> NepaliNull
            else -> NepaliString(elem.toString())
        }
    }

    private fun nepaliMapToJsonObject(map: NepaliMap): JSONObject {
        val obj = JSONObject()
        map.entries.forEach { (k, v) ->
            obj.put(k, nepaliValueToJson(v))
        }
        return obj
    }

    private fun nepaliValueToJson(value: NepaliValue): Any {
        return when (value) {
            is NepaliNumber -> if (value.isInt) value.toLong() else value.value
            is NepaliString -> value.value
            is NepaliBool -> value.value
            is NepaliNull -> JSONObject.NULL
            is NepaliMap -> nepaliMapToJsonObject(value)
            is NepaliList -> {
                val arr = JSONArray()
                value.elements.forEach { arr.put(nepaliValueToJson(it)) }
                arr
            }
            else -> value.toDisplayString()
        }
    }

    // --- regex / khoj ---
    private fun createRegexModule(): NepaliModule {
        val module = NepaliModule("regex")
        module.members["khoj"] = NepaliBuiltin("khoj") { args, _ ->
            val patternStr = args.getOrNull(0)?.toDisplayString() ?: ""
            val text = args.getOrNull(1)?.toDisplayString() ?: ""
            val r = Regex(patternStr)
            val m = r.find(text)
            if (m != null) {
                val map = mutableMapOf<String, NepaliValue>(
                    "match" to NepaliString(m.value),
                    "start" to NepaliNumber(m.range.first.toDouble(), isInt = true),
                    "end" to NepaliNumber(m.range.last.toDouble(), isInt = true),
                    "groups" to NepaliList(m.groupValues.map { NepaliString(it) }.toMutableList())
                )
                NepaliMap(map)
            } else {
                NepaliNull
            }
        }

        module.members["search"] = module.members["khoj"]!!

        module.members["sabai_khoj"] = NepaliBuiltin("sabai_khoj") { args, _ ->
            val patternStr = args.getOrNull(0)?.toDisplayString() ?: ""
            val text = args.getOrNull(1)?.toDisplayString() ?: ""
            val r = Regex(patternStr)
            val list = r.findAll(text).map { NepaliString(it.value) as NepaliValue }.toMutableList()
            NepaliList(list)
        }
        module.members["findall"] = module.members["sabai_khoj"]!!

        module.members["badal"] = NepaliBuiltin("badal") { args, _ ->
            val patternStr = args.getOrNull(0)?.toDisplayString() ?: ""
            val replacement = args.getOrNull(1)?.toDisplayString() ?: ""
            val text = args.getOrNull(2)?.toDisplayString() ?: ""
            val res = Regex(patternStr).replace(text, replacement)
            NepaliString(res)
        }
        module.members["sub"] = module.members["badal"]!!

        module.members["milcha"] = NepaliBuiltin("milcha") { args, _ ->
            val patternStr = args.getOrNull(0)?.toDisplayString() ?: ""
            val text = args.getOrNull(1)?.toDisplayString() ?: ""
            NepaliBool(Regex(patternStr).matches(text))
        }
        module.members["match"] = module.members["milcha"]!!

        return module
    }

    private fun extractHtmlElements(html: String, selector: String): List<String> {
        val trimmed = selector.trim().lowercase()
        val tagName = when {
            trimmed.startsWith("#") -> "[a-zA-Z0-9]+"
            trimmed.startsWith(".") -> "[a-zA-Z0-9]+"
            trimmed in listOf("h1", "h2", "h3", "h4", "h5", "h6", "p", "a", "li", "span", "div", "title", "b", "strong", "td", "th", "article", "section") -> trimmed
            else -> "[a-zA-Z0-9]+"
        }

        val pattern = if (trimmed.startsWith(".")) {
            val cls = Regex.escape(trimmed.removePrefix("."))
            Regex("<([a-zA-Z0-9]+)[^>]*class=[\"'][^\"']*\\b$cls\\b[^\"']*[\"'][^>]*>(.*?)</\\1>", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
        } else if (trimmed.startsWith("#")) {
            val id = Regex.escape(trimmed.removePrefix("#"))
            Regex("<([a-zA-Z0-9]+)[^>]*id=[\"']$id[\"'][^>]*>(.*?)</\\1>", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
        } else {
            Regex("<$tagName(?:\\s+[^>]*)?>(.*?)</$tagName>", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
        }

        val matches = pattern.findAll(html).map { matchResult ->
            val inner = if (matchResult.groups.size >= 3) matchResult.groups[2]?.value ?: "" else matchResult.groups[1]?.value ?: ""
            cleanHtmlText(inner)
        }.filter { it.isNotBlank() }.toList()

        if (matches.isEmpty() && html.contains("<$trimmed", ignoreCase = true)) {
            val fallback = Regex("<$trimmed[^>]*>(.*?)</$trimmed>", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
            return fallback.findAll(html).map { cleanHtmlText(it.groupValues.getOrElse(1) { "" }) }.filter { it.isNotBlank() }.toList()
        }
        return matches
    }

    private fun extractHtmlLinks(html: String): List<NepaliValue> {
        val regex = Regex("<a\\s+(?:[^>]*?\\s+)?href=[\"']([^\"']*)[\"'][^>]*>(.*?)</a>", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
        return regex.findAll(html).map { mr ->
            val url = mr.groupValues[1]
            val text = cleanHtmlText(mr.groupValues[2])
            val map = mutableMapOf<String, NepaliValue>(
                "url" to NepaliString(url),
                "text" to NepaliString(text)
            )
            NepaliMap(map)
        }.toList()
    }

    private fun cleanHtmlText(html: String): String {
        return html
            .replace(Regex("<script[\\s\\S]*?</script>", RegexOption.IGNORE_CASE), "")
            .replace(Regex("<style[\\s\\S]*?</style>", RegexOption.IGNORE_CASE), "")
            .replace(Regex("<[^>]+>"), " ")
            .replace("&nbsp;", " ")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    private fun extractHtmlTable(html: String): NepaliList {
        val rowRegex = Regex("<tr[^>]*>(.*?)</tr>", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
        val colRegex = Regex("<(?:td|th)[^>]*>(.*?)</(?:td|th)>", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
        val rows = mutableListOf<NepaliValue>()

        rowRegex.findAll(html).forEach { rowMatch ->
            val cols = colRegex.findAll(rowMatch.groupValues[1]).map { colMatch ->
                NepaliString(cleanHtmlText(colMatch.groupValues[1])) as NepaliValue
            }.toMutableList()
            if (cols.isNotEmpty()) {
                rows.add(NepaliList(cols))
            }
        }
        return NepaliList(rows)
    }

    private fun resolveFile(path: String): File = File(workspaceDir, path)

    // --- csv module ---
    private fun createCsvModule(): NepaliModule {
        val module = NepaliModule("csv")
        module.members["read"] = NepaliBuiltin("read") { args, _ ->
            val path = (args.getOrNull(0) as? NepaliString)?.value
                ?: throw IllegalArgumentException("csv.read expects file path string")
            val targetFile = resolveFile(path)
            if (!targetFile.exists()) {
                throw IllegalArgumentException("CSV file not found: $path")
            }
            val lines = targetFile.readLines()
            val parsedRows = mutableListOf<NepaliValue>()
            for (line in lines) {
                if (line.isNotBlank()) {
                    val cols = line.split(",").map { NepaliString(it.trim()) as NepaliValue }.toMutableList()
                    parsedRows.add(NepaliList(cols))
                }
            }
            NepaliList(parsedRows)
        }
        module.members["write"] = NepaliBuiltin("write") { args, _ ->
            val path = (args.getOrNull(0) as? NepaliString)?.value
                ?: throw IllegalArgumentException("csv.write expects file path string")
            val rows = (args.getOrNull(1) as? NepaliList)?.elements
                ?: throw IllegalArgumentException("csv.write expects list of rows")
            val sb = StringBuilder()
            for (r in rows) {
                if (r is NepaliList) {
                    sb.appendLine(r.elements.joinToString(",") { it.toDisplayString() })
                } else {
                    sb.appendLine(r.toDisplayString())
                }
            }
            val targetFile = resolveFile(path)
            targetFile.parentFile?.mkdirs()
            targetFile.writeText(sb.toString())
            NepaliBool(true)
        }
        return module
    }

    // --- path module ---
    private fun createPathModule(): NepaliModule {
        val module = NepaliModule("path")
        module.members["join"] = NepaliBuiltin("join") { args, _ ->
            val p1 = args.getOrNull(0)?.toDisplayString() ?: ""
            val p2 = args.getOrNull(1)?.toDisplayString() ?: ""
            NepaliString(File(p1, p2).path)
        }
        module.members["basename"] = NepaliBuiltin("basename") { args, _ ->
            val p = args.getOrNull(0)?.toDisplayString() ?: ""
            NepaliString(File(p).name)
        }
        module.members["dirname"] = NepaliBuiltin("dirname") { args, _ ->
            val p = args.getOrNull(0)?.toDisplayString() ?: ""
            NepaliString(File(p).parent ?: "")
        }
        module.members["extension"] = NepaliBuiltin("extension") { args, _ ->
            val p = args.getOrNull(0)?.toDisplayString() ?: ""
            NepaliString(File(p).extension)
        }
        module.members["exists"] = NepaliBuiltin("exists") { args, _ ->
            val p = args.getOrNull(0)?.toDisplayString() ?: ""
            NepaliBool(resolveFile(p).exists())
        }
        return module
    }

    // --- command module ---
    private fun createCommandModule(): NepaliModule {
        val module = NepaliModule("command")
        module.members["arguments"] = NepaliBuiltin("arguments") { _, _ ->
            NepaliList(mutableListOf(NepaliString("nepali"), NepaliString("run"), NepaliString("main.np")))
        }
        return module
    }

    // --- process module ---
    private fun createProcessModule(): NepaliModule {
        val module = NepaliModule("process")
        module.members["run"] = NepaliBuiltin("run") { args, _ ->
            val cmd = args.getOrNull(0)?.toDisplayString() ?: ""
            callbacks.onPrint("⚙️ [process.run] Executing: $cmd")
            val obj = NepaliObject("ProcessResult")
            obj.fields["code"] = NepaliNumber(0.0, isInt = true)
            obj.fields["output"] = NepaliString("Command '$cmd' completed with status 0")
            obj.fields["ok"] = NepaliBool(true)
            obj
        }
        return module
    }

    // --- suraksha / hash module ---
    private fun createSurakshaModule(): NepaliModule {
        val module = NepaliModule("suraksha")
        module.members["sha256"] = NepaliBuiltin("sha256") { args, _ ->
            val text = args.getOrNull(0)?.toDisplayString() ?: ""
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(text.toByteArray())
            NepaliString(digest.joinToString("") { "%02x".format(it) })
        }
        module.members["md5"] = NepaliBuiltin("md5") { args, _ ->
            val text = args.getOrNull(0)?.toDisplayString() ?: ""
            val md = MessageDigest.getInstance("MD5")
            val digest = md.digest(text.toByteArray())
            NepaliString(digest.joinToString("") { "%02x".format(it) })
        }
        module.members["base64_encode"] = NepaliBuiltin("base64_encode") { args, _ ->
            val text = args.getOrNull(0)?.toDisplayString() ?: ""
            NepaliString(Base64.encodeToString(text.toByteArray(), Base64.NO_WRAP))
        }
        module.members["base64_decode"] = NepaliBuiltin("base64_decode") { args, _ ->
            val encoded = args.getOrNull(0)?.toDisplayString() ?: ""
            try {
                NepaliString(String(Base64.decode(encoded, Base64.DEFAULT)))
            } catch (e: Exception) {
                NepaliString("")
            }
        }
        return module
    }

    // --- email module ---
    private fun createEmailModule(): NepaliModule {
        val module = NepaliModule("email")
        module.members["send"] = NepaliBuiltin("send") { args, _ ->
            val to = args.getOrNull(0)?.toDisplayString() ?: ""
            val subject = args.getOrNull(1)?.toDisplayString() ?: ""
            val body = args.getOrNull(2)?.toDisplayString() ?: ""
            callbacks.onPrint("📧 [Email Dispatched] To: $to | Subject: $subject | Body: ${body.take(40)}...")
            NepaliBool(true)
        }
        return module
    }

    // --- zip module ---
    private fun createZipModule(): NepaliModule {
        val module = NepaliModule("zip")
        module.members["create"] = NepaliBuiltin("create") { args, _ ->
            val zipName = args.getOrNull(0)?.toDisplayString() ?: "archive.zip"
            val fileList = (args.getOrNull(1) as? NepaliList)?.elements?.map { it.toDisplayString() } ?: emptyList()
            val zipFile = resolveFile(zipName)
            zipFile.parentFile?.mkdirs()
            FileOutputStream(zipFile).use { fos ->
                ZipOutputStream(fos).use { zos ->
                    for (fn in fileList) {
                        val f = resolveFile(fn)
                        if (f.exists()) {
                            val entry = ZipEntry(f.name)
                            zos.putNextEntry(entry)
                            f.inputStream().use { input ->
                                input.copyTo(zos)
                            }
                            zos.closeEntry()
                        }
                    }
                }
            }
            callbacks.onPrint("📦 [Zip Created] ${zipFile.name} with ${fileList.size} files")
            NepaliBool(true)
        }
        module.members["extract"] = NepaliBuiltin("extract") { args, _ ->
            val zipName = args.getOrNull(0)?.toDisplayString() ?: "archive.zip"
            val destDirName = args.getOrNull(1)?.toDisplayString() ?: "extracted"
            val destDir = resolveFile(destDirName)
            if (!destDir.exists()) destDir.mkdirs()
            val zipFile = resolveFile(zipName)
            var count = 0
            if (zipFile.exists()) {
                FileInputStream(zipFile).use { fis ->
                    ZipInputStream(fis).use { zis ->
                        var entry = zis.nextEntry
                        while (entry != null) {
                            val outFile = File(destDir, entry.name)
                            if (!entry.isDirectory) {
                                outFile.parentFile?.mkdirs()
                                FileOutputStream(outFile).use { fos ->
                                    zis.copyTo(fos)
                                }
                                count++
                            }
                            zis.closeEntry()
                            entry = zis.nextEntry
                        }
                    }
                }
            }
            callbacks.onPrint("📦 [Zip Extracted] Extracted $count files to ${destDir.name}")
            NepaliBool(true)
        }
        return module
    }
}
