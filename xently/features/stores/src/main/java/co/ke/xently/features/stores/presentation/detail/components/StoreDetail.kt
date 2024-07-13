package co.ke.xently.features.stores.presentation.detail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import co.ke.xently.features.stores.R
import co.ke.xently.features.stores.data.domain.Store
import co.ke.xently.features.stores.presentation.detail.StoreDetailUiState
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyPreview
import co.ke.xently.libraries.ui.core.components.shimmer
import co.ke.xently.libraries.ui.core.rememberSnackbarHostState

@Composable
fun StoreDetail(
    state: StoreDetailUiState,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = rememberSnackbarHostState(),
    onClickMoreDetails: (Store) -> Unit,
    onGetPointsAndReviewClick: () -> Unit,
) {
    Column(modifier = modifier) {
        StoreImagesBox(
            modifier = Modifier.height(300.dp),
            images = state.store?.images ?: emptyList(),
        )

        Card(shape = RectangleShape) {
            StoreDetailListItem(
                store = state.store ?: Store.DEFAULT,
                isLoading = state.isLoading,
                snackbarHostState = snackbarHostState,
            )

            TextButton(
                shape = RectangleShape,
                contentPadding = PaddingValues(bottom = 12.dp),
                onClick = { state.store?.let(onClickMoreDetails) },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onBackground,
                ),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .shimmer(state.isLoading),
            ) {
                Text(
                    text = stringResource(R.string.action_more_details),
                    textDecoration = TextDecoration.Underline,
                )
            }
        }

        QrCodeCard(
            isLoading = state.isLoading,
            modifier = Modifier.padding(16.dp),
            onGetPointsAndReviewClick = onGetPointsAndReviewClick,
        )

        HorizontalDivider(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .padding(horizontal = 16.dp),
        )
    }
}

internal class StoreDetailUiStateParameterProvider : PreviewParameterProvider<StoreDetailUiState> {
    override val values: Sequence<StoreDetailUiState>
        get() = sequenceOf(
            StoreDetailUiState(isLoading = true),
            StoreDetailUiState(store = Store.DEFAULT),
        )
}

@XentlyPreview
@Composable
private fun StoreDetailPreview(
    @PreviewParameter(StoreDetailUiStateParameterProvider::class)
    state: StoreDetailUiState,
) {
    XentlyTheme {
        StoreDetail(
            state = state,
            onClickMoreDetails = {},
            onGetPointsAndReviewClick = {},
        )
    }
}
