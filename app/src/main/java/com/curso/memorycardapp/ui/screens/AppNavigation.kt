package com.curso.memorycardapp.ui.screens

import androidx.activity.ComponentActivity
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.curso.memorycardapp.ui.model.GameConfiguration
import com.curso.memorycardapp.ui.model.GameResult
import com.curso.memorycardapp.ui.model.PreferencesViewModel
import com.curso.memorycardapp.ui.model.PreferencesViewModelFactory

private object Routes {
    const val MAIN        = "main"
    const val CONFIG      = "config"
    const val GAME        = "game"
    const val RESULTS     = "results"
    const val HELP        = "help"
    const val PREFERENCES = "preferences"
    const val HISTORIAL   = "historial"
    const val DETALLE     = "historial_detalle"
}

@Composable
fun MemoryCardNavigation(windowSizeClass: WindowSizeClass) {
    val navController = rememberNavController()
    val context = LocalContext.current

    val prefsViewModel: PreferencesViewModel = viewModel(
        factory = PreferencesViewModelFactory(context)
    )
    val savedPrefs by prefsViewModel.preferences.collectAsState()

    // Fix 2.11: currentConfig guardado en el ViewModel para sobrevivir rotaciones
    var currentConfig by rememberSaveable { mutableStateOf(GameConfiguration()) }

    // Fix 1.5: gameResult guardado con rememberSaveable para sobrevivir rotaciones
    var gameResult by rememberSaveable { mutableStateOf<GameResult?>(null) }

    NavHost(navController, startDestination = Routes.MAIN) {

        composable(Routes.MAIN) {
            MainMenuScreen(
                onPlay = {
                    currentConfig = savedPrefs
                    navController.navigate(Routes.GAME) {
                        popUpTo(Routes.GAME) { inclusive = true }
                    }
                },
                onHelp        = { navController.navigate(Routes.HELP) },
                onHistory     = { navController.navigate(Routes.HISTORIAL) },
                onPreferences = { navController.navigate(Routes.PREFERENCES) },
                onExit        = { (context as? ComponentActivity)?.finish() }
            )
        }

        composable(Routes.CONFIG) {
            ConfigurationScreen(
                initialConfig = savedPrefs,
                onStart = { config ->
                    currentConfig = config
                    prefsViewModel.guardar(config)
                    navController.navigate(Routes.GAME) {
                        popUpTo(Routes.GAME) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.GAME) {
            GameScreen(
                config          = currentConfig,
                windowSizeClass = windowSizeClass,
                onGameEnd = { result ->
                    gameResult = result
                    navController.navigate(Routes.RESULTS) {
                        popUpTo(Routes.GAME) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.RESULTS) {
            // Fix 1.5: gameResult ahora sobrevive rotaciones gracias a rememberSaveable
            gameResult?.let { result ->
                ResultsScreen(
                    result        = result,
                    onRestart     = {
                        navController.navigate(Routes.CONFIG) {
                            popUpTo(Routes.RESULTS) { inclusive = true }
                        }
                    },
                    onExit        = {
                        navController.navigate(Routes.MAIN) {
                            popUpTo(Routes.MAIN) { inclusive = false }
                        }
                    },
                    onQuit        = { (context as? ComponentActivity)?.finish() },
                    onPreferences = { navController.navigate(Routes.PREFERENCES) }
                )
            }
        }

        composable(Routes.HELP) {
            HelpScreen { navController.popBackStack() }
        }

        composable(Routes.PREFERENCES) {
            PreferencesScreen { navController.popBackStack() }
        }

        composable(Routes.HISTORIAL) {
            HistorialScreen(
                windowSizeClass = windowSizeClass,
                onBack          = { navController.popBackStack() },
                onDetalle       = { id ->
                    navController.navigate("${Routes.DETALLE}/$id")
                }
            )
        }

        composable("${Routes.DETALLE}/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull()
                ?: return@composable
            HistorialDetalleScreen(
                partidaId = id,
                onBack    = { navController.popBackStack() }
            )
        }
    }
}