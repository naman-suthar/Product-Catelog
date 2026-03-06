package app.naman.lumostest.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import app.naman.lumostest.ui.detail.ProductDetailScreen
import app.naman.lumostest.ui.list.ProductListScreen
import app.naman.lumostest.ui.splash.SplashScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(
                onSplashComplete = {
                    navController.navigate("products") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }
        composable("products") {
            ProductListScreen(
                onProductClick = { id -> navController.navigate("products/$id") }
            )
        }
        composable(
            route = "products/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) {
            // Guard against double-tap / double-swipe popping past the list screen
            var navigating by remember { mutableStateOf(false) }

            fun goBack() {
                if (!navigating) {
                    navigating = true
                    navController.popBackStack()
                }
            }

            // Intercept system back gesture with the same guard.
            // ProductDetailScreen's own BackHandler (for the image viewer) is composed
            // deeper and takes priority when enabled, so image-viewer close still works.
            BackHandler { goBack() }

            ProductDetailScreen(onBack = { goBack() })
        }
    }
}
