package co.ke.xently.features.products.presentation.list.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import co.ke.xently.features.products.R
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyThemePreview

@Composable
internal fun ProductListEmptyState(
    message: String,
    modifier: Modifier = Modifier,
    canRetry: Boolean = true,
    onClickRetry: () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painterResource(co.ke.xently.features.ui.core.R.drawable.empty),
            contentDescription = null,
            modifier = Modifier.size(150.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        if (canRetry) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onClickRetry) {
                Text(text = stringResource(R.string.action_retry))
            }
        }
    }
}

private class ProductListEmptyStatePreviewParameter : PreviewParameterProvider<Boolean> {
    override val values: Sequence<Boolean>
        get() = sequenceOf(true, false)
}

@XentlyThemePreview
@Composable
private fun ProductListEmptyStatePreview(
    @PreviewParameter(ProductListEmptyStatePreviewParameter::class)
    canRetry: Boolean,
) {
    XentlyTheme {
        ProductListEmptyState(
            message = "Example message",
            canRetry = canRetry,
            modifier = Modifier.padding(16.dp),
            onClickRetry = {},
        )
    }
}