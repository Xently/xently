package co.ke.xently.features.products.presentation.list.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
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
import androidx.compose.ui.tooling.preview.PreviewParameter
import co.ke.xently.features.products.R
import co.ke.xently.features.products.data.domain.Product
import co.ke.xently.features.ui.core.presentation.components.DropdownMenuWithUpdateAndDelete
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyThemePreview
import co.ke.xently.libraries.ui.core.components.shimmer

@Composable
internal fun ManipulatableProductListItem(
    product: Product,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    onClickUpdate: () -> Unit,
    onClickConfirmDelete: () -> Unit,
) {
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            text = {
                Text(
                    text = stringResource(
                        R.string.message_confirm_product_deletion,
                        product
                    )
                )
            },
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

    ProductListItem(product = product, isLoading = isLoading, modifier = modifier) {
        var expanded by rememberSaveable { mutableStateOf(false) }

        Box(modifier = Modifier.shimmer(isLoading)) {
            Icon(
                Icons.Default.MoreVert,
                contentDescription = stringResource(
                    R.string.content_desc_more_options_for_product,
                    product,
                ),
                modifier = Modifier.clickable(
                    role = Role.Checkbox,
                    indication = ripple(bounded = false),
                    interactionSource = remember { MutableInteractionSource() },
                ) { expanded = !isLoading },
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
}

@XentlyThemePreview
@Composable
private fun ProductListItemPreview(
    @PreviewParameter(ProductListItemParameterProvider::class)
    parameter: ProductListItemParameter,
) {
    XentlyTheme {
        ManipulatableProductListItem(
            product = parameter.product,
            isLoading = parameter.isLoading,
            onClickUpdate = {},
            onClickConfirmDelete = {},
        )
    }
}