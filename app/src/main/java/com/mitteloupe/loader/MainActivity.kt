package com.mitteloupe.loader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mitteloupe.loader.gears.Gears
import com.mitteloupe.loader.home.Home
import com.mitteloupe.loader.jigsaw.Jigsaw
import com.mitteloupe.loader.ui.theme.LoadersTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoadersTheme {
                MainNavHost()
            }
        }
    }
}

@Composable
fun MainNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = "home"
) {
    NavHost(navController, startDestination = startDestination, modifier = modifier) {
        composable("home") {
            Home(
                onNavigateToGears = { navController.navigate("gears") },
                onNavigateToJigsaw = { navController.navigate("jigsaw") }
            )
        }
        composable("gears") { Gears(navController) }
        composable("jigsaw") { Jigsaw(navController) }
    }
}

@Preview
@Composable
fun Preview() {
    LoadersTheme {
        MainNavHost()
    }
}
