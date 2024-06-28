package co.ke.xently.customer.landing.presentation

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import co.ke.xently.customer.R
import co.ke.xently.customer.landing.domain.AppDestination
import co.ke.xently.customer.landing.domain.Menu
import co.ke.xently.customer.landing.presentation.components.LandingModalDrawerSheet
import co.ke.xently.customer.landing.presentation.components.LandingScreenContent
import co.ke.xently.features.ui.core.presentation.LocalEventHandler
import co.ke.xently.libraries.ui.core.LocalAuthenticationState
import kotlinx.coroutines.launch

@Composable
fun LandingScreen(
    modifier: Modifier = Modifier,
    onClickSettings: () -> Unit,
) {
    val viewModel = hiltViewModel<LandingViewModel>()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val authenticationState = viewModel.authenticationState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val eventHandler = LocalEventHandler.current

    LaunchedEffect(viewModel) {
        viewModel.event.collect { event ->
            when (event) {
                LandingEvent.Success -> Unit
                is LandingEvent.SelectStore -> eventHandler.requestStoreSelection()
                is LandingEvent.Error -> {
                    Toast.makeText(
                        context,
                        event.error.asString(context = context),
                        Toast.LENGTH_LONG,
                    ).show()
                }

                is LandingEvent.ShopError -> {
                    Toast.makeText(
                        context,
                        event.error.asString(context = context),
                        Toast.LENGTH_LONG,
                    ).show()
                }
            }
        }
    }

    CompositionLocalProvider(LocalAuthenticationState provides authenticationState) {
        LandingScreen(
            modifier = modifier,
            onClickSettings = onClickSettings,
            onAction = viewModel::onAction,
        )
    }
}

@Composable
internal fun LandingScreen(
    modifier: Modifier = Modifier,
    onClickSettings: () -> Unit,
    onAction: (LandingAction) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val closeDrawer: () -> Unit = {
        scope.launch {
            if (drawerState.isOpen) {
                drawerState.close()
            }
        }
    }
    BackHandler(enabled = drawerState.isOpen) {
        closeDrawer()
    }
    var selectedMenu by rememberSaveable { mutableStateOf(Menu.DASHBOARD) }
    var currentDestination by rememberSaveable { mutableStateOf(AppDestination.DASHBOARD) }

    ModalNavigationDrawer(
        modifier = modifier,
        drawerState = drawerState,
        drawerContent = {
            val authenticationState by LocalAuthenticationState.current

            LandingModalDrawerSheet(
                selectedMenu = selectedMenu,
                authenticationState = authenticationState,
                onClickLogout = { onAction(LandingAction.ClickSignOut) },
                onClickMenu = {
                    when (it) {
                        Menu.SETTINGS -> onClickSettings()
                        Menu.DASHBOARD -> currentDestination = AppDestination.DASHBOARD
                        Menu.SCOREBOARD -> currentDestination = AppDestination.SCOREBOARD
                        Menu.PROFILE -> currentDestination = AppDestination.PROFILE
                        Menu.NOTIFICATIONS -> currentDestination = AppDestination.NOTIFICATIONS
                    }
                    selectedMenu = Menu.valueOf(currentDestination.name)
                    closeDrawer()
                },
            )
        },
    ) {
        val adaptiveInfo = currentWindowAdaptiveInfo()
        val customNavSuiteType = with(adaptiveInfo) {
            if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED) {
                NavigationSuiteType.NavigationDrawer
            } else {
                NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(adaptiveInfo)
            }
        }

        NavigationSuiteScaffold(
            modifier = Modifier.fillMaxSize(),
            layoutType = customNavSuiteType,
            navigationSuiteItems = {
                AppDestination.entries.forEach { destination ->
                    val showLabel =
                        adaptiveInfo.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT
                                && customNavSuiteType == NavigationSuiteType.NavigationBar
                    item(
                        icon = {
                            Icon(
                                destination.icon,
                                contentDescription = stringResource(destination.contentDescription),
                            )
                        },
                        label = if (showLabel) null else {
                            {
                                Text(
                                    text = stringResource(destination.label),
                                    modifier = Modifier.basicMarquee(),
                                )
                            }
                        },
                        selected = destination == currentDestination,
                        onClick = {
                            currentDestination = destination
                            selectedMenu = Menu.valueOf(destination.name)
                        },
                    )
                }
            },
        ) {
            val navigationIcon: @Composable () -> Unit = {
                IconButton(
                    onClick = {
                        scope.launch {
                            drawerState.apply {
                                if (isClosed) open() else close()
                            }
                        }
                    },
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Notes,
                        contentDescription = stringResource(R.string.content_desc_open_navigation_drawer),
                    )
                }
            }

            LandingScreenContent(
                currentDestination = currentDestination,
                navigationIcon = navigationIcon,
                onClickSettingsMenu = onClickSettings,
            )
        }
    }
}
