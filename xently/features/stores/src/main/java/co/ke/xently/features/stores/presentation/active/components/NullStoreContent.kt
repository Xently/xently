package co.ke.xently.features.stores.presentation.active.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import co.ke.xently.features.stores.R
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyPreview

@Composable
internal fun NullStoreContent(
    modifier: Modifier,
    isShopSelected: Boolean,
    onClickSelectShop: () -> Unit,
    onClickSelectBranch: () -> Unit,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp),
        ) {
            Image(
                painterResource(co.ke.xently.features.ui.core.R.drawable.empty),
                contentDescription = null,
                modifier = Modifier.size(256.dp),
            )
            if (isShopSelected) {
                Text(
                    text = stringResource(R.string.error_select_branch),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Button(onClick = onClickSelectBranch) {
                    Text(text = stringResource(R.string.action_select_branch))
                }
            } else {
                Text(
                    text = stringResource(R.string.error_select_shop),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Button(onClick = onClickSelectShop) {
                    Text(text = stringResource(R.string.action_select_shop))
                }
            }
        }
    }
}

private class NullStoreContentParameterProvider : PreviewParameterProvider<Boolean> {
    override val values: Sequence<Boolean>
        get() = sequenceOf(true, false)
}

@XentlyPreview
@Composable
private fun NullStoreContentPreview(
    @PreviewParameter(NullStoreContentParameterProvider::class)
    shopSelected: Boolean,
) {
    XentlyTheme {
        NullStoreContent(
            isShopSelected = shopSelected,
            onClickSelectShop = {},
            onClickSelectBranch = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}