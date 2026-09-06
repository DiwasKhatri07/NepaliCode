package com.nepalicode.dev.nepalicode.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NepaliCodeBadgeLogo(
    modifier: Modifier = Modifier,
    size: Dp = 32.dp
) {
    // Distinctive NepaliCode Emblem: Crimson red and navy gradient with Nepali flag double-triangle inspiration
    val crimson = Color(0xFFDC143C)
    val himalayanNavy = Color(0xFF003893)
    val neonAccent = Color(0xFF00E676)

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.linearGradient(
                    listOf(crimson, himalayanNavy)
                )
            )
            .border(1.dp, neonAccent.copy(alpha = 0.6f), RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "🇳🇵",
                fontSize = (size.value * 0.45f).sp
            )
        }
    }
}

@Composable
fun NepaliCodeFullLogo(
    modifier: Modifier = Modifier,
    isDark: Boolean = true
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NepaliCodeBadgeLogo(size = 30.dp)
        Spacer(modifier = Modifier.width(8.dp))
        Box {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Nepali",
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = if (isDark) Color(0xFFFFFFFF) else Color(0xFF1E293B),
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Code",
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = Color(0xFF00E676),
                    letterSpacing = (-0.5).sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFDC143C).copy(alpha = 0.2f))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = ".np",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF5252),
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
