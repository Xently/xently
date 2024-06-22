package co.ke.xently.features.stores.presentation.list.components

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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import co.ke.xently.features.stores.R
import co.ke.xently.features.stores.data.domain.Store
import co.ke.xently.features.ui.core.presentation.components.DropdownMenuWithUpdateAndDelete
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyThemePreview
import co.ke.xently.libraries.ui.core.components.shimmer
import co.ke.xently.libraries.ui.image.XentlyImage

@Composable
internal fun StoreListItem(
    store: Store,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    onClickUpdate: () -> Unit,
    onClickConfirmDelete: () -> Unit,
) {
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            text = { Text(text = """Are you sure you want to delete the "$store"?""") },
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
            Card(
                modifier = Modifier
                    .size(size = 60.dp)
                    .shimmer(isLoading),
            ) {
                var index by rememberSaveable(store.id) { mutableIntStateOf(0) }
                XentlyImage(
                    data = store.images.getOrNull(index),
                    modifier = Modifier.fillMaxSize(),
                    onError = {
                        if (index != store.images.lastIndex) index += 1
                    },
                )
            }
        },
        supportingContent = {
            Text(
                text = store.name,
                modifier = Modifier.shimmer(isLoading),
            )
        },
        headlineContent = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = store.shop.name,
                    modifier = Modifier
                        .weight(1f)
                        .shimmer(isLoading),
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                )
                var expanded by rememberSaveable { mutableStateOf(false) }

                Box(modifier = Modifier.shimmer(isLoading)) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = """More options for store "${store.name}, ${store.shop.name}".""",
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
        },
    )
}

private data class StoreListItemParameter(
    val store: Store,
    val isLoading: Boolean = false,
)

private class StoreListItemParameterProvider : PreviewParameterProvider<StoreListItemParameter> {
    override val values: Sequence<StoreListItemParameter>
        get() = sequenceOf(
            StoreListItemParameter(
                Store.DEFAULT
            ),
            StoreListItemParameter(
                Store.DEFAULT,
                isLoading = true,
            ),
        )
}

@XentlyThemePreview
@Composable
private fun StoreListItemPreview(
    @PreviewParameter(StoreListItemParameterProvider::class)
    parameter: StoreListItemParameter,
) {
    XentlyTheme {
        StoreListItem(
            store = parameter.store,
            isLoading = parameter.isLoading,
            onClickUpdate = {},
            onClickConfirmDelete = {},
        )
    }
}