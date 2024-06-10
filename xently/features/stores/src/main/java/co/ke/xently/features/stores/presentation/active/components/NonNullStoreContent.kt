package co.ke.xently.features.stores.presentation.active.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import co.ke.xently.features.shops.data.domain.Shop
import co.ke.xently.features.stores.R
import co.ke.xently.features.stores.data.domain.Store
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.data.core.Link
import co.ke.xently.libraries.data.image.domain.ImageResponse
import co.ke.xently.libraries.ui.core.XentlyPreview

@Composable
internal fun NonNullStoreContent(
    store: Store,
    isImageUploading: Boolean,
    modifier: Modifier = Modifier,
    onClickEdit: () -> Unit,
    onClickMoreDetails: () -> Unit,
    onClickUploadImage: () -> Unit,
    onClickDeleteImage: (ImageResponse) -> Unit,
    onClickUpdateImage: (ImageResponse) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item(key = "store_summary") {
            StoreDetailContent(
                store = store,
                isImageUploading = isImageUploading,
                onClickEdit = onClickEdit,
                onClickMoreDetails = onClickMoreDetails,
                onClickUploadImage = onClickUploadImage,
            )
        }
        if (store.images.isEmpty()) {
            item(key = "empty_store_images") {
                Spacer(modifier = Modifier.height(24.dp))
                EmptyStoreImageListContent(modifier = Modifier.fillMaxWidth())
            }
        } else {
            items(store.images, key = { image -> image.url() }) { image ->
                StoreImageListItem(
                    image = image,
                    isLoading = false,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    onClickUpdate = { onClickUpdateImage(image) },
                    onClickConfirmDelete = { onClickDeleteImage(image) },
                )
            }
        }
    }
}

@Composable
private fun StoreDetailContent(
    store: Store,
    isImageUploading: Boolean,
    modifier: Modifier = Modifier,
    onClickEdit: () -> Unit,
    onClickMoreDetails: () -> Unit,
    onClickUploadImage: () -> Unit,
) {
    Column(modifier = modifier) {
        StoreSummaryListItem(store = store, onClickEdit = onClickEdit)

        if (!store.description.isNullOrBlank()) {
            val isExpanded by rememberSaveable { mutableStateOf(false) }
            Text(
                text = store.description!!,
                maxLines = if (isExpanded) Int.MAX_VALUE else 4,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clickable { isExpanded != isExpanded },
            )
        }
        TextButton(
            modifier = Modifier.padding(horizontal = 16.dp),
            onClick = onClickMoreDetails,
            contentPadding = PaddingValues(vertical = 12.dp),
            content = {
                Text(
                    text = stringResource(R.string.action_more_details),
                    textDecoration = TextDecoration.Underline,
                )
            },
            shape = RoundedCornerShape(20),
            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onBackground),
        )

        OutlinedButton(
            enabled = !isImageUploading,
            onClick = onClickUploadImage,
            modifier = Modifier.padding(horizontal = 16.dp),
            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onBackground),
        ) {
            AnimatedVisibility(isImageUploading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(24.dp),
                )
            }
            Text(text = stringResource(R.string.action_upload_image))
            Spacer(Modifier.width(8.dp))
            Icon(Icons.Default.Add, contentDescription = null)
        }
    }
}

private data class NonNullStoreContentUiState(
    val store: Store,
    val isImageUploading: Boolean = false,
)

private class NonNullStoreContentUiStateParameterProvider :
    PreviewParameterProvider<NonNullStoreContentUiState> {
    private val store = Store(
        name = "Westlands",
        shop = Shop(name = "Ranalo K'Osewe"),
        description = "Short description about the business/hotel will go here. Lorem ipsum dolor trui loerm ipsum is a repetitive alternative place holder text for design projects.",
        images = List(10) {
            ImageResponse(links = mapOf("media" to Link(href = "https://picsum.photos/id/${it + 1}/200/300")))
        },
    )
    override val values: Sequence<NonNullStoreContentUiState>
        get() = sequenceOf(
            NonNullStoreContentUiState(store = store),
            NonNullStoreContentUiState(store = store, isImageUploading = true),
            NonNullStoreContentUiState(store = store.copy(images = emptyList())),
            NonNullStoreContentUiState(
                store = store.copy(images = emptyList()),
                isImageUploading = true,
            ),
        )
}

@XentlyPreview
@Composable
private fun NonNullStoreContentPreview(
    @PreviewParameter(NonNullStoreContentUiStateParameterProvider::class)
    state: NonNullStoreContentUiState,
) {
    XentlyTheme {
        NonNullStoreContent(
            modifier = Modifier.fillMaxSize(),
            store = state.store,
            isImageUploading = state.isImageUploading,
            onClickEdit = {},
            onClickMoreDetails = {},
            onClickUploadImage = {},
            onClickDeleteImage = {},
            onClickUpdateImage = {},
        )
    }
}