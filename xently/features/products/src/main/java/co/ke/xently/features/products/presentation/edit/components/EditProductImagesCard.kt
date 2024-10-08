package co.ke.xently.features.products.presentation.edit.components


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.data.image.domain.File
import co.ke.xently.libraries.data.image.domain.Progress
import co.ke.xently.libraries.data.image.domain.UploadRequest
import co.ke.xently.libraries.data.image.domain.UploadResponse
import co.ke.xently.libraries.ui.core.XentlyThemePreview
import co.ke.xently.libraries.ui.image.presentation.imageState

typealias Index = Int
typealias IndexAndImagePair = Pair<Int, File?>

@Composable
internal fun EditProductImagesCard(
    images: List<File?>,
    modifier: Modifier = Modifier,
    shape: Shape = CardDefaults.shape,
    withResult: (IndexAndImagePair) -> Unit,
    onClickRemoveImage: (Index) -> Unit,
) {
    Card(modifier = modifier, shape = shape) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp),
        ) {
            itemsIndexed(
                images,
                contentType = { _, _ -> "product-image" },
                key = { index, _ -> index },
            ) { index, upload ->
                var imageUri by remember(index) { mutableStateOf<Uri?>(null) }
                val pickMedia = rememberLauncherForActivityResult(PickVisualMedia()) {
                    imageUri = it
                }
                val image by imageUri.imageState(initialValue = upload)

                LaunchedEffect(index, image) {
                    withResult(index to image)
                    when (image) {
                        is UploadResponse, is Progress, null -> Unit
                        is File.Error, is UploadRequest -> imageUri = null
                    }
                }

                EditProductImageCard(
                    image = image,
                    modifier = Modifier.width(IntrinsicSize.Min),
                    onClickRemoveImage = { onClickRemoveImage(index) },
                    onClickImage = { pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly)) },
                )
            }
        }
    }
}

@XentlyThemePreview
@Composable
private fun ProductImagesCardPreview() {
    XentlyTheme {
        EditProductImagesCard(
            images = remember { List(5) { null } },
            withResult = {},
            onClickRemoveImage = {},
        )
    }
}