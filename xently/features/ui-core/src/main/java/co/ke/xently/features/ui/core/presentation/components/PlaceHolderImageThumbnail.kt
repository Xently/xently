package co.ke.xently.features.ui.core.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyThemePreview

@Composable
fun PlaceHolderImageThumbnail(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    shape: Shape = CircleShape,
    paddingValues: PaddingValues = PaddingValues(12.dp),
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    contentColour: Color = MaterialTheme.colorScheme.surfaceVariant,
    content: @Composable BoxScope.() -> Unit,
) {
    Surface(
        modifier = Modifier
            .size(size)
            .then(modifier),
        shape = shape,
        color = color,
        contentColor = contentColour,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
        ) {
            content()
        }
    }
}

@XentlyThemePreview
@Composable
private fun PlaceHolderImageThumbnailPreview() {
    XentlyTheme {
        PlaceHolderImageThumbnail(size = 60.dp) {
            Icon(Icons.Default.Person, contentDescription = null)
        }
    }
}