package pl.artoch.maps_app.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import pl.artoch.maps_app.ui.navigation.screens.Screen
import pl.artoch.maps_app.ui.screens.chart.ChartScreen
import pl.artoch.maps_app.ui.screens.home.HomeScreen
import pl.artoch.maps_app.ui.screens.map.MapScreen
import pl.artoch.maps_app.ui.screens.map.MapViewModel
import pl.artoch.maps_app.ui.screens.nested.NestedScreen
import pl.artoch.maps_app.ui.screens.overlay.OverlayScreen

@Composable
fun TabsScreenFactory(
    insetPaddings: PaddingValues,
    navController: NavHostController
) {
    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        startDestination = Screen.HomeScreen.route
    ) {
        composable(Screen.HomeScreen.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.ChartScreen.route) {
            ChartScreen(navController = navController)
        }
        composable(Screen.NestedScreen.route) {
            NestedScreen()
        }
        dialog(
            route = Screen.OverlayScreen.route,
            dialogProperties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            OverlayScreen(navController = navController)
        }
        composable(Screen.MapScreen.route) {
            MapScreen(
                viewModel = hiltViewModel<MapViewModel>(),
                innerPadding = insetPaddings
            )
        }
    }
}
