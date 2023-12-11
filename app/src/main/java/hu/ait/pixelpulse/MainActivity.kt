package hu.ait.pixelpulse

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import hu.ait.pixelpulse.ui.navigation.BottomNavigationBar
import hu.ait.pixelpulse.ui.navigation.NavigationItem
import hu.ait.pixelpulse.ui.navigation.Screen
import hu.ait.pixelpulse.ui.screen.auth.login.LoginScreen
import hu.ait.pixelpulse.ui.screen.feed.FeedScreen
import hu.ait.pixelpulse.ui.screen.postupload.PostUploadScreen
import hu.ait.pixelpulse.ui.screen.profile.UserProfile
import hu.ait.pixelpulse.ui.theme.AppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    // State of bottomBar, set state to false, if current page route is "car_details"
                    val bottomBarState = rememberSaveable { (mutableStateOf(true)) }

                    val navBackStackEntry by navController.currentBackStackEntryAsState()

// Control TopBar and BottomBar
                    when (navBackStackEntry?.destination?.route) {
                        Screen.Login.route -> {
                            bottomBarState.value = false
                        }

                        else -> bottomBarState.value = true
                    }
                    Scaffold(
                        bottomBar = { BottomNavigationBar(navController, bottomBarState) }
                    ) {
                        NavGraph(navController)
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    BottomNavigation(
    ) {
        val currentRoute = navController.currentDestination?.route
        NavigationItem.values().forEach { item ->
            BottomNavigationItem(
                icon = { Icon(Icons.Filled.Home, contentDescription = null) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        // Avoid multiple copies of the same destination
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(NavigationItem.Feed.route) }
            )
        }

        composable(NavigationItem.Feed.route) {
            FeedScreen()
        }
        composable(NavigationItem.Post.route) {
            PostUploadScreen(navController = navController)
        }

        composable(NavigationItem.UserProfile.route) {
            UserProfile()
        }
    }
}
