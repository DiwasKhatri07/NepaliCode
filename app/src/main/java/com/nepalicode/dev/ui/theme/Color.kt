package com.nepalicode.dev.ui.theme

import androidx.compose.ui.graphics.Color

// High Density Design Theme Canvas & Surfaces
val IdeBackground = Color(0xFF1C1B1F)       // Deep Obsidian / M3 Dark Background
val IdeSurface = Color(0xFF2B2930)          // M3 Dark Surface Container High / Headers & Bars
val IdeSurfaceVariant = Color(0xFF49454F)   // M3 Dark Outline / Chip active background
val IdeSurfaceContainer = Color(0xFF36343B)  // Intermediate surface container
val IdeBorder = Color(0xFF49454F)           // M3 Dark Outline Variant / Structural Dividers

// High Density Primary, Secondary & Accent Tokens
val HighDensityPrimary = Color(0xFFD0BCFF)  // M3 Lavender Accent (Keywords, Highlights, Run pill)
val HighDensityOnPrimary = Color(0xFF381E72)// Deep Purple (Text/Icon on Primary Run pill)
val HighDensityCyan = Color(0xFF77D8ED)     // Cyan (Function definitions, builtins, methods)
val HighDensityCoral = Color(0xFFF2B8B5)    // Soft Pink / Coral (Strings, modules, errors)
val HighDensityLavenderLight = Color(0xFFEADDFF) // Light lavender (Function names, expressions)

// Legacy Aliases for seamless module compatibility
val TechCyan = HighDensityCyan
val TechPurple = HighDensityPrimary
val TechMint = HighDensityCyan
val TechAmber = HighDensityLavenderLight
val NepaliCrimson = HighDensityCoral

// High Density Text Hierarchy
val TextPrimary = Color(0xFFE6E1E5)         // High contrast foreground
val TextSecondary = Color(0xFFCAC4D0)       // Medium contrast icon / label
val TextMuted = Color(0xFF938F99)           // Low contrast metadata & comments
val LineNumberColor = Color(0xFF49454F)     // Subtle high-density line number column

// High Density Terminal & Status
val TerminalBackground = Color(0xFF000000)  // Deep Black
val TerminalHeaderBackground = Color(0xFF1C1B1F) // Terminal header strip
val TerminalGreen = Color(0xFF4ADE80)       // High-density vibrant terminal green
val TerminalRed = Color(0xFFF2B8B5)         // Soft red / pink
val TerminalYellow = Color(0xFFFFD54F)      // Warm amber prompt
val TerminalCyan = Color(0xFF77D8ED)        // Terminal system cyan

