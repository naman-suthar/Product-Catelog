package app.naman.lumostest.ui.theme

import androidx.compose.runtime.compositionLocalOf

data class ThemeController(
    val isDark: Boolean,
    val palette: ThemePalette,
    val toggleDark: () -> Unit,
    val setPalette: (ThemePalette) -> Unit,
)

val LocalThemeController = compositionLocalOf<ThemeController> {
    error("No ThemeController provided")
}
