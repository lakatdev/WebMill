package hu.keszeglab.webmill.navigation

import androidx.compose.runtime.*

class ScreenManager {
    private val _currentScreen = mutableStateOf<Screen>(Screen.Menu)
    val currentScreen: State<Screen> = _currentScreen
    
    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }
    
    fun navigateToMenu() {
        navigateTo(Screen.Menu)
    }

    fun navigateToGame(isVsComputer: Boolean = false, smartness: Int = 0) {
        navigateTo(Screen.Game(isVsComputer, smartness))
    }
}

@Composable
fun rememberScreenManager(): ScreenManager {
    return remember { ScreenManager() }
}
