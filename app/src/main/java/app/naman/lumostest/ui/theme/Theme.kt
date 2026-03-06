package app.naman.lumostest.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun LumosTestTheme(
    darkTheme: Boolean,
    palette: ThemePalette,
    content: @Composable () -> Unit
) {
    val base = if (darkTheme) DarkColorScheme else LightColorScheme
    val colorScheme = if (darkTheme) {
        base.copy(
            primary = palette.darkPrimary,
            onPrimary = palette.darkOnPrimary,
            primaryContainer = palette.darkPrimaryContainer,
            onPrimaryContainer = palette.darkOnPrimaryContainer,
        )
    } else {
        base.copy(
            primary = palette.lightPrimary,
            onPrimary = palette.lightOnPrimary,
            primaryContainer = palette.lightPrimaryContainer,
            onPrimaryContainer = palette.lightOnPrimaryContainer,
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
