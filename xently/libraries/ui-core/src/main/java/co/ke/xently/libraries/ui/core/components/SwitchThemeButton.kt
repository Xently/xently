package co.ke.xently.libraries.ui.core.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.ke.xently.libraries.ui.core.XentlyPreview
import co.ke.xently.libraries.ui.core.theme.AppTheme
import co.ke.xently.libraries.ui.core.theme.LocalThemeIsDark


@Composable
fun SwitchThemeIconButton(modifier: Modifier = Modifier) {
    var isDark by LocalThemeIsDark.current
    IconButton(modifier = modifier, onClick = { isDark = !isDark }) {
        Icon(
            modifier = Modifier
                .padding(8.dp)
                .size(20.dp),
            imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
            contentDescription = if (isDark) "Switch to light theme" else "Switch to dark theme",
        )
    }
}

@XentlyPreview
@Composable
private fun SwitchThemeIconButtonPreview() {
    AppTheme(dynamicColor = false, colorScheme = { MaterialTheme.colorScheme }) {
        SwitchThemeIconButton()
    }
}