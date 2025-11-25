package hu.keszeglab.webmill.navigation

sealed class Screen {
    data object Menu : Screen()
    data class Game(val isVsComputer: Boolean = false, val computerSmartness: Int = 0) : Screen()
}
