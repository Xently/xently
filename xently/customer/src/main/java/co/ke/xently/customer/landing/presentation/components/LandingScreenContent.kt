package co.ke.xently.customer.landing.presentation.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.waterfall
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import co.ke.xently.customer.R
import co.ke.xently.customer.landing.domain.AppDestination
import co.ke.xently.features.customers.presentation.list.CustomerListScreen
import co.ke.xently.features.notifications.presentation.list.NotificationListScreen
import co.ke.xently.features.stores.presentation.list.selection.StoreSelectionListScreen

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun LandingScreenContent(
    currentDestination: AppDestination,
    navigationIcon: @Composable () -> Unit,
    onClickSettingsMenu: () -> Unit,
) {
    when (currentDestination) {
        AppDestination.DASHBOARD -> {
            StoreSelectionListScreen(
                onClickBack = {},
                onClickAddStore = {},
                onClickEditStore = {},
                onStoreSelected = {},
            )
            /*
            CenterAlignedTopAppBar(
                navigationIcon = navigationIcon,
                title = { Text(text = stringResource(R.string.app_name)) },
                actions = {
                    Box {
                        var expanded by rememberSaveable { mutableStateOf(false) }
                        IconButton(
                            onClick = { expanded = true },
                            content = {
                                Icon(
                                    Icons.Default.MoreVert,
                                    contentDescription = stringResource(R.string.content_desc_topbar_menu),
                                )
                            },
                        )

                        DropdownMenuWithLegalRequirements(
                            expanded = expanded,
                            onExpandChanged = { expanded = it },
                            preLegalRequirements = {
                                DropdownMenuItem(
                                    onClick = {
                                        onClickSettingsMenu()
                                        expanded = false
                                    },
                                    text = { Text(text = stringResource(R.string.app_destination_settings)) },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Settings,
                                            contentDescription = stringResource(R.string.app_destination_settings),
                                        )
                                    },
                                )
                            },
                        )
                    }
                },
            )
             */
        }

        AppDestination.SCOREBOARD -> CustomerListScreen {
            CenterAlignedTopAppBar(
                windowInsets = WindowInsets.waterfall,
                navigationIcon = navigationIcon,
                title = { Text(text = stringResource(R.string.topbar_title_scoreboard)) },
            )
        }

        AppDestination.NOTIFICATIONS -> NotificationListScreen {
            CenterAlignedTopAppBar(
                windowInsets = WindowInsets.waterfall,
                navigationIcon = navigationIcon,
                title = { Text(text = stringResource(R.string.topbar_title_notifications)) },
            )
        }

        AppDestination.PROFILE -> Unit // TODO: Add implementation...
    }
}