package co.ke.xently.customer.landing.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.waterfall
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.sp
import co.ke.xently.customer.R
import co.ke.xently.customer.landing.domain.AppDestination
import co.ke.xently.features.customers.presentation.list.CustomerScoreboardListScreen
import co.ke.xently.features.notifications.presentation.list.NotificationListScreen
import co.ke.xently.features.profile.presentation.detail.ProfileDetailScreen
import co.ke.xently.features.stores.data.domain.Store
import co.ke.xently.features.stores.presentation.list.StoreListScreen
import co.ke.xently.features.ui.core.presentation.components.DropdownMenuWithLegalRequirements

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun LandingScreenContent(
    currentDestination: AppDestination,
    onClickSettingsMenu: () -> Unit,
    onClickStore: (Store) -> Unit,
    onClickEditProfile: () -> Unit,
    navigationIcon: @Composable () -> Unit,
) {
    when (currentDestination) {
        AppDestination.DASHBOARD -> {
            StoreListScreen(onClickStore = onClickStore) {
                CenterAlignedTopAppBar(
                    navigationIcon = navigationIcon,
                    title = {
                        Text(
                            maxLines = 1,
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            text = stringResource(R.string.app_name).toUpperCase(Locale.current),
                        )
                    },
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
            }
        }

        AppDestination.SCOREBOARD -> CustomerScoreboardListScreen {
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

        AppDestination.PROFILE -> ProfileDetailScreen(onClickEditProfile = onClickEditProfile) {
            CenterAlignedTopAppBar(
                windowInsets = WindowInsets.waterfall,
                navigationIcon = navigationIcon,
                title = { Text(text = stringResource(R.string.topbar_title_profile)) },
            )
        }
    }
}