package com.example.stocky

import SearchScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.material3.Surface
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.example.stocky.screens.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StockyApp()
        }
    }
}

@Composable
fun StockyNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        composable(route = Screen.Main.route) {
            MainScreen(
                onStockClick = { symbol ->
                    navController.navigate("${Screen.Detail.route}/$symbol")
                },
                onNavigateToSearch = {
                    navController.navigate(Screen.Search.route)
                }
            )
        }

        composable(route = Screen.Search.route) {
            SearchScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Screen.Detail.routeWithArgument) { navBackStackEntry ->
            val symbol = navBackStackEntry.arguments?.getString("symbol").orEmpty()
            DetailScreen(
                stockSymbol = symbol,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

@Preview
@Composable
fun StockyApp() {
    val navController = rememberNavController()
    MaterialTheme {
        Surface {
            StockyNavGraph(navController = navController)
        }
    }
}