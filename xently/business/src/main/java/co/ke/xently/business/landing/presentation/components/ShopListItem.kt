package co.ke.xently.business.landing.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.ke.xently.business.R
import co.ke.xently.features.shops.data.domain.Shop
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.data.core.Link
import co.ke.xently.libraries.ui.core.XentlyThemePreview

@Composable
internal fun ShopListItem(
    shop: Shop,
    modifier: Modifier = Modifier,
    onClickAddStore: () -> Unit,
) {
    ListItem(
        modifier = modifier,
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        headlineContent = { Text(text = shop.name) },
        leadingContent = if (!shop.isActivated) null else {
            {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = stringResource(R.string.content_desc_is_currently_selected),
                )
            }
        },
        trailingContent = if (!shop.links.containsKey("add-store")) null else {
            {
                Surface(
                    color = Color.Transparent,
                    onClick = onClickAddStore,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.AddBusiness,
                            contentDescription = stringResource(R.string.action_add_store),
                        )
                        Text(stringResource(R.string.action_add_store))
                    }
                }
            }
        },
    )
}

@XentlyThemePreview
@Composable
private fun ShopListItemPreview() {
    XentlyTheme {
        ShopListItem(
            shop = remember {
                Shop(
                    id = 1,
                    name = "Xently Electronics Shop",
                    links = mapOf("add-store" to Link(href = "https://xently.co.ke")),
                )
            },
            onClickAddStore = { },
            modifier = Modifier.padding(8.dp),
        )
    }
}