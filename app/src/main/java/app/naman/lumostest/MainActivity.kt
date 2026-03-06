package app.naman.lumostest

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import app.naman.lumostest.ui.navigation.AppNavGraph
import app.naman.lumostest.ui.theme.LocalThemeController
import app.naman.lumostest.ui.theme.LumosTestTheme
import app.naman.lumostest.ui.theme.ThemeController
import app.naman.lumostest.ui.theme.ThemePalettes
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val prefs = getSharedPreferences("lumos_theme", Context.MODE_PRIVATE)

        setContent {
            var isDark by remember {
                mutableStateOf(prefs.getBoolean("dark", false))
            }
            var palette by remember {
                mutableStateOf(
                    ThemePalettes.find { it.name == prefs.getString("palette", null) }
                        ?: ThemePalettes[0]
                )
            }

            val controller = ThemeController(
                isDark = isDark,
                palette = palette,
                toggleDark = {
                    val next = !isDark
                    isDark = next
                    prefs.edit().putBoolean("dark", next).apply()
                },
                setPalette = { newPalette ->
                    palette = newPalette
                    prefs.edit().putString("palette", newPalette.name).apply()
                },
            )

            CompositionLocalProvider(LocalThemeController provides controller) {
                LumosTestTheme(darkTheme = isDark, palette = palette) {
                    AppNavGraph()
                }
            }
        }
    }
}
