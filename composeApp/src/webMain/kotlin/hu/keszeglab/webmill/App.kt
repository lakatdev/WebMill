package hu.keszeglab.webmill

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import hu.keszeglab.webmill.navigation.Screen
import hu.keszeglab.webmill.navigation.rememberScreenManager
import hu.keszeglab.webmill.ui.screens.GameScreen
import hu.keszeglab.webmill.ui.screens.MenuScreen

@Composable
fun App() {
    MaterialTheme {
        val screenManager = rememberScreenManager()
        val currentScreen by screenManager.currentScreen
        
        when (currentScreen) {
            is Screen.Menu -> {
                MenuScreen(
                    onStartClick = { isVsComputer, smartness ->
                        screenManager.navigateToGame(isVsComputer, smartness)
                    },
                    modifier = Modifier.fillMaxSize().safeContentPadding()
                )
            }
            is Screen.Game -> {
                GameScreen(
                    isVsComputer = (currentScreen as Screen.Game).isVsComputer,
                    computerSmartness = (currentScreen as Screen.Game).computerSmartness,
                    onBackToStart = {
                        screenManager.navigateToMenu()
                    },
                    modifier = Modifier.fillMaxSize().safeContentPadding()
                )
            }
        }
    }
}