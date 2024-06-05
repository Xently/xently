package co.ke.xently.features.stores.presentation.active.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.ke.xently.features.shops.data.domain.Shop
import co.ke.xently.features.stores.R
import co.ke.xently.features.stores.data.domain.Store
import co.ke.xently.features.ui.core.presentation.components.CircularButton
import co.ke.xently.features.ui.core.presentation.components.PlaceHolderImageThumbnail
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyPreview


@Composable
internal fun StoreSummaryListItem(store: Store, onClickEdit: () -> Unit) {
    ListItem(
        headlineContent = { Text(store.shop.name, fontWeight = FontWeight.Bold) },
        supportingContent = { Text(store.name) },
        leadingContent = {
            PlaceHolderImageThumbnail(size = 60.dp) {
                Icon(Icons.Default.Person, contentDescription = null)
            }
        },
        trailingContent = {
            CircularButton(
                onClick = onClickEdit,
                content = {
                    Icon(
                        Icons.Outlined.Edit,
                        contentDescription = stringResource(R.string.content_desc_edit_store),
                    )
                },
            )
        }
    )
}

@XentlyPreview
@Composable
private fun StoreSummaryListItemPreview() {
    XentlyTheme {
        val store = remember {
            Store(
                name = "Westlands",
                shop = Shop(name = "Ranalo K'Osewe"),
                description = "Short description about the business/hotel will go here. Lorem ipsum dolor trui loerm ipsum is a repetitive alternative place holder text for design projects.",
            )
        }
        StoreSummaryListItem(
            store = store,
            onClickEdit = {},
        )
    }
}