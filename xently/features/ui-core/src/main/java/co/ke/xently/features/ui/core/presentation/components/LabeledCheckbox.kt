package co.ke.xently.features.ui.core.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyPreview

@Composable
fun LabeledCheckbox(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    horizontalSpacing: Dp = 8.dp,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: @Composable RowScope.() -> Unit,
) {
    Surface(
        checked = checked,
        enabled = enabled,
        onCheckedChange = onCheckedChange,
        color = Color.Transparent,
    ) {
        Row(
            modifier = Modifier.then(modifier),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
        ) {
            Checkbox(
                checked = checked,
                enabled = enabled,
                onCheckedChange = onCheckedChange,
            )
            label()
        }
    }
}


@Composable
fun LabeledCheckbox(
    checked: Boolean,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    horizontalSpacing: Dp = 8.dp,
    onCheckedChange: (Boolean) -> Unit,
) {
    LabeledCheckbox(
        checked = checked,
        enabled = enabled,
        modifier = modifier,
        onCheckedChange = onCheckedChange,
        horizontalSpacing = horizontalSpacing,
    ) {
        Text(
            text = label,
            color = if (enabled) {
                Color.Unspecified
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            },
        )
    }
}

@XentlyPreview
@Composable
private fun LabeledCheckboxPreview(
    @PreviewParameter(ButtonStateParameterProvider::class)
    enabled: Boolean,
) {
    XentlyTheme {
        LabeledCheckbox(
            checked = enabled,
            label = "Enabled",
            onCheckedChange = {},
        )
    }
}