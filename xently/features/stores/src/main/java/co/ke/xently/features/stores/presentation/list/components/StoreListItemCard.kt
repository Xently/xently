package co.ke.xently.features.stores.presentation.list.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.BookmarkRemove
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import co.ke.xently.features.stores.R
import co.ke.xently.features.stores.data.domain.Store
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyThemePreview
import co.ke.xently.libraries.ui.core.components.shimmer
import co.ke.xently.libraries.ui.image.XentlyImage

@Composable
internal fun StoreListItemCard(
    store: Store,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onClickToggleBookmark: () -> Unit,
) {
    Card(modifier = modifier, onClick = onClick, shape = MaterialTheme.shapes.large) {
        Row {
            ListItem(
                modifier = Modifier.weight(1f),
                headlineContent = {
                    Text(
                        text = store.shop.name,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.shimmer(isLoading),
                    )
                },
                supportingContent = {
                    val supportingTest = remember(store.name, store.categories) {
                        buildString {
                            store.categories.firstOrNull()?.name?.let { category ->
                                append(category)
                                append(" | ")
                            }
                            append(store.name)
                        }
                    }
                    Text(
                        text = supportingTest,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.shimmer(isLoading),
                    )
                },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            )
            var expanded by rememberSaveable {
                mutableStateOf(false)
            }

            Box {
                IconButton(
                    modifier = Modifier
                        .padding(8.dp)
                        .shimmer(isLoading),
                    onClick = {
                        expanded = !isLoading
                    },
                    content = {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.content_desc_more_options_for_store),
                        )
                    },
                )

                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    if (store.links.containsKey("add-bookmark")) {
                        DropdownMenuItem(
                            onClick = { onClickToggleBookmark(); expanded = false },
                            text = { Text(text = stringResource(R.string.action_add_bookmark)) },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.BookmarkAdd,
                                    contentDescription = stringResource(R.string.action_add_bookmark),
                                )
                            },
                        )
                    }
                    if (store.links.containsKey("remove-bookmark")) {
                        DropdownMenuItem(
                            onClick = { onClickToggleBookmark(); expanded = false },
                            text = { Text(text = stringResource(R.string.action_remove_bookmark)) },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.BookmarkRemove,
                                    contentDescription = stringResource(R.string.action_remove_bookmark),
                                )
                            },
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
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
    }
}

private data class StoreListItemCardParameter(
    val store: Store,
    val isLoading: Boolean = false,
)

private class StoreListItemCardParameterProvider :
    PreviewParameterProvider<StoreListItemCardParameter> {
    override val values: Sequence<StoreListItemCardParameter>
        get() = sequenceOf(
            StoreListItemCardParameter(
                Store.DEFAULT
            ),
            StoreListItemCardParameter(
                Store.DEFAULT,
                isLoading = true,
            ),
        )
}

@XentlyThemePreview
@Composable
private fun StoreListItemCardPreview(
    @PreviewParameter(StoreListItemCardParameterProvider::class)
    parameter: StoreListItemCardParameter,
) {
    XentlyTheme {
        StoreListItemCard(
            store = parameter.store,
            isLoading = parameter.isLoading,
            onClickToggleBookmark = {},
            onClick = {},
        )
    }
}