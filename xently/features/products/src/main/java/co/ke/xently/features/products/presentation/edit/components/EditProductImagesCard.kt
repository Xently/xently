package co.ke.xently.features.products.presentation.edit.components


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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.data.image.domain.Image
import co.ke.xently.libraries.ui.core.XentlyThemePreview

typealias Index = Int

@Composable
internal fun EditProductImagesCard(
    images: List<Image?>,
    modifier: Modifier = Modifier,
    shape: Shape = CardDefaults.shape,
    onClickImage: (Index) -> Unit,
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
                key = { index, image -> image?.key() ?: ">>>$index<<<" },
            ) { index, upload ->
                EditProductImageCard(
                    image = upload,
                    onClickImage = { onClickImage(index) },
                    onClickRemoveImage = { onClickRemoveImage(index) },
                    modifier = Modifier.width(IntrinsicSize.Min),
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
            onClickImage = {},
            onClickRemoveImage = {},
        )
    }
}