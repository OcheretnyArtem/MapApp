package pl.artoch.maps_app.ui.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import pl.artoch.maps_app.ui.navigation.items.NavigationItems

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val tabs = NavigationItems.TabScreen.items
    var selectedTab by remember { mutableStateOf(tabs.first().route) }
    LaunchedEffect(navBackStackEntry) {
        navBackStackEntry?.let { entry ->
            val matchingItem = tabs.find { it.route == entry.destination.route }
            matchingItem?.route?.let { selectedTab = it }
        }
    }
    NavigationBar {
        tabs.forEach { item ->
            BottomNavigationBarItem(
                item = item,
                isSelected = selectedTab == item.route,
                onItemClick = {
                    selectedTab = item.route
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            selectedTab = item.route
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
private fun RowScope.BottomNavigationBarItem(
    item: NavigationItems.TabScreen,
    isSelected: Boolean,
    onItemClick: () -> Unit
) {
    NavigationBarItem(
        selected = isSelected,
        onClick = onItemClick,
        label = {
            Text(text = item.title)
        },
        icon = {
            BadgedBox(
                badge = { SetupBadges(item) }
            ) {
                Icon(
                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                    contentDescription = item.title
                )
            }
        },
        alwaysShowLabel = false
    )
}

@Composable
private fun SetupBadges(item: NavigationItems.TabScreen) {
    when {
        item.badgeCount != null -> Badge {
            Text(text = item.badgeCount.toString())
        }

        item.hasNews -> Badge()
        else -> Unit
    }
}
