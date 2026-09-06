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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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

data class NppmPackage(
    val name: String,
    val version: String,
    val description: String,
    val isInstalled: Boolean,
    val isCore: Boolean = true
)

@Composable
fun PackageManagerScreen(
    modifier: Modifier = Modifier
) {
    var packages by remember {
        mutableStateOf(
            listOf(
                NppmPackage("anurodh", "0.1.0", "HTTP Requests (GET, POST, JSON) for NepaliLang", true),
                NppmPackage("web", "0.1.0", "Web browser launcher and page extractor", true),
                NppmPackage("browser", "0.1.0", "Browser automation, clicking, and typing engine", true),
                NppmPackage("automation", "0.1.0", "Desktop, mouse, keyboard & clipboard automation", true),
                NppmPackage("database", "0.1.0", "SQLite local database management", true),
                NppmPackage("ganit", "0.1.0", "Mathematics operations, algebra, trigonometry", true),
                NppmPackage("samaya", "0.1.0", "Date, time formatting, and delay timers", true),
                NppmPackage("json", "0.1.0", "JSON parser, serializer and file utilities", true),
                NppmPackage("random", "0.1.0", "Random numbers, list shuffling and selection", true),
                NppmPackage("file", "0.1.0", "Virtual and physical file I/O operations", true),
                NppmPackage("folder", "0.1.0", "Directory creation, listing and deletion", true),
                NppmPackage("system", "0.1.0", "Platform, OS, and hardware environment info", true),
                NppmPackage("log", "0.1.0", "Structured logging with info, warning, error", true),
                NppmPackage("server", "0.2.0-preview", "Micro web server framework for APIs", false, false),
                NppmPackage("ai-connect", "0.2.0-preview", "DeepMind Gemini & LLM connector for .np", false, false)
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(IdeBackground)
    ) {
        // NPPM Header Bar
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(IdeSurfaceVariant, RoundedCornerShape(8.dp))
                            .border(0.5.dp, IdeBorder, RoundedCornerShape(8.dp))
                            .padding(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Inventory2,
                            contentDescription = null,
                            tint = HighDensityPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "NPPM — Nepali Package Manager",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Managing dependencies defined in nepali.toml",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        }

        HorizontalDivider(color = IdeBorder, thickness = 1.dp)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(packages) { pkg ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("package_${pkg.name}"),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = IdeSurface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(IdeBorder))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = pkg.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = HighDensityPrimary,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .background(IdeSurfaceVariant, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "v${pkg.version}",
                                        fontSize = 10.sp,
                                        color = TextSecondary,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                if (pkg.isCore) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .background(HighDensityCyan.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "StdLib",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = HighDensityCyan
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = pkg.description,
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        if (pkg.isInstalled) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .background(HighDensityCyan.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = HighDensityCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Installed",
                                    fontSize = 11.sp,
                                    color = HighDensityCyan,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        } else {
                            Button(
                                onClick = {
                                    packages = packages.map {
                                        if (it.name == pkg.name) it.copy(isInstalled = true) else it
                                    }
                                },
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = HighDensityPrimary,
                                    contentColor = HighDensityOnPrimary
                                )
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp), tint = HighDensityOnPrimary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Install", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
