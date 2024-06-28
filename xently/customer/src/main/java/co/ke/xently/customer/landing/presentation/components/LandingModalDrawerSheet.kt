package co.ke.xently.customer.landing.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import co.ke.xently.customer.R
import co.ke.xently.customer.landing.domain.Menu
import co.ke.xently.features.ui.core.presentation.LocalEventHandler
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.data.auth.AuthenticationState
import co.ke.xently.libraries.data.auth.CurrentUser
import co.ke.xently.libraries.ui.core.XentlyThemePreview
import co.ke.xently.libraries.ui.core.openUrl
import java.util.UUID

@Composable
internal fun LandingModalDrawerSheet(
    selectedMenu: Menu,
    authenticationState: AuthenticationState,
    modifier: Modifier = Modifier,
    onClickLogout: () -> Unit,
    onClickMenu: (Menu) -> Unit,
) {
    val eventHandler = LocalEventHandler.current
    ModalDrawerSheet(modifier = modifier) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .weight(1f),
        ) {
            NavigationDrawerHeaderSection(currentUser = authenticationState.currentUser)
            AnimatedVisibility(authenticationState.isSignOutInProgress) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            HorizontalDivider()
            for (menu in Menu.entries) {
                NavigationDrawerItem(
                    selected = selectedMenu.isSelectable && menu == selectedMenu,
                    label = { Text(text = stringResource(menu.title)) },
                    icon = { Icon(menu.icon, null) },
                    onClick = { onClickMenu(menu) },
                )
            }
            HorizontalDivider()
            if (authenticationState.currentUser != null) {
                NavigationDrawerItem(
                    selected = false,
                    onClick = onClickLogout,
                    modifier = Modifier.padding(vertical = 48.dp),
                    label = { Text(text = stringResource(R.string.action_logout)) },
                    icon = { Icon(Icons.AutoMirrored.Filled.Logout, null) },
                )
            } else {
                NavigationDrawerItem(
                    selected = false,
                    onClick = eventHandler::requestAuthentication,
                    modifier = Modifier.padding(vertical = 48.dp),
                    label = { Text(text = stringResource(R.string.action_login)) },
                    icon = { Icon(Icons.AutoMirrored.Filled.Login, null) },
                )
            }
        }

        HorizontalDivider()
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            val content = LocalContext.current
            val privacyPolicy = "https://xently.co.ke/privacy.html"
            TextButton(onClick = { content.openUrl(privacyPolicy) }) {
                Text(text = stringResource(R.string.action_label_privacy_policy))
            }

            Icon(
                Icons.Default.Circle,
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .padding(horizontal = 8.dp),
            )

            val termsOfService = "https://xently.co.ke/termsandconditions.html"
            TextButton(onClick = { content.openUrl(termsOfService) }) {
                Text(text = stringResource(R.string.action_label_terms_of_service))
            }
        }
    }
}

private data class ModalDrawerSheetState(
    val canAddShop: Boolean,
    val authenticationState: AuthenticationState,
)

private class ModalDrawerSheetStatePreviewProvider :
    PreviewParameterProvider<ModalDrawerSheetState> {
    override val values: Sequence<ModalDrawerSheetState>
        get() = sequenceOf(
            ModalDrawerSheetState(
                canAddShop = false,
                authenticationState = AuthenticationState(),
            ),
            ModalDrawerSheetState(
                canAddShop = true,
                authenticationState = AuthenticationState(
                    isSignOutInProgress = true,
                    currentUser = CurrentUser(
                        id = UUID.randomUUID().toString(),
                        name = "John Doe",
                        email = "william.henry.harrison@example-pet-store.com"
                    ),
                ),
            ),
            ModalDrawerSheetState(
                canAddShop = false,
                authenticationState = AuthenticationState(
                    isSignOutInProgress = true,
                    currentUser = CurrentUser(
                        id = UUID.randomUUID().toString(),
                        name = "John Doe",
                        email = "william.henry.harrison@example-pet-store.com"
                    ),
                ),
            ),
        )
}

@XentlyThemePreview
@Composable
private fun ModalDrawerSheetPreview(
    @PreviewParameter(ModalDrawerSheetStatePreviewProvider::class)
    state: ModalDrawerSheetState,
) {
    XentlyTheme {
        LandingModalDrawerSheet(
            selectedMenu = Menu.entries.random(),
            authenticationState = state.authenticationState,
            onClickLogout = {},
            onClickMenu = {},
        )
    }
}