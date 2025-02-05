package pl.artoch.maps_app.ui.navigation.graphs

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pl.artoch.maps_app.ui.navigation.BottomNavigationBar
import pl.artoch.maps_app.ui.navigation.TabsScreenFactory

fun NavGraphBuilder.tabsNavGraph() {
    composable(Graph.TABS) {
        TabsContainer()
    }
}

@Composable
private fun TabsContainer(navController: NavHostController = rememberNavController()) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { insetPaddings ->
        TabsScreenFactory(insetPaddings, navController)
    }
}
