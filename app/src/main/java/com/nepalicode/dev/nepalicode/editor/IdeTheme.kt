package com.nepalicode.dev.nepalicode.editor

import androidx.compose.ui.graphics.Color

enum class IdeThemeMode(val title: String, val description: String) {
    HIGH_DENSITY_DARK("High Density Dark", "Lavender, Cyan, Coral on deep obsidian"),
    CLASSIC_DARK("Classic Dark", "Emerald, Cobalt, Amber on jet black"),
    SOLARIZED_DARK("Solarized Dark", "Cyan, Yellow, Magenta on deep teal navy"),
    MONOKAI_PRO("Monokai Pro", "Hot Pink, Lime, Amber on warm charcoal"),
    CYBERPUNK_NEON("Cyberpunk Neon", "Electric Purple, Neon Cyan, Hot Pink on deep violet"),
    LIGHT_MODE("Daylight Clean", "Crisp high-contrast daylight theme with bold blues"),
    NEPALI_SUNSET("Nepali Sunset", "Himalayan Crimson, Amber Gold, Coral on maroon")
}

data class IdeThemePalette(
    val mode: IdeThemeMode,
    val isDark: Boolean,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val border: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val lineNumberColor: Color,
    val primaryAccent: Color,
    val onPrimaryAccent: Color,
    val secondaryAccent: Color,
    val keywordColor: Color,
    val defKeywordColor: Color,
    val stringColor: Color,
    val numberColor: Color,
    val commentColor: Color,
    val builtinColor: Color,
    val stdLibColor: Color,
    val operatorColor: Color,
    val errorUnderlineColor: Color,
    val errorBackgroundColor: Color,
    val activeLineBackground: Color
)

object IdeThemeRegistry {

    val HighDensityDark = IdeThemePalette(
        mode = IdeThemeMode.HIGH_DENSITY_DARK,
        isDark = true,
        background = Color(0xFF141218),
        surface = Color(0xFF2B2930),
        surfaceVariant = Color(0xFF49454F),
        border = Color(0xFF49454F),
        textPrimary = Color(0xFFE6E1E5),
        textSecondary = Color(0xFFCAC4D0),
        textMuted = Color(0xFF938F99),
        lineNumberColor = Color(0xFF635F6A),
        primaryAccent = Color(0xFFD0BCFF),
        onPrimaryAccent = Color(0xFF381E72),
        secondaryAccent = Color(0xFF77D8ED),
        keywordColor = Color(0xFFD0BCFF),
        defKeywordColor = Color(0xFF77D8ED),
        stringColor = Color(0xFFF2B8B5),
        numberColor = Color(0xFF80D4FF),
        commentColor = Color(0xFF938F99),
        builtinColor = Color(0xFF77D8ED),
        stdLibColor = Color(0xFFEADDFF),
        operatorColor = Color(0xFFCAC4D0),
        errorUnderlineColor = Color(0xFFF2B8B5),
        errorBackgroundColor = Color(0x3DF2B8B5),
        activeLineBackground = Color(0x1AD0BCFF)
    )

    val ClassicDark = IdeThemePalette(
        mode = IdeThemeMode.CLASSIC_DARK,
        isDark = true,
        background = Color(0xFF121212),
        surface = Color(0xFF1E1E1E),
        surfaceVariant = Color(0xFF2D2D2D),
        border = Color(0xFF383838),
        textPrimary = Color(0xFFF0F0F0),
        textSecondary = Color(0xFFCCCCCC),
        textMuted = Color(0xFF888888),
        lineNumberColor = Color(0xFF555555),
        primaryAccent = Color(0xFF38BDF8),
        onPrimaryAccent = Color(0xFF0C4A6E),
        secondaryAccent = Color(0xFF34D399),
        keywordColor = Color(0xFF60A5FA),
        defKeywordColor = Color(0xFF38BDF8),
        stringColor = Color(0xFF34D399),
        numberColor = Color(0xFFFBBF24),
        commentColor = Color(0xFF6B7280),
        builtinColor = Color(0xFFA78BFA),
        stdLibColor = Color(0xFF38BDF8),
        operatorColor = Color(0xFFE5E7EB),
        errorUnderlineColor = Color(0xFFEF4444),
        errorBackgroundColor = Color(0x3DEF4444),
        activeLineBackground = Color(0x1A38BDF8)
    )

    val SolarizedDark = IdeThemePalette(
        mode = IdeThemeMode.SOLARIZED_DARK,
        isDark = true,
        background = Color(0xFF002B36),
        surface = Color(0xFF073642),
        surfaceVariant = Color(0xFF0B4654),
        border = Color(0xFF145465),
        textPrimary = Color(0xFF839496),
        textSecondary = Color(0xFF93A1A1),
        textMuted = Color(0xFF586E75),
        lineNumberColor = Color(0xFF586E75),
        primaryAccent = Color(0xFF2AA198),
        onPrimaryAccent = Color(0xFF002B36),
        secondaryAccent = Color(0xFF268BD2),
        keywordColor = Color(0xFF859900),
        defKeywordColor = Color(0xFF268BD2),
        stringColor = Color(0xFF2AA198),
        numberColor = Color(0xFFD33682),
        commentColor = Color(0xFF586E75),
        builtinColor = Color(0xFFB58900),
        stdLibColor = Color(0xFFCB4B16),
        operatorColor = Color(0xFF93A1A1),
        errorUnderlineColor = Color(0xFFDC322F),
        errorBackgroundColor = Color(0x3DDC322F),
        activeLineBackground = Color(0x24073642)
    )

