package co.ke.xently.features.products.presentation.list.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.minimumInteractiveComponentSize
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
import co.ke.xently.features.products.R
import co.ke.xently.features.products.data.domain.Product
import co.ke.xently.features.ui.core.presentation.components.DropdownMenuWithUpdateAndDelete
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyThemePreview
import co.ke.xently.libraries.ui.core.domain.formatPrice
import co.ke.xently.libraries.ui.image.XentlyImage

@Composable
internal fun ProductListItem(
    product: Product,
    modifier: Modifier = Modifier,
    onClickUpdate: () -> Unit,
    onClickConfirmDelete: () -> Unit,
) {
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            text = { Text(text = """Are you sure you want to delete the "$product"?""") },
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

    val imageSize = 60.dp
    ListItem(
        modifier = modifier,
        leadingContent = {
            Card(modifier = Modifier.size(size = imageSize)) {
                val image = product.images.firstOrNull()
                if (image != null) {
                    XentlyImage(
                        data = image,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        },
        headlineContent = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = product.toString(),
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold,
                    maxLines = if (product.description.isNullOrBlank()) 3 else 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelLarge,
                )
                Text(
                    text = product.unitPrice.formatPrice("KES", false),
                    style = MaterialTheme.typography.labelLarge,
                )
                var expanded by rememberSaveable { mutableStateOf(false) }

                Box {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = """More options for product "$product".""",
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
        supportingContent = if (product.description.isNullOrBlank()) null else {
            {
                var expand by rememberSaveable { mutableStateOf(false) }
                Text(
                    text = product.description!!,
                    maxLines = if (expand) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clickable(
                            role = Role.Checkbox,
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                        ) { expand = !expand },
                )
            }
        },
    )
}

private class ProductListItemParameterProvider : PreviewParameterProvider<Product> {
    override val values: Sequence<Product>
        get() = sequenceOf(
            Product(
                name = "Chips Kuku",
                unitPrice = 19234.0,
                description = "A mix of pilau and roasted potatoes garnished with a side of paprika grilled tomatoes.",
            ),
            Product(
                name = "Chips Kuku, Bhajia, Smokies, Fish, Yoghurt, Sugar & Chicken",
                unitPrice = 134.0,
            ),
        )
}

@XentlyThemePreview
@Composable
private fun ProductListItemPreview(
    @PreviewParameter(ProductListItemParameterProvider::class)
    product: Product,
) {
    XentlyTheme {
        ProductListItem(
            product = product,
            onClickUpdate = {},
            onClickConfirmDelete = {},
        )
    }
}