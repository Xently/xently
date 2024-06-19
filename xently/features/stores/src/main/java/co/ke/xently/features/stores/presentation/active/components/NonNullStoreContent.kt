package co.ke.xently.features.stores.presentation.active.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
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
import co.ke.xently.libraries.data.image.domain.File
import co.ke.xently.libraries.data.image.domain.Progress
import co.ke.xently.libraries.data.image.domain.UploadRequest
import co.ke.xently.libraries.data.image.domain.UploadResponse
import co.ke.xently.libraries.ui.core.XentlyPreview
import co.ke.xently.libraries.ui.image.presentation.imageState
import com.valentinilk.shimmer.shimmer

@Composable
internal fun NonNullStoreContent(
    store: Store,
    isImageUploading: Boolean,
    images: List<File>,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    onClickEdit: () -> Unit,
    onClickMoreDetails: () -> Unit,
    onClickUploadImage: () -> Unit,
    onClickDeleteImage: (Int) -> Unit,
    withImageUpdateResult: (Pair<Int, File>) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item(key = "store_summary", contentType = "store_summary") {
            StoreDetailContent(
                store = store,
                isImageUploading = isImageUploading,
                onClickEdit = onClickEdit,
                onClickMoreDetails = onClickMoreDetails,
                onClickUploadImage = onClickUploadImage,
                modifier = if (isLoading) Modifier.shimmer() else Modifier,
            )
        }
        if (images.isEmpty()) {
            item(key = "empty_store_images", contentType = "empty_store_images") {
                Spacer(modifier = Modifier.height(24.dp))
                EmptyStoreImageListContent(modifier = Modifier.fillMaxWidth())
            }
        } else {
            itemsIndexed(
                images,
                key = { index, _ -> index },
                contentType = { _, _ -> "store-images" },
            ) { index, img ->
                var imageUri by remember(index) { mutableStateOf<Uri?>(null) }
                val pickMedia = rememberLauncherForActivityResult(PickVisualMedia()) {
                    imageUri = it
                }
                val image by imageUri.imageState(img)

                LaunchedEffect(index, image) {
                    withImageUpdateResult(index to image!!)
                    when (image!!) {
                        is UploadResponse, is Progress -> Unit
                        is File.Error, is UploadRequest -> imageUri = null
                    }
                }

                StoreImageListItem(
                    image = image!!,
                    isLoading = false,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .run { if (isLoading) shimmer() else this },
                    onClickConfirmDelete = { onClickDeleteImage(index) },
                    onClickUpdate = { pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly)) },
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
            var expand by rememberSaveable { mutableStateOf(false) }
            Text(
                text = store.description!!,
                maxLines = if (expand) Int.MAX_VALUE else 4,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clickable(
                        role = Role.Checkbox,
                        indication = ripple(radius = 1_000.dp),
                        interactionSource = remember { MutableInteractionSource() },
                    ) { expand = !expand },
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
        description = """Short description about the business/hotel will go here.
                |Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
                |
                |Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem.""".trimMargin(),
        images = List(10) {
            UploadResponse(links = mapOf("media" to Link(href = "https://picsum.photos/id/${it + 1}/200/300")))
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
            images = state.store.images,
            isImageUploading = state.isImageUploading,
            onClickEdit = {},
            onClickMoreDetails = {},
            onClickUploadImage = {},
            onClickDeleteImage = {},
            withImageUpdateResult = {},
        )
    }
}