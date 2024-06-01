package co.ke.xently.features.ui.core.presentation.components

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

internal class ButtonStateParameterProvider : PreviewParameterProvider<Boolean> {
    override val values: Sequence<Boolean>
        get() = sequenceOf(false, true)
}