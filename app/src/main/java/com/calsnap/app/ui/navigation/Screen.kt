package com.calsnap.app.ui.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Home       : Screen("home")
    object Progress   : Screen("progress")
    object AddFood    : Screen("add_food")
    object Settings   : Screen("settings")
    object AiChat     : Screen("ai_chat")
}
