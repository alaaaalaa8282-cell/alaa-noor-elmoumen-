package com.alaa.presentation.navigation

sealed class Screen(val route: String) {
    object Home       : Screen("home")
    object Qibla      : Screen("qibla")
    object Azkar      : Screen("azkar")
    object Dhikr      : Screen("dhikr")
    object Tasbih     : Screen("tasbih")
    object Challenges : Screen("challenges")
    object Quran      : Screen("quran")
    object Settings   : Screen("settings")
}
