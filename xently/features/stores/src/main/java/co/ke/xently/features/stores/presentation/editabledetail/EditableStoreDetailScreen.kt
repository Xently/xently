package co.ke.xently.features.stores.presentation.editabledetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import co.ke.xently.features.stores.R
import co.ke.xently.features.stores.data.domain.error.DataError
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyPreview

@Composable
fun EditableStoreDetailScreen(
    state: EditableStoreDetailUiState,
    event: EditableStoreDetailEvent?,
    modifier: Modifier = Modifier,
    onAction: (EditableStoreDetailAction) -> Unit,
    onClickBack: () -> Unit,
    onClickSignIn: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    LaunchedEffect(event) {
        when (event) {
            null -> Unit
            is EditableStoreDetailEvent.Error -> {
                val result = snackbarHostState.showSnackbar(
                    event.error.asString(context = context),
                    duration = SnackbarDuration.Long,
                    actionLabel = if (event.type is DataError.Network) {
                        context.getString(R.string.action_retry)
                    } else {
                        null
                    },
                )

                when (result) {
                    SnackbarResult.Dismissed -> {

                    }

                    SnackbarResult.ActionPerformed -> {

                    }
                }
            }

            EditableStoreDetailEvent.Success -> onClickBack()
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {

        }
    }
}


private class EditableStoreDetailUiStateParameterProvider :
    PreviewParameterProvider<EditableStoreDetailUiState> {
    override val values: Sequence<EditableStoreDetailUiState>
        get() = sequenceOf(
            EditableStoreDetailUiState(),
            EditableStoreDetailUiState(
                isLoading = true,
            ),
        )
}

@XentlyPreview
@Composable
private fun EditableStoreDetailScreenPreview(
    @PreviewParameter(EditableStoreDetailUiStateParameterProvider::class)
    state: EditableStoreDetailUiState,
) {
    XentlyTheme {
        EditableStoreDetailScreen(
            state = state,
            event = null,
            modifier = Modifier.fillMaxSize(),
            onAction = {},
            onClickBack = {},
            onClickSignIn = {},
        )
    }
}
