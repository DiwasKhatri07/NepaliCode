package com.nepalicode.dev.nepalicode.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

data class KeywordPair(
    val nepali: String,
    val english: String,
    val usage: String
)

@Composable
fun ReferenceScreen(
    onLoadSnippet: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val keywordPairs = listOf(
        KeywordPair("kaam", "def", "Function declaration: kaam jod(a, b):"),
        KeywordPair("firta", "return", "Return value from function: firta a + b"),
        KeywordPair("yedi", "if", "Conditional branching: yedi umar >= 18:"),
        KeywordPair("natra", "else", "Else branch: natra:"),
        KeywordPair("natabhaye", "elif", "Else if branch: natabhaye umar > 12:"),
        KeywordPair("ko_lagi", "for", "For loop: ko_lagi i ma 10:"),
        KeywordPair("jabasamma", "while", "While loop: jabasamma a < 10:"),
        KeywordPair("lyau", "import", "Import module: lyau anurodh"),
        KeywordPair("bata", "from", "From import: bata anurodh lyau get"),
        KeywordPair("koshish", "try", "Try block: koshish:"),
        KeywordPair("samau", "except", "Except block: samau error:"),
        KeywordPair("antya", "finally", "Finally block: antya:"),
        KeywordPair("sacho", "True", "Boolean true literal"),
        KeywordPair("jhut", "False", "Boolean false literal"),
        KeywordPair("khali", "None", "Null/None empty value"),
        KeywordPair("dekha()", "print()", "Output display to terminal"),
        KeywordPair("leu()", "input()", "Take user terminal input"),
        KeywordPair("yo()", "constructor", "Convenience constructor: a = yo(3)"),
        KeywordPair("lambai()", "len()", "Length of string/list/map"),
        KeywordPair("daura()", "range()", "Generate number sequence")
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(IdeBackground)
    ) {
        // Header
        Surface(
            color = IdeSurface,
            tonalElevation = 0.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(IdeSurfaceVariant, RoundedCornerShape(8.dp))
                        .border(0.5.dp, IdeBorder, RoundedCornerShape(8.dp))
                        .padding(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = null,
                        tint = HighDensityPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "NepaliLang Syntax Reference 🇳🇵",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Created by Diwas Khatri (NepaliSource)",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }
        }

        HorizontalDivider(color = IdeBorder, thickness = 1.dp)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("reference_dev_credits_card"),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = IdeSurface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(HighDensityPrimary.copy(alpha = 0.5f)))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🇳🇵", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Lead Developer: Diwas Khatri",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HighDensityPrimary
                                )
                                Text(
                                    text = "diwaskhatri935@gmail.com • NepaliLang (.np) v2.4.0",
                                    fontSize = 11.sp,
                                    color = HighDensityCyan,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Specialization: High-density mobile IDE, compiler & tokenizer, dual Nepali/English syntax, HTTP Anurodh, browser automation & SQLite engine.",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = IdeSurfaceVariant),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(HighDensityPrimary.copy(alpha = 0.3f)))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Core Language Philosophy",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = HighDensityPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "\"Programming should be simple enough that a Nepali beginner can understand what the code is doing.\"",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Supports both English keywords and Nepali-friendly aliases, plus first-class Unicode identifiers (नाम = \"दिवस\").",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Dual Keyword Mappings:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }

            items(keywordPairs) { pair ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = IdeSurface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(IdeBorder))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = pair.nepali,
                                    color = HighDensityCoral,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.Translate,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = pair.english,
                                    color = HighDensityCyan,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = pair.usage,
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}
