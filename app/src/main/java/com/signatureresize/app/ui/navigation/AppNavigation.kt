package com.signatureresize.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.signatureresize.app.ui.home.HomeScreen
import com.signatureresize.app.ui.editor.EditorScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Editor : Screen("editor")
    object Result : Screen("result")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToEditor = { navController.navigate(Screen.Editor.route) }
            )
        }
        composable(Screen.Editor.route) {
             EditorScreen(navController)
        }
        composable(Screen.Result.route) {
            // ResultScreen(navController)
        }
    }
}
