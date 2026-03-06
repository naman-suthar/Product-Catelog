package app.naman.lumostest.ui.theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// ── Theme palette presets ───────────────────────────────────────────────────

data class ThemePalette(
    val name: String,
    val lightPrimary: Color,
    val lightOnPrimary: Color = Color.White,
    val lightPrimaryContainer: Color,
    val lightOnPrimaryContainer: Color,
    val darkPrimary: Color,
    val darkOnPrimary: Color,
    val darkPrimaryContainer: Color,
    val darkOnPrimaryContainer: Color = Color.White,
)

val ThemePalettes = listOf(
    ThemePalette(
        name = "Purple",
        lightPrimary = Color(0xFF6650A4), lightOnPrimary = Color.White,
        lightPrimaryContainer = Color(0xFFEADDFF), lightOnPrimaryContainer = Color(0xFF21005D),
        darkPrimary = Color(0xFFD0BCFF), darkOnPrimary = Color(0xFF381E72),
        darkPrimaryContainer = Color(0xFF4F378B), darkOnPrimaryContainer = Color(0xFFEADDFF),
    ),
    ThemePalette(
        name = "Blue",
        lightPrimary = Color(0xFF1976D2), lightOnPrimary = Color.White,
        lightPrimaryContainer = Color(0xFFD1E4FF), lightOnPrimaryContainer = Color(0xFF001D36),
        darkPrimary = Color(0xFF9ECAFF), darkOnPrimary = Color(0xFF003258),
        darkPrimaryContainer = Color(0xFF004880), darkOnPrimaryContainer = Color(0xFFD1E4FF),
    ),
    ThemePalette(
        name = "Green",
        lightPrimary = Color(0xFF2E7D32), lightOnPrimary = Color.White,
        lightPrimaryContainer = Color(0xFFB7F0B1), lightOnPrimaryContainer = Color(0xFF002204),
        darkPrimary = Color(0xFF9CF598), darkOnPrimary = Color(0xFF003A08),
        darkPrimaryContainer = Color(0xFF14520F), darkOnPrimaryContainer = Color(0xFFB7F0B1),
    ),
    ThemePalette(
        name = "Orange",
        lightPrimary = Color(0xFFBF5000), lightOnPrimary = Color.White,
        lightPrimaryContainer = Color(0xFFFFDBCC), lightOnPrimaryContainer = Color(0xFF390C00),
        darkPrimary = Color(0xFFFFB68E), darkOnPrimary = Color(0xFF602200),
        darkPrimaryContainer = Color(0xFF893800), darkOnPrimaryContainer = Color(0xFFFFDBCC),
    ),
    ThemePalette(
        name = "Red",
        lightPrimary = Color(0xFFBA1A1A), lightOnPrimary = Color.White,
        lightPrimaryContainer = Color(0xFFFFDAD6), lightOnPrimaryContainer = Color(0xFF410002),
        darkPrimary = Color(0xFFFFB4AB), darkOnPrimary = Color(0xFF690005),
        darkPrimaryContainer = Color(0xFF93000A), darkOnPrimaryContainer = Color(0xFFFFDAD6),
    ),
    ThemePalette(
        name = "Teal",
        lightPrimary = Color(0xFF006A60), lightOnPrimary = Color.White,
        lightPrimaryContainer = Color(0xFF9EF2E4), lightOnPrimaryContainer = Color(0xFF00201C),
        darkPrimary = Color(0xFF82D5C8), darkOnPrimary = Color(0xFF003731),
        darkPrimaryContainer = Color(0xFF004F47), darkOnPrimaryContainer = Color(0xFF9EF2E4),
    ),
    ThemePalette(
        name = "Pink",
        lightPrimary = Color(0xFFAD1457), lightOnPrimary = Color.White,
        lightPrimaryContainer = Color(0xFFFFD8E7), lightOnPrimaryContainer = Color(0xFF3E0020),
        darkPrimary = Color(0xFFFFB1C8), darkOnPrimary = Color(0xFF650033),
        darkPrimaryContainer = Color(0xFF8E004A), darkOnPrimaryContainer = Color(0xFFFFD8E7),
    ),
    ThemePalette(
        name = "Brown",
        lightPrimary = Color(0xFF6D4C41), lightOnPrimary = Color.White,
        lightPrimaryContainer = Color(0xFFFFDBD1), lightOnPrimaryContainer = Color(0xFF31110C),
        darkPrimary = Color(0xFFFFB4A0), darkOnPrimary = Color(0xFF561F17),
        darkPrimaryContainer = Color(0xFF73342A), darkOnPrimaryContainer = Color(0xFFFFDBD1),
    ),
)
