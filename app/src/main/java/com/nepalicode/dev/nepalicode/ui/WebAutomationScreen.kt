package com.nepalicode.dev.nepalicode.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Http
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nepalicode.dev.ui.theme.HighDensityCoral
import com.nepalicode.dev.ui.theme.HighDensityCyan
import com.nepalicode.dev.ui.theme.HighDensityOnPrimary
import com.nepalicode.dev.ui.theme.HighDensityPrimary
import com.nepalicode.dev.ui.theme.IdeBackground
import com.nepalicode.dev.ui.theme.IdeBorder
import com.nepalicode.dev.ui.theme.IdeSurface
import com.nepalicode.dev.ui.theme.IdeSurfaceVariant
import com.nepalicode.dev.ui.theme.NepaliCrimson
import com.nepalicode.dev.ui.theme.TechCyan
import com.nepalicode.dev.ui.theme.TechMint
import com.nepalicode.dev.ui.theme.TextMuted
import com.nepalicode.dev.ui.theme.TextPrimary
import com.nepalicode.dev.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WebAutomationScreen(
    currentUrl: String,
    webPageTitle: String,
    automationLogs: List<AutomationLogItem>,
    onTriggerWebOpen: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var urlInput by remember(currentUrl) { mutableStateOf(currentUrl) }
    var selectedSubTab by remember { mutableIntStateOf(0) } // 0: Web View & Inspector, 1: Automation Timeline

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(IdeBackground)
    ) {
        // Browser URL Address Bar
        Surface(
            color = IdeSurface,
            tonalElevation = 0.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .background(IdeSurfaceVariant, RoundedCornerShape(8.dp))
                            .border(0.5.dp, IdeBorder, RoundedCornerShape(8.dp))
                            .padding(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Web",
                            tint = HighDensityPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedTextField(
                        value = urlInput,
                        onValueChange = { urlInput = it },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("browser_address_bar"),
                        singleLine = true,
                        placeholder = { Text("https://example.com", fontSize = 12.sp, color = TextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HighDensityPrimary,
                            unfocusedBorderColor = IdeBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = { onTriggerWebOpen(urlInput) },
                        modifier = Modifier
                            .size(42.dp)
                            .background(HighDensityPrimary, RoundedCornerShape(8.dp))
                            .testTag("browser_go_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Navigate",
                            tint = HighDensityOnPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Sub-tabs: Web Inspector vs Web Extractor vs Automation Timeline
                TabRow(
                    selectedTabIndex = selectedSubTab,
                    containerColor = IdeSurfaceVariant,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedSubTab]),
                            color = HighDensityPrimary,
                            height = 2.dp
                        )
                    }
                ) {
                    Tab(
                        selected = selectedSubTab == 0,
                        onClick = { selectedSubTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(14.dp), tint = if (selectedSubTab == 0) HighDensityPrimary else TextMuted)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Inspector", fontSize = 11.sp, fontWeight = if (selectedSubTab == 0) FontWeight.Bold else FontWeight.Normal, color = if (selectedSubTab == 0) HighDensityPrimary else TextMuted)
                            }
                        }
                    )
                    Tab(
                        selected = selectedSubTab == 1,
                        onClick = { selectedSubTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Layers, contentDescription = null, modifier = Modifier.size(14.dp), tint = if (selectedSubTab == 1) HighDensityCyan else TextMuted)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Extractor", fontSize = 11.sp, fontWeight = if (selectedSubTab == 1) FontWeight.Bold else FontWeight.Normal, color = if (selectedSubTab == 1) HighDensityCyan else TextMuted)
                            }
                        }
                    )
                    Tab(
                        selected = selectedSubTab == 2,
                        onClick = { selectedSubTab = 2 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.SmartToy, contentDescription = null, modifier = Modifier.size(14.dp), tint = if (selectedSubTab == 2) HighDensityCoral else TextMuted)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Log (${automationLogs.size})", fontSize = 11.sp, fontWeight = if (selectedSubTab == 2) FontWeight.Bold else FontWeight.Normal, color = if (selectedSubTab == 2) HighDensityCoral else TextMuted)
                            }
                        }
                    )
                }
            }
        }

        HorizontalDivider(color = IdeBorder, thickness = 1.dp)

        when (selectedSubTab) {
            0 -> {
                // Web Inspector & Live Browser Simulation
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = IdeSurface),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(IdeBorder))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .background(Color(0xFF22C55E), CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "HTTP 200 OK • Live Active",
                                        fontSize = 11.sp,
                                        color = Color(0xFF22C55E),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Text(
                                    text = "Engine: anurodh & web",
                                    fontSize = 10.sp,
                                    color = TextMuted,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "URL: $currentUrl",
                                fontSize = 12.sp,
                                color = TechCyan,
                                fontFamily = FontFamily.Monospace
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Title: $webPageTitle",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )

                            HorizontalDivider(
                                color = IdeBorder,
                                thickness = 1.dp,
                                modifier = Modifier.padding(vertical = 10.dp)
                            )

                            Text(
                                text = "Simulated Web DOM & Document Tree:",
                                fontSize = 11.sp,
                                color = TextMuted,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(IdeBackground, RoundedCornerShape(8.dp))
                                    .border(1.dp, IdeBorder, RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Text("<!DOCTYPE html>", color = TextMuted, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                    Text("<html>", color = TechCyan, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                    Text("  <head>", color = TechCyan, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                    Text("    <title>$webPageTitle</title>", color = TextPrimary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                    Text("  </head>", color = TechCyan, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                    Text("  <body>", color = TechCyan, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                    Text("    <h1>Namaste from $currentUrl</h1>", color = TextPrimary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                    Text("    <p id=\"status\">NepaliLang web engine connected successfully.</p>", color = TechMint, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                    Text("    <a href=\"https://nepalilang.org/docs\">NepaliLang Documentation</a>", color = HighDensityPrimary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                    Text("    <button id=\"submit\">Submit</button>", color = NepaliCrimson, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                    Text("  </body>", color = TechCyan, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                    Text("</html>", color = TechCyan, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                }
                            }
                        }
                    }
                }
            }
            1 -> {
                // Web Extractor & Scraper Tester
                WebExtractorTab(currentUrl = currentUrl, webPageTitle = webPageTitle)
            }
            else -> {
                // Automation Action Timeline
                if (automationLogs.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.SmartToy,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No automation events yet",
                                color = TextSecondary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Run web_automation.np, web_scraping_news.np or desktop_automation.np",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(automationLogs.reversed()) { log ->
                            AutomationTimelineCard(log)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WebExtractorTab(
    currentUrl: String,
    webPageTitle: String
) {
    var selector by remember { mutableStateOf("h1") }
    val sampleHtml = """
        <!DOCTYPE html>
        <html>
        <head><title>$webPageTitle</title></head>
        <body>
            <h1>Namaste from $currentUrl</h1>
            <p id="status">NepaliLang web engine connected successfully.</p>
            <p class="description">Fast, light mobile-first automation language for Nepal.</p>
            <a href="https://nepalilang.org/docs">NepaliLang Official Docs</a>
            <a href="https://github.com/diwaskhatri">GitHub Diwas Khatri</a>
            <button id="submit">Submit Action</button>
        </body>
        </html>
    """.trimIndent()

    val extractedItems = remember(selector, sampleHtml) {
        extractMockHtmlElements(sampleHtml, selector)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = IdeSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(IdeBorder))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "CSS Selector Extractor",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = HighDensityCyan
                )
                Text(
                    text = "Test page.extract(selector) and extract_links() on DOM",
                    fontSize = 11.sp,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = selector,
                    onValueChange = { selector = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("CSS Selector", fontSize = 12.sp) },
                    placeholder = { Text("e.g. h1, p, a, button, #status", fontSize = 12.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HighDensityCyan,
                        unfocusedBorderColor = IdeBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Selector Presets
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Presets:", fontSize = 11.sp, color = TextMuted)
                    listOf("h1", "p", "a", "button", "#status", "title").forEach { preset ->
                        Surface(
                            color = if (selector == preset) HighDensityCyan.copy(alpha = 0.2f) else IdeSurfaceVariant,
                            shape = RoundedCornerShape(6.dp),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(if (selector == preset) HighDensityCyan else IdeBorder)
                            ),
                            modifier = Modifier
                                .clickable { selector = preset }
                                .padding(horizontal = 2.dp)
                        ) {
                            Text(
                                text = preset,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = if (selector == preset) HighDensityCyan else TextSecondary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Extraction Results
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = IdeSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(IdeBorder))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Extracted Elements (${extractedItems.size} matches)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Nepali: page.extract(\"$selector\")",
                        fontSize = 11.sp,
                        color = HighDensityPrimary,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (extractedItems.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No elements matching selector '$selector'",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(extractedItems) { item ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(IdeBackground, RoundedCornerShape(6.dp))
                                    .border(1.dp, IdeBorder, RoundedCornerShape(6.dp))
                                    .padding(8.dp)
                            ) {
                                Column {
                                    Text(
                                        text = item,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = TechMint
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun extractMockHtmlElements(html: String, selector: String): List<String> {
    val cleanSel = selector.trim()
    if (cleanSel.isEmpty()) return emptyList()

    return try {
        when {
            cleanSel.startsWith("#") -> {
                val id = cleanSel.removePrefix("#")
                val pattern = Regex("<([a-zA-Z0-9]+)[^>]*id=[\"']$id[\"'][^>]*>(.*?)</\\1>", RegexOption.DOT_MATCHES_ALL)
                pattern.findAll(html).map { it.groupValues[2].trim() }.toList()
            }
            cleanSel.startsWith(".") -> {
                val className = cleanSel.removePrefix(".")
                val pattern = Regex("<([a-zA-Z0-9]+)[^>]*class=[\"'][^\"']*$className[^\"']*[\"'][^>]*>(.*?)</\\1>", RegexOption.DOT_MATCHES_ALL)
                pattern.findAll(html).map { it.groupValues[2].trim() }.toList()
            }
            cleanSel == "a" -> {
                val pattern = Regex("<a[^>]*href=[\"'](.*?)[\"'][^>]*>(.*?)</a>", RegexOption.DOT_MATCHES_ALL)
                pattern.findAll(html).map { "[${it.groupValues[2].trim()}] -> ${it.groupValues[1]}" }.toList()
            }
            else -> {
                val pattern = Regex("<$cleanSel\\b[^>]*>(.*?)</$cleanSel>", RegexOption.DOT_MATCHES_ALL)
                pattern.findAll(html).map { it.groupValues[1].trim() }.toList()
            }
        }
    } catch (_: Exception) {
        emptyList()
    }
}


@Composable
fun AutomationTimelineCard(log: AutomationLogItem) {
    val sdf = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }
    val timeStr = sdf.format(Date(log.timestamp))

    val icon = when {
        log.action.contains("HTTP", ignoreCase = true) -> Icons.Default.Http
        log.action.contains("Browser", ignoreCase = true) || log.action.contains("Web", ignoreCase = true) -> Icons.Default.Language
        else -> Icons.Default.SmartToy
    }

    val tagColor = when {
        log.action.contains("HTTP") -> HighDensityCyan
        log.action.contains("Click") -> HighDensityCoral
        log.action.contains("Type") -> HighDensityPrimary
        else -> HighDensityPrimary
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = IdeSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(IdeBorder))
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .background(tagColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tagColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = log.action,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = tagColor
                    )
                    Text(
                        text = timeStr,
                        fontSize = 10.sp,
                        color = TextMuted,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = log.details,
                    fontSize = 12.sp,
                    color = TextPrimary,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
