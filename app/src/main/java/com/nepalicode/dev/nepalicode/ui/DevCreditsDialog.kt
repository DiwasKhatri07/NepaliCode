package com.nepalicode.dev.nepalicode.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nepalicode.dev.nepalicode.editor.IdeThemePalette

@Composable
fun DevCreditsDialog(
    palette: IdeThemePalette,
    onDismiss: () -> Unit,
    onInsertCreditsSnippet: (() -> Unit)? = null
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("dev_credits_dialog"),
        containerColor = palette.surface,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(palette.primaryAccent.copy(alpha = 0.2f), CircleShape)
                        .border(1.dp, palette.primaryAccent, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🇳🇵", fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Diwas Khatri",
                            color = palette.textPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified Creator",
                            tint = palette.primaryAccent,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = "Creator of NepaliLang & NepaliCode IDE",
                        color = palette.textSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = palette.background),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(palette.border)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "NepaliLang (.np) Architecture",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.primaryAccent
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• Custom Tokenizer & Color-Coded Syntax Highlighting\n" +
                                   "• CodeTextField with Error Underlines & Gutter Indicators\n" +
                                   "• Nepali (.np) & Dual English Keyword Runtime\n" +
                                   "• Builtin Anurodh HTTP & Browser Web Automation\n" +
                                   "• SQLite Database, Filesystem & NPPM Packages\n" +
                                   "• Real-Time Linter & Auto-Completion Suggestion Engine",
                            fontSize = 11.sp,
                            color = palette.textSecondary,
                            lineHeight = 17.sp
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(palette.surfaceVariant, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Contact",
                        tint = palette.secondaryAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "diwaskhatri935@gmail.com",
                        color = palette.textPrimary,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Developer Email", "diwaskhatri935@gmail.com"))
                            Toast.makeText(context, "Email copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.height(28.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                    ) {
                        Text("Copy", fontSize = 10.sp, color = palette.primaryAccent)
                    }
                }

                Text(
                    text = "NepaliLang Version 2.4.0 • Designed for high productivity and accessible computing in Nepali and English.",
                    fontSize = 10.sp,
                    color = palette.textMuted
                )
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (onInsertCreditsSnippet != null) {
                    OutlinedButton(
                        onClick = {
                            onInsertCreditsSnippet()
                            onDismiss()
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(14.dp), tint = palette.primaryAccent)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Insert Code", fontSize = 12.sp, color = palette.primaryAccent)
                    }
                }
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = palette.primaryAccent,
                        contentColor = palette.onPrimaryAccent
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Close", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    )
}
