package co.ke.xently.business.landing

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
    val contentDescription: String,
) {
    HOME("Home", Icons.Default.Home, "Home"),
    FAVORITES("Favourites", Icons.Default.Favorite, "Favourites"),
    SHOPPING("Shopping", Icons.Default.ShoppingCart, "Shopping"),
    PROFILE("Profile", Icons.Default.AccountBox, "Profile"),
}

@Composable
fun LandingScreen(modifier: Modifier = Modifier) {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
    NavigationSuiteScaffold(
        modifier = modifier,
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            it.icon,
                            contentDescription = it.contentDescription,
                        )
                    },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        },
    ) {
        when (currentDestination) {
            AppDestinations.HOME -> HomeDestination()
            AppDestinations.FAVORITES -> FavoritesDestination()
            AppDestinations.SHOPPING -> ShoppingDestination()
            AppDestinations.PROFILE -> ProfileDestination()
        }
    }
}

@Composable
fun ProfileDestination() {
    Text(text = "Profile")
}

@Composable
fun ShoppingDestination() {
    Text(text = "Shopping")
}

@Composable
fun FavoritesDestination() {
    Text(text = "Favorites")
}

@Composable
fun HomeDestination() {
    Text(text = "Home")
}
