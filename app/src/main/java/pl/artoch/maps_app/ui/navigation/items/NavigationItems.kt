package pl.artoch.maps_app.ui.navigation.items

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

sealed interface NavigationItems {
    val route: String

    sealed interface TabScreen : NavigationItems {
        val title: String
        val selectedIcon: ImageVector
        val unselectedIcon: ImageVector
        val hasNews: Boolean
        val badgeCount: Int?

        companion object {

            /** Order is matters **/
            val items = setOf(HomeScreen, MapScreen, ChartScreen)
        }
    }

    data object HomeScreen : TabScreen {
        override val route: String = toString()
        override val title = "Home"
        override val selectedIcon = Icons.Filled.Home
        override val unselectedIcon = Icons.Outlined.Home
        override val hasNews = false
        override val badgeCount: Int? = null
    }

    data object MapScreen : TabScreen {
        override val route: String = toString()
        override val title = "Map"
        override val selectedIcon = Icons.Filled.LocationOn
        override val unselectedIcon = Icons.Outlined.LocationOn
        override val hasNews = true
        override val badgeCount: Int? = null
    }

    data object ChartScreen : TabScreen {
        override val route: String = toString()
        override val title: String = "Chat"
        override val selectedIcon: ImageVector = Icons.Filled.Email
        override val unselectedIcon: ImageVector = Icons.Outlined.Email
        override val hasNews: Boolean = true
        override val badgeCount: Int = 45
    }

    data object LogInScreen : NavigationItems {
        override val route: String = toString()
    }

    @Serializable
    data class NestedScreen(val username: String) : NavigationItems {
        override val route: String = toString()
    }

    data object OverlayScreen : NavigationItems {
        override val route: String = toString()
    }
}
