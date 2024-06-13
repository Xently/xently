package co.ke.xently.features.shops.presentation.list.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import co.ke.xently.features.shops.R
import co.ke.xently.features.shops.data.domain.Shop
import co.ke.xently.features.ui.core.presentation.components.DropdownMenuWithUpdateAndDelete
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.data.core.Link
import co.ke.xently.libraries.ui.core.XentlyThemePreview

@Composable
internal fun ShopListItem(
    shop: Shop,
    modifier: Modifier = Modifier,
    onClickUpdate: () -> Unit,
    onClickConfirmDelete: () -> Unit,
) {
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            text = { Text(text = """Are you sure you want to delete the "$shop"?""") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onClickConfirmDelete()
                    },
                ) { Text(text = stringResource(R.string.action_delete)) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(text = stringResource(android.R.string.cancel))
                }
            },
        )
    }

    ListItem(
        modifier = modifier,
        leadingContent = {
            Card(modifier = Modifier.size(size = 60.dp)) {

            }
        },
        headlineContent = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = shop.toString(),
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelLarge,
                )
                var expanded by rememberSaveable { mutableStateOf(false) }

                Box {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = """More options for shop "$shop".""",
                        modifier = Modifier.clickable(
                            role = Role.Checkbox,
                            indication = ripple(bounded = false),
                            interactionSource = remember { MutableInteractionSource() },
                        ) { expanded = true },
                    )

                    DropdownMenuWithUpdateAndDelete(
                        expanded = expanded,
                        onExpandChanged = { expanded = it },
                        onClickUpdate = { onClickUpdate(); expanded = false },
                        onClickDelete = {
                            showDeleteDialog = true
                            expanded = false
                        },
                    )
                }
            }
        },
    )
}

private class ShopListItemParameterProvider : PreviewParameterProvider<Shop> {
    override val values: Sequence<Shop>
        get() = sequenceOf(
            Shop(
                id = 1L,
                name = "Shop name",
                slug = "shop-name",
                onlineShopUrl = "https://example.com",
                links = mapOf(
                    "self" to Link(href = "https://example.com"),
                    "add-store" to Link(href = "https://example.com/edit"),
                ),
            ),
            Shop(
                id = 1L,
                name = "Shop name",
                slug = "shop-name",
                onlineShopUrl = "https://example.com",
                links = mapOf(
                    "self" to Link(href = "https://example.com"),
                    "add-store" to Link(href = "https://example.com/edit"),
                ),
            ),
        )
}

@XentlyThemePreview
@Composable
private fun ShopListItemPreview(
    @PreviewParameter(ShopListItemParameterProvider::class)
    shop: Shop,
) {
    XentlyTheme {
        ShopListItem(
            shop = shop,
            onClickUpdate = {},
            onClickConfirmDelete = {},
        )
    }
}