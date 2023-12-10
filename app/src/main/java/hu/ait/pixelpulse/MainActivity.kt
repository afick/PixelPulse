package hu.ait.pixelpulse

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import hu.ait.pixelpulse.ui.navigation.Screen
import hu.ait.pixelpulse.ui.screen.auth.login.LoginScreen
import hu.ait.pixelpulse.ui.screen.feed.FeedScreen
import hu.ait.pixelpulse.ui.screen.postupload.PostUploadScreen
import hu.ait.pixelpulse.ui.theme.PixelPulseTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PixelPulseTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PostUploadScreen(navController = rememberNavController())
                }
            }
        }
    }
}


@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Feed.route)
                }
            )
        }
        composable(Screen.Feed.route) {
            FeedScreen(
                onNavigateToWritePost = {
                    navController.navigate(Screen.WritePost.route)
                }
            )
        }
//        composable(Screen.WritePost.route) {
//            WritePostScreen()
//        }
    }
}