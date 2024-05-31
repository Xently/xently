package co.ke.xently.libraries.ui.core.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import co.ke.xently.libraries.ui.core.XentlyPreview
import co.ke.xently.libraries.ui.core.theme.AppTheme


@Composable
fun NavigateBackIconButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    IconButton(modifier = modifier, onClick = onClick) {
        Icon(
            Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Go back",
        )
    }
}

@XentlyPreview
@Composable
private fun NavigateBackIconButtonPreview() {
    AppTheme(dynamicColor = false, colorScheme = { MaterialTheme.colorScheme }) {
        NavigateBackIconButton {}
    }
}