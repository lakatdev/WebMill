package hu.keszeglab.webmill.navigation

sealed class Screen {
    data object Menu : Screen()
    data object Game : Screen()
}
