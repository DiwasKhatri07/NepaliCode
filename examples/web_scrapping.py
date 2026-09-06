# 🕷️ Web Scraping & Data Extraction with NepaliLang
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
