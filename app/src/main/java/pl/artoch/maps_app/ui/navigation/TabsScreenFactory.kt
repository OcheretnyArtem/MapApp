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
import pl.artoch.maps_app.ui.navigation.items.NavigationItems
import pl.artoch.maps_app.ui.screens.chart.ChartScreen
import pl.artoch.maps_app.ui.screens.home.HomeScreen
import pl.artoch.maps_app.ui.screens.map.MapScreen
import pl.artoch.maps_app.ui.screens.map.MapViewModel
import pl.artoch.maps_app.ui.screens.overlay.OverlayScreen
import pl.artoch.maps_app.ui.screens.stories.StoriesScreen
import pl.artoch.maps_app.ui.screens.stories.StoriesViewModel

@Composable
fun TabsScreenFactory(
    insetPaddings: PaddingValues,
    navController: NavHostController
) {
    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        startDestination = NavigationItems.HomeScreen.route
    ) {
        composable(NavigationItems.HomeScreen.route) {
            HomeScreen(navController = navController)
        }
        composable(NavigationItems.ChartScreen.route) {
            ChartScreen(navController = navController)
        }
        composable(NavigationItems.StoriesScreen.route) {
            StoriesScreen(
                viewModel = hiltViewModel<StoriesViewModel>(),
                innerPadding = insetPaddings,
                navController = navController
            )
        }
        dialog(
            route = NavigationItems.OverlayScreen.route,
            dialogProperties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            OverlayScreen(navController = navController)
        }
        composable(NavigationItems.MapScreen.route) {
            MapScreen(
                viewModel = hiltViewModel<MapViewModel>(),
                innerPadding = insetPaddings
            )
        }
    }
}
