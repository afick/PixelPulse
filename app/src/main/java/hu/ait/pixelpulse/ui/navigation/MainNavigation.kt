package hu.ait.pixelpulse.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    object Login : Screen("login")
}

enum class NavigationItem(val route: String, val icon: ImageVector, val title: String) {
    Feed("feed", Icons.Filled.Home, "Feed"),
    Post("post", Icons.Filled.Add, "Post"),
    UserProfile("profile", Icons.Filled.Person, "Profile"),
}