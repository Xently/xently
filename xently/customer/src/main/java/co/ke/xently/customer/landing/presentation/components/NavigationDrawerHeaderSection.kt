package co.ke.xently.customer.landing.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import co.ke.xently.customer.R
import co.ke.xently.features.shops.data.domain.Shop
import co.ke.xently.features.ui.core.presentation.components.PlaceHolderImageThumbnail
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.data.auth.CurrentUser
import co.ke.xently.libraries.ui.core.XentlyThemePreview
import java.util.UUID

@Composable
internal fun NavigationDrawerHeaderSection(
    modifier: Modifier = Modifier,
    currentUser: CurrentUser?,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.defaultMinSize(minHeight = 176.dp),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            ListItem(
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                leadingContent = {
                    PlaceHolderImageThumbnail(
                        paddingValues = PaddingValues(2.dp),
                        contentColour = MaterialTheme.colorScheme.onPrimary,
                        color = MaterialTheme.colorScheme.primary,
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Profile picture",
                            modifier = Modifier.matchParentSize(),
                        )
                    }
                },
                headlineContent = {
                    Text(
                        text = currentUser?.name ?: stringResource(R.string.anonymous),
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                supportingContent = {
                    Text(
                        text = currentUser?.email ?: stringResource(R.string.anonymous_email),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelLarge,
                    )
                },
            )
        }
    }
}

private data class NavigationDrawerHeaderSectionState(
    val canAddShop: Boolean,
    val currentUser: CurrentUser?,
    val switchAccount: Boolean = false,
    val shops: List<Shop> = emptyList(),
)

private class NavigationDrawerHeaderSectionStatePreviewProvider :
    PreviewParameterProvider<NavigationDrawerHeaderSectionState> {
    override val values: Sequence<NavigationDrawerHeaderSectionState>
        get() = sequenceOf(
            NavigationDrawerHeaderSectionState(
                canAddShop = false,
                currentUser = null,
            ),
            NavigationDrawerHeaderSectionState(
                canAddShop = true,
                currentUser = null,
                switchAccount = true,
            ),
            NavigationDrawerHeaderSectionState(
                canAddShop = true,
                currentUser = null,
                switchAccount = true,
                shops = List(5) {
                    Shop.DEFAULT.copy(id = it + 1L, isActivated = it == 1)
                },
            ),
            NavigationDrawerHeaderSectionState(
                canAddShop = true,
                currentUser = CurrentUser(
                    id = UUID.randomUUID().toString(),
                    name = "John Doe",
                    email = "william.henry.harrison@example-pet-store.com",
                ),
            ),
            NavigationDrawerHeaderSectionState(
                canAddShop = true,
                switchAccount = true,
                currentUser = CurrentUser(
                    id = UUID.randomUUID().toString(),
                    name = "John Doe",
                    email = "william.henry.harrison@example-pet-store.com",
                ),
            ),
            NavigationDrawerHeaderSectionState(
                canAddShop = true,
                switchAccount = true,
                currentUser = CurrentUser(
                    id = UUID.randomUUID().toString(),
                    name = "John Doe",
                    email = "william.henry.harrison@example-pet-store.com",
                ),
                shops = List(5) {
                    Shop.DEFAULT.copy(id = it + 1L, isActivated = it == 1)
                },
            ),
        )
}

@XentlyThemePreview
@Composable
private fun NavigationDrawerHeaderSectionPreview(
    @PreviewParameter(NavigationDrawerHeaderSectionStatePreviewProvider::class)
    state: NavigationDrawerHeaderSectionState,
) {
    XentlyTheme {
        NavigationDrawerHeaderSection(
            currentUser = state.currentUser,
        )
    }
}