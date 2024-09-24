package co.ke.xently.features.stores.presentation.list.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.ui.Alignment
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
import co.ke.xently.libraries.ui.image.XentlyImage
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material3.fade
import com.google.accompanist.placeholder.material3.placeholder

@Composable
internal fun StoreListItem(
    store: Store,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    onClick: () -> Unit = {},
    onClickUpdate: () -> Unit,
    onClickConfirmDelete: () -> Unit,
) {
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            text = { Text(text = stringResource(R.string.message_confirm_store_deletion, store)) },
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

    StoreItemCard(
        store = store,
        modifier = modifier,
        isLoading = isLoading,
        onClick = onClick,
    ) { (expanded, onClose) ->
        DropdownMenuWithUpdateAndDelete(
            expanded = expanded,
            onExpandChanged = { onClose() },
            onClickUpdate = { onClickUpdate(); onClose() },
            onClickDelete = {
                showDeleteDialog = true
                onClose()
            },
        )
    }
}

@Composable
fun StoreListItem(
    store: Store,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    trailingHeadlineContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
) {
    ListItem(
        modifier = modifier,
        trailingContent = trailingContent,
        leadingContent = {
            Box {
                Card(
                    modifier = Modifier
                        .size(size = 60.dp)
                        .placeholder(
                            visible = isLoading,
                            highlight = PlaceholderHighlight.fade(),
                        ),
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
                if (store.isActivated) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = stringResource(R.string.content_desc_activated_store),
                        modifier = Modifier.align(Alignment.BottomEnd),
                    )
                }
            }
        },
        supportingContent = {
            var maxLines by rememberSaveable { mutableIntStateOf(1) }
            Text(
                text = store.name,
                fontWeight = FontWeight.Light,
                overflow = TextOverflow.Ellipsis,
                maxLines = maxLines,
                modifier = Modifier
                    .placeholder(visible = isLoading, highlight = PlaceholderHighlight.fade())
                    .clickable(
                        role = Role.Checkbox,
                        indication = ripple(radius = 1_000.dp),
                        interactionSource = remember { MutableInteractionSource() },
                    ) { maxLines = if (maxLines == 1) Int.MAX_VALUE else 1 },
            )
        },
        headlineContent = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = store.shop.name,
                    modifier = Modifier
                        .weight(1f)
                        .placeholder(
                            visible = isLoading,
                            highlight = PlaceholderHighlight.fade(),
                        ),
                    maxLines = 1,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                )

                trailingHeadlineContent?.invoke()
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
            StoreListItemParameter(
                Store.DEFAULT.copy(isActivated = true),
            ),
            StoreListItemParameter(
                Store.DEFAULT.copy(isActivated = true),
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