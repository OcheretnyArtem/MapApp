package pl.artoch.maps_app.ui.navigation.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import pl.artoch.maps_app.ui.navigation.item.Screen
import pl.artoch.maps_app.ui.screens.auth.LogInScreen

fun NavGraphBuilder.authNavGraph(navController: NavHostController) {
    navigation(
        route = Graph.AUTHENTICATION,
        startDestination = Screen.LogInScreen.route
    ) {
        composable(Screen.LogInScreen.route) {
            LogInScreen(navController)
        }
    }
}