package co.ke.xently.features.products.presentation.edit.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import co.ke.xently.features.products.R
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.data.core.Link
import co.ke.xently.libraries.ui.core.XentlyThemePreview
import co.ke.xently.libraries.ui.image.XentlyImage
import co.ke.xently.libraries.ui.image.domain.Upload
import coil3.toUri

@Composable
internal fun EditProductImageCard(
    image: Upload?,
    modifier: Modifier = Modifier,
    onClickImage: () -> Unit,
    onClickRemoveImage: () -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ElevatedCard(modifier = Modifier.size(150.dp), onClick = onClickImage) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                if (image == null) {
                    Icon(
                        Icons.Default.AddCircleOutline,
                        contentDescription = stringResource(R.string.content_desc_upload_image),
                    )
                } else {
                    XentlyImage(
                        data = image,
                        modifier = Modifier.matchParentSize(),
                    )
                    if (image is Upload.Progress) {
                        CircularProgressIndicator()
                    }
                }
            }
        }

        TextButton(
            onClick = onClickRemoveImage,
            enabled = remember(image) {
                derivedStateOf {
                    image != null
                            && image !is Upload.Progress
                }
            }.value,
        ) { Text(stringResource(R.string.action_remove)) }
    }
}

private class ImageParameterProvider : PreviewParameterProvider<Upload?> {
    override val values: Sequence<Upload?>
        get() = sequenceOf(
            Upload.Error.FileTooLargeError(2_000, 4_000),
            null,
            Upload.Response(
                links = mapOf("media" to Link(href = "https://example.com/image.jpg")),
            ),
            Upload.Progress(45, 100),
            Upload.Request(
                uri = "".toUri(),
                fileSize = 100,
                mimeType = "image/jpeg",
                fileName = "Example.jpg",
            )
        )
}

@XentlyThemePreview
@Composable
private fun ProductImageCardPreview(
    @PreviewParameter(ImageParameterProvider::class)
    image: Upload?,
) {
    XentlyTheme {
        EditProductImageCard(
            image = image,
            onClickImage = {},
            onClickRemoveImage = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}