    val MonokaiPro = IdeThemePalette(
        mode = IdeThemeMode.MONOKAI_PRO,
        isDark = true,
        background = Color(0xFF221F22),
        surface = Color(0xFF2D2A2E),
        surfaceVariant = Color(0xFF403E41),
        border = Color(0xFF4D4A4F),
        textPrimary = Color(0xFFFCFCFA),
        textSecondary = Color(0xFFC1C0C0),
        textMuted = Color(0xFF727072),
        lineNumberColor = Color(0xFF727072),
        primaryAccent = Color(0xFFFF6188),
        onPrimaryAccent = Color(0xFF221F22),
        secondaryAccent = Color(0xFFA9DC76),
        keywordColor = Color(0xFFFF6188),
        defKeywordColor = Color(0xFF78DCE8),
        stringColor = Color(0xFFFFD866),
        numberColor = Color(0xFFAB9DF2),
        commentColor = Color(0xFF727072),
        builtinColor = Color(0xFF78DCE8),
        stdLibColor = Color(0xFFA9DC76),
        operatorColor = Color(0xFFFF6188),
        errorUnderlineColor = Color(0xFFFF6188),
        errorBackgroundColor = Color(0x3DFF6188),
        activeLineBackground = Color(0x26FF6188)
    )

    val CyberpunkNeon = IdeThemePalette(
        mode = IdeThemeMode.CYBERPUNK_NEON,
        isDark = true,
        background = Color(0xFF0E071D),
        surface = Color(0xFF1B0F33),
        surfaceVariant = Color(0xFF2E1A52),
        border = Color(0xFF4C2A85),
        textPrimary = Color(0xFFF3E8FF),
        textSecondary = Color(0xFFD8B4FE),
        textMuted = Color(0xFF9373B8),
        lineNumberColor = Color(0xFF6B4E8C),
        primaryAccent = Color(0xFFE879F9),
        onPrimaryAccent = Color(0xFF2E0854),
        secondaryAccent = Color(0xFF22D3EE),
        keywordColor = Color(0xFFE879F9),
        defKeywordColor = Color(0xFF22D3EE),
        stringColor = Color(0xFFF472B6),
        numberColor = Color(0xFFFACC15),
        commentColor = Color(0xFF7E60A8),
        builtinColor = Color(0xFF38BDF8),
        stdLibColor = Color(0xFFC084FC),
        operatorColor = Color(0xFFF43F5E),
        errorUnderlineColor = Color(0xFFF43F5E),
        errorBackgroundColor = Color(0x40F43F5E),
        activeLineBackground = Color(0x28E879F9)
    )

    val LightMode = IdeThemePalette(
        mode = IdeThemeMode.LIGHT_MODE,
        isDark = false,
        background = Color(0xFFF8F9FA),
        surface = Color(0xFFFFFFFF),
        surfaceVariant = Color(0xFFE9ECEF),
        border = Color(0xFFCED4DA),
        textPrimary = Color(0xFF212529),
        textSecondary = Color(0xFF495057),
        textMuted = Color(0xFF868E96),
        lineNumberColor = Color(0xFFADB5BD),
        primaryAccent = Color(0xFF1D4ED8),
        onPrimaryAccent = Color(0xFFFFFFFF),
        secondaryAccent = Color(0xFF0891B2),
        keywordColor = Color(0xFF7C3AED),
        defKeywordColor = Color(0xFF1D4ED8),
        stringColor = Color(0xFF059669),
        numberColor = Color(0xFFD97706),
        commentColor = Color(0xFF6B7280),
        builtinColor = Color(0xFF0284C7),
        stdLibColor = Color(0xFF4338CA),
        operatorColor = Color(0xFF374151),
        errorUnderlineColor = Color(0xFFDC2626),
        errorBackgroundColor = Color(0x26DC2626),
        activeLineBackground = Color(0x1A1D4ED8)
    )

    val NepaliSunset = IdeThemePalette(
        mode = IdeThemeMode.NEPALI_SUNSET,
        isDark = true,
        background = Color(0xFF1E0A0E),
        surface = Color(0xFF2D1117),
        surfaceVariant = Color(0xFF471C26),
        border = Color(0xFF5E2734),
        textPrimary = Color(0xFFFFF1F2),
        textSecondary = Color(0xFFFECDD3),
        textMuted = Color(0xFF9E6B77),
        lineNumberColor = Color(0xFF7A4A55),
        primaryAccent = Color(0xFFF43F5E),
        onPrimaryAccent = Color(0xFF4C0519),
        secondaryAccent = Color(0xFFF59E0B),
        keywordColor = Color(0xFFFB7185),
        defKeywordColor = Color(0xFFF59E0B),
        stringColor = Color(0xFF34D399),
        numberColor = Color(0xFFFBBF24),
        commentColor = Color(0xFF9E6B77),
        builtinColor = Color(0xFFF43F5E),
        stdLibColor = Color(0xFFFBBF24),
        operatorColor = Color(0xFFFDA4AF),
        errorUnderlineColor = Color(0xFFE11D48),
        errorBackgroundColor = Color(0x3DE11D48),
        activeLineBackground = Color(0x28F43F5E)
    )

    fun getPalette(mode: IdeThemeMode): IdeThemePalette {
        return when (mode) {
            IdeThemeMode.HIGH_DENSITY_DARK -> HighDensityDark
            IdeThemeMode.CLASSIC_DARK -> ClassicDark
            IdeThemeMode.SOLARIZED_DARK -> SolarizedDark
            IdeThemeMode.MONOKAI_PRO -> MonokaiPro
            IdeThemeMode.CYBERPUNK_NEON -> CyberpunkNeon
            IdeThemeMode.LIGHT_MODE -> LightMode
            IdeThemeMode.NEPALI_SUNSET -> NepaliSunset
        }
    }
}
