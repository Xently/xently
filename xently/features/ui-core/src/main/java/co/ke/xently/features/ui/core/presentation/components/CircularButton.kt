package co.ke.xently.features.ui.core.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyThemePreview

@Composable
fun CircularButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = CircleShape,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    contentColour: Color = MaterialTheme.colorScheme.surfaceVariant,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = Modifier
            .size(48.dp)
            .then(modifier),
        shape = shape,
        color = color,
        contentColor = contentColour,
        onClick = onClick,
        enabled = enabled
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(PaddingValues(12.dp))
                .fillMaxSize(),
        ) {
            content()
        }
    }
}

@XentlyThemePreview
@Composable
private fun CircularButtonPreview() {
    XentlyTheme {
        CircularButton(
            onClick = { },
            content = { Icon(Icons.Outlined.Edit, contentDescription = null) },
        )
    }
}