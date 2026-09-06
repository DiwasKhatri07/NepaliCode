package com.nepalicode.dev.nepalicode.data

import android.content.Context
import java.io.File

data class ProjectFile(
    val name: String,
    var content: String,
    val isModified: Boolean = false,
    val isSystem: Boolean = false
)

class ProjectManager(private val context: Context) {
    private val projectDir: File by lazy {
        val dir = File(context.filesDir, "nepali_project")
        if (!dir.exists()) dir.mkdirs()
        dir
    }

    private val defaultFiles = listOf(
        ProjectFile(
            name = "main.np",
            content = """# 🇳🇵 NepaliLang v0.1.0 — Hello Nepal!
# Created by Diwas Khatri (NepaliSource)

naam = "दिवस"
print("Namaste Nepal!")
print(f"Developer: {naam}")

# Spec 2: Convenience constructor yo()
a = yo(3)
b = yo(4)
c = a + b
print("yo(3) + yo(4) =", c)

# Spec 3: Nepali function & conditional
kaam jod(x, y):
    firta x + y

sum_val = jod(10, 20)
print("Jod result:", sum_val)

yedi sum_val >= 25:
    print("Adult/High value condition met (sacho)")
natra:
    print("Minor/Lower value")

# Spec 3: Loops
print("Counting with ko_lagi loop:")
ko_lagi i ma 5:
    print(f"Step {i}")
"""
        ),
        ProjectFile(
            name = "api_demo.np",
            content = """# 🌐 NepaliLang HTTP Requests with anurodh
import anurodh
import json

print("Sending HTTP GET request via anurodh...")
response = anurodh.get("https://jsonplaceholder.typicode.com/todos/1")

print("Response Status:", response.status)
print("Is Successful (ok):", response.ok)
print("Response Body:")
print(response.text)

# Parse JSON into Map
todo = json.parse(response.text)
print("Parsed Title:", todo["title"])
"""
        ),
        ProjectFile(
            name = "web_automation.np",
            content = """# 🚀 Web & Browser Automation with NepaliLang
import web
import browser
import samaya

print("Opening web browser preview...")
web.khol("https://example.com")

print("Launching browser automation session...")
site = browser.khol("https://example.com")

site.type("#search", "NepaliLang")
site.click("#submit")
site.wait(1)

print("Web page title:", site.title())
site.close()
print("Web automation complete!")
"""
        ),
        ProjectFile(
            name = "desktop_automation.np",
            content = """# ⚡ Desktop & Mobile Automation with NepaliLang
import automation
import samaya

print("Starting automation tasks...")
automation.mouse.move(500, 300)
automation.mouse.click()

automation.keyboard.write("Namaste from NepaliCode!")
automation.keyboard.press("enter")

automation.clipboard.set("NepaliLang Automation Clipboard")
clip_text = automation.clipboard.get()
print("Retrieved Clipboard:", clip_text)

automation.screen("demo_screen.png")
print("Automation completed successfully.")
"""
        ),
        ProjectFile(
            name = "database_demo.np",
            content = """# 🗄️ SQLite Database with NepaliLang
import database

db = database.open("nepali_app.db")

db.execute("CREATE TABLE IF NOT EXISTS developers (id INTEGER PRIMARY KEY, name TEXT, role TEXT)")
db.execute("INSERT INTO developers (name, role) VALUES (?, ?)", ["Diwas Khatri", "Creator"])
db.execute("INSERT INTO developers (name, role) VALUES (?, ?)", ["Sandesh", "Engineer"])

rows = db.query("SELECT * FROM developers")
print("Fetched developers from SQLite:")
for row in rows:
    print("Developer:", row["name"], "| Role:", row["role"])

db.close()
print("Database operations complete.")
"""
        ),
        ProjectFile(
            name = "variables_and_types.np",
            content = """# 📚 NepaliLang Variables, Data Types & Conversions
# Learn ank, shabda, dashamlav, suchi, kosh, bool

# 1. Variables & Basic Types
naam = "Diwas Khatri"
umer = 22
dar = 98.6
developer_ho = sacho

dekha("--- Nepali Variables & Types ---")
dekha("Naam:", naam, "| Prakar:", prakar(naam))
dekha("Umer:", umer, "| Prakar:", prakar(umer))
dekha("Dar:", dar, "| Prakar:", prakar(dar))
dekha("Is Dev:", developer_ho, "| Prakar:", prakar(developer_ho))

# 2. Type Conversions
ank_string = "450"
paribartit_ank = ank(ank_string)
dekha("Converted string to number:", paribartit_ank + 50)

# 3. Suchi (List) & Operations
phalharu = ["Syau", "Aap", "Kera", "Suntala"]
dekha("Phalharu:", phalharu)
dekha("Lambai (Length):", lambai(phalharu))
dekha("Ulta (Reversed):", ulta(phalharu))
dekha("Kram (Sorted):", kram(phalharu))

# 4. Kosh (Dictionary / Map)
biddhyarthi = {
    "naam": "Sagar Sharma",
    "shreni": 12,
    "ank": 94,
    "pass": sacho
}
dekha("Student Record:", biddhyarthi)
dekha("Name from Kosh:", biddhyarthi["naam"])
"""
        ),
        ProjectFile(
            name = "web_scraping_news.np",
            content = """# 🕷️ Web Scraping & Data Extraction with NepaliLang
# Using the built-in 'web' module with CSS selector extraction

lyau web

dekha("1. Fetching web page from https://example.com ...")
page = web.get("https://example.com")

dekha("Page Title:", page.title())

# Extract specific elements using CSS selectors: h1, p, a, etc.
dekha("2. Extracting <h1> headings:")
headings = page.extract("h1")
dekha("Headings found:", headings)

dekha("3. Extracting all hyperlinks (<a href>):")
links = page.extract_links()
ko_lagi link ma links:
    dekha("-> Link URL:", link["url"], "| Text:", link["text"])

dekha("4. Clean text extraction:")
clean_text = page.extract_text()
dekha("Page text preview:", clean_text)
"""
        ),
        ProjectFile(
            name = "web_automation_advanced.np",
            content = """# 🤖 Browser Automation with Action Pipeline
# Automates browsing, clicking, typing, and screenshot capture

lyau browser

dekha("Launching virtual browser session...")
b = browser.khol("https://example.com")

dekha("1. Navigating to search form...")
b.url("https://example.com/search")

dekha("2. Typing search query into input box...")
b.type("#search_box", "NepaliLang Web Automation")

dekha("3. Clicking submit button...")
b.click("#submit_btn")

dekha("4. Waiting for dynamic content to render...")
b.wait(1)

dekha("5. Capturing viewport screenshot...")
b.screenshot("search_result.png")

dekha("6. Extracting result titles:")
results = b.extract("h2")
dekha("Search items:", results)

b.close()
dekha("Automation finished successfully! 🇳🇵")
"""
        ),
        ProjectFile(
            name = "regex_and_text.np",
            content = """# 🔍 Regular Expressions & Text Search in NepaliLang
# Using 'regex' (or Nepali alias 'khoj')

lyau regex

lekh = "Namaste! Contact Diwas at diwaskhatri935@gmail.com or call +977-9800000000. Project code: NP-2026."

dekha("Input Text:", lekh)

# 1. Search for phone numbers (+977-...)
phone_match = regex.khoj("\\+977-\\d+", lekh)
yedi phone_match != khali:
    dekha("Found Phone:", phone_match["match"])

# 2. Extract email address
email_match = regex.khoj("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}", lekh)
yedi email_match != khali:
    dekha("Found Email:", email_match["match"])

# 3. Find all uppercase words
codes = regex.sabai_khoj("[A-Z]{2}-\\d{4}", lekh)
dekha("Project Codes:", codes)

# 4. Replace / Mask sensitive data
masked = regex.badal("\\+977-\\d+", "[REDACTED PHONE]", lekh)
dekha("Masked text:", masked)
"""
        ),
        ProjectFile(
            name = "math_and_stats.np",
            content = """# 📐 Math & Scientific Calculations with NepaliLang
# Using 'ganit' module

lyau ganit

dekha("--- Ganit Module Demo ---")
dekha("ganit.sqrt(256) =", ganit.sqrt(256))
dekha("ganit.power(3, 4) =", ganit.power(3, 4))
dekha("ganit.jod(100, 250) =", ganit.jod(100, 250))
dekha("ganit.guna(12, 12) =", ganit.guna(12, 12))

# Statistical numbers list
numbers = [14, 28, 45, 99, 12, 67, 3]
dekha("Numbers:", numbers)
dekha("Min value:", min(numbers))
dekha("Max value:", max(numbers))
dekha("Sum (Jod):", sum(numbers))
dekha("Sorted (Kram):", kram(numbers))
dekha("Reversed (Ulta):", ulta(numbers))
"""
        ),
        ProjectFile(
            name = "guessing_game.np",
            content = """# 🎯 Interactive Number Guessing Game in NepaliLang
# Uses 'random', input 'leu', and conditionals 'yedi/natra'

lyau random

gupt_ank = random.ank(1, 10)
dekha("🇳🇵 Namaste! 1 dekhi 10 samma ko euta ank anumaan garnuhos!")

anumaan = 0
jabasamma anumaan != gupt_ank:
    koshish_input = leu("Tapai ko anumaan (Guess): ")
    anumaan = ank(koshish_input)
    
    yedi anumaan == gupt_ank:
        dekha("🎉 Badhaai chha! Tapai le sahi ank milaaunu bhayo:", gupt_ank)
    natabhaye anumaan < gupt_ank:
        dekha("Thulo ank sochnuhos! (Guess higher)")
    natra:
        dekha("Sano ank sochnuhos! (Guess lower)")
"""
        ),
        ProjectFile(
            name = "oop_classes.np",
            content = """# 🏛️ Object-Oriented Programming in NepaliCode
# Using 'kakshya' (class) and 'kaam' (methods)

kakshya Developer:
    kaam __init__(self, naam, role):
        self.naam = naam
        self.role = role

    kaam introduce(self):
        dekha(f"Namaste! Ma {self.naam} ho, role: {self.role}.")

dev1 = Developer("Diwas Khatri", "Founder & Lead Architect")
dev1.introduce()

dev2 = Developer("NepaliSource Team", "Core Engineering")
dev2.introduce()
"""
        ),
        ProjectFile(
            name = "csv_and_files.np",
            content = """# 📁 CSV and File Handling with NepaliCode
lyau csv
lyau path
lyau file

file_name = "team.csv"
rows = [
    ["Name", "Role", "Language"],
    ["Diwas Khatri", "Founder", "NepaliCode"],
    ["Aayush", "Contributor", "NPPM"],
    ["Sujata", "UI Designer", "Figma"]
]

dekha("Writing team data to CSV...")
csv.write(file_name, rows)

dekha("Checking if file exists with path module:")
dekha("File exists:", path.exists(file_name))
dekha("Basename:", path.basename(file_name))

dekha("Reading back CSV records:")
records = csv.read(file_name)
ko_lagi row ma records:
    dekha("Record:", row)
"""
        ),
        ProjectFile(
            name = "suraksha_crypto.np",
            content = """# 🔒 Cryptography & Security with 'suraksha' (hash)
lyau suraksha

lekh = "NepaliCode Secure Message 🇳🇵"
dekha("Original Message:", lekh)

# SHA-256 Hashing
hash256 = suraksha.sha256(lekh)
dekha("SHA-256 Hash:", hash256)

# MD5 Hashing
hash_md5 = suraksha.md5(lekh)
dekha("MD5 Hash:", hash_md5)

# Base64 Encoding & Decoding
encoded = suraksha.base64_encode(lekh)
dekha("Base64 Encoded:", encoded)

decoded = suraksha.base64_decode(encoded)
dekha("Decoded Message:", decoded)
"""
        ),
        ProjectFile(
            name = "nepali.toml",
            content = """[project]
name = "nepalilang-project"
version = "0.1.0"
author = "Diwas Khatri"
description = "NepaliLang automation and scripting application"

[dependencies]
anurodh = "0.1.0"
web = "0.1.0"
browser = "0.1.0"
automation = "0.1.0"
database = "0.1.0"
ganit = "0.1.0"
samaya = "0.1.0"
regex = "0.1.0"
"""
        )
    )

    fun loadFiles(): MutableList<ProjectFile> {
        val files = mutableListOf<ProjectFile>()
        for (defaultFile in defaultFiles) {
            val f = File(projectDir, defaultFile.name)
            if (!f.exists()) {
                f.writeText(defaultFile.content)
            }
            files.add(ProjectFile(name = defaultFile.name, content = f.readText()))
        }

        // Load any other user files in directory
        projectDir.listFiles()?.forEach { file ->
            if (files.none { it.name == file.name }) {
                files.add(ProjectFile(name = file.name, content = file.readText()))
            }
        }
        return files
    }

    fun saveFile(name: String, content: String) {
        val f = File(projectDir, name)
        f.writeText(content)
    }

    fun renameFile(oldName: String, newName: String): Boolean {
        val oldF = File(projectDir, oldName)
        val newF = File(projectDir, newName)
        if (!oldF.exists()) return false
        if (newF.exists() && oldName != newName) return false
        return oldF.renameTo(newF)
    }

    fun deleteFile(name: String): Boolean {
        val f = File(projectDir, name)
        return if (f.exists()) f.delete() else false
    }

    fun getProjectDirPath(): String = projectDir.absolutePath
}
