package com.curso.memorycardapp.ui.screens

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.curso.memorycardapp.ui.model.GameConfiguration
import com.curso.memorycardapp.ui.model.GameResult

private object Routes {
    const val MAIN = "main"
    const val CONFIG = "config"
    const val GAME = "game"
    const val RESULTS = "results"
    const val HELP = "help"
}

@Composable
fun MemoryCardNavigation() {
    val navController = rememberNavController()
    var currentConfig by remember { mutableStateOf(GameConfiguration()) }
    var gameResult by remember { mutableStateOf<GameResult?>(null) }
    val context = LocalContext.current

    NavHost(navController, startDestination = Routes.MAIN) {

        composable(Routes.MAIN) {
            MainMenuScreen(
                onPlay = { navController.navigate(Routes.CONFIG) },
                onHelp = { navController.navigate(Routes.HELP) },
                onExit = { (context as? ComponentActivity)?.finish() }
            )
        }

        composable(Routes.CONFIG) {
            ConfigurationScreen(
                onStart = { config ->
                    currentConfig = config
                    // Limpiar game del back stack para que "Atrás" desde juego vaya a config
                    navController.navigate(Routes.GAME) {
                        popUpTo(Routes.GAME) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.GAME) {
            GameScreen(
                config = currentConfig,
                onGameEnd = { result ->
                    gameResult = result
                    // Limpiar el juego del back stack al ir a resultados
                    navController.navigate(Routes.RESULTS) {
                        popUpTo(Routes.GAME) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.RESULTS) {
            gameResult?.let { result ->
                ResultsScreen(
                    result = result,
                    onRestart = {
                        // Volver a config para nueva partida, limpiar resultados
                        navController.navigate(Routes.CONFIG) {
                            popUpTo(Routes.RESULTS) { inclusive = true }
                        }
                    },
                    onExit = {
                        // Volver al menú principal, limpiar todo el back stack
                        navController.navigate(Routes.MAIN) {
                            popUpTo(Routes.MAIN) { inclusive = false }
                        }
                    },
                    onQuit = { (context as? ComponentActivity)?.finish() }
                )
            }
        }

        composable(Routes.HELP) {
            HelpScreen { navController.popBackStack() }
        }
    }
}