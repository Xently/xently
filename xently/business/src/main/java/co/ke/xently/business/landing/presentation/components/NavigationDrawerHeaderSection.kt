package co.ke.xently.business.landing.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import co.ke.xently.business.R
import co.ke.xently.features.shops.data.domain.Shop
import co.ke.xently.features.ui.core.presentation.components.PlaceHolderImageThumbnail
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.data.auth.CurrentUser
import co.ke.xently.libraries.ui.core.XentlyThemePreview

@Composable
internal fun NavigationDrawerHeaderSection(
    modifier: Modifier = Modifier,
    canAddShop: Boolean,
    currentUser: CurrentUser?,
    onClickAddShop: () -> Unit,
    onClickSelectShop: () -> Unit,
    onClickShop: (Shop) -> Unit,
    onClickAddStore: (Shop) -> Unit,
) {
    var switchAccount by rememberSaveable { mutableStateOf(false) }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .defaultMinSize(minHeight = 176.dp)
            .then(modifier),
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
                        text = currentUser?.displayName ?: stringResource(R.string.anonymous),
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
                trailingContent = {
                    IconButton(
                        onClick = { switchAccount = !switchAccount },
                        content = {
                            Icon(
                                if (switchAccount) {
                                    Icons.Default.KeyboardArrowUp
                                } else Icons.Default.KeyboardArrowDown,
                                contentDescription = "Switch account",
                            )
                        },
                    )
                },
            )
            if (switchAccount) {
                HorizontalDivider()
                ListItem(
                    modifier = Modifier.clickable {
                        switchAccount = false
                        onClickSelectShop()
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.action_select_shop),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge,
                        )
                    },
                    trailingContent = if (!canAddShop) null else {
                        {
                            Surface(onClick = onClickAddShop, color = Color.Transparent) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Default.AddBusiness,
                                        contentDescription = stringResource(R.string.action_add_shop),
                                    )
                                    Text(stringResource(R.string.action_add_shop))
                                }
                            }
                        }
                    },
                )

                /*storeResponse.also {
                    when (it) {
                        Response.Default -> Unit
                        is Response.Failure -> {
                            // TODO: Handle failure...
                        }

                        Response.Loading -> {
                            HorizontalDivider()
                            CircularProgressIndicator()
                        }

                        is Response.Success -> {
                            HorizontalDivider()
                            val shop: Shop = it.data.shop
                            ShopListItem(
                                shop = shop,
                                onClickShop = {switchAccount = false; onClickShop(shop) },
                                onClickAddStore = { onClickAddStore(shop) }
                            )
                        }
                    }
                }*/
            }
        }
    }
}

private data class NavigationDrawerHeaderSectionState(
    val canAddShop: Boolean,
    val currentUser: CurrentUser?,
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
                currentUser = CurrentUser(
                    uid = 1,
                    firstName = "John",
                    lastName = "Doe",
                    email = "william.henry.harrison@example-pet-store.com",
                ),
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
            canAddShop = state.canAddShop,
            currentUser = state.currentUser,
            onClickAddShop = {},
            onClickSelectShop = {},
            onClickShop = {},
            onClickAddStore = {},
        )
    }
}