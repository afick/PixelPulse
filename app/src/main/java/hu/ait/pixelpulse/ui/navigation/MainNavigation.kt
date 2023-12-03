package hu.ait.pixelpulse.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Feed : Screen("feed")
    object WritePost : Screen("writepost")
    object Profile : Screen("profile")
    object OtherProfile : Screen("otherprofile")
}