package co.ke.xently.features.reviewcategory.presentation.edit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.waterfall
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.ke.xently.features.reviewcategory.R
import co.ke.xently.features.reviewcategory.data.domain.ReviewCategory
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyPreview
import co.ke.xently.libraries.ui.core.components.NavigateBackIconButton

@Composable
fun ReviewCategoryEditDetailScreen(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
) {
    val viewModel = hiltViewModel<ReviewCategoryEditDetailViewModel>()

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.event.collect { event ->
            when (event) {
                is ReviewCategoryEditDetailEvent.Success -> {
                    when (event.action) {
                        ReviewCategoryEditDetailAction.ClickSave -> onClickBack()
                        ReviewCategoryEditDetailAction.ClickSaveAndAddAnother -> {
                            viewModel.onAction(ReviewCategoryEditDetailAction.ChangeName(""))
                            snackbarHostState.showSnackbar(
                                message = context.getString(R.string.message_review_category_saved),
                                duration = SnackbarDuration.Short,
                            )
                        }

                        else -> throw NotImplementedError()
                    }
                }

                is ReviewCategoryEditDetailEvent.Error -> {
                    snackbarHostState.showSnackbar(
                        event.error.asString(context = context),
                        duration = SnackbarDuration.Long,
                    )
                }
            }
        }
    }

    ReviewCategoryEditDetailScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
        onClickBack = onClickBack,
        onAction = viewModel::onAction,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ReviewCategoryEditDetailScreen(
    state: ReviewCategoryEditDetailUiState,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
    onAction: (ReviewCategoryEditDetailAction) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Column(modifier = Modifier.windowInsetsPadding(TopAppBarDefaults.windowInsets)) {
                CenterAlignedTopAppBar(
                    windowInsets = WindowInsets.waterfall,
                    title = { Text(text = stringResource(R.string.top_bar_title_edit_review_category_details)) },
                    navigationIcon = { NavigateBackIconButton(onClick = onClickBack) },
                )
                AnimatedVisibility(state.isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                    )
                }
            }
        },
    ) { paddingValues ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(paddingValues)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OutlinedTextField(
                shape = CardDefaults.shape,
                value = state.name,
                enabled = !state.disableFields,
                onValueChange = { onAction(ReviewCategoryEditDetailAction.ChangeName(it)) },
                label = { Text(text = stringResource(R.string.text_field_label_review_category_name)) },
                singleLine = true,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Words,
                ),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedButton(
                    enabled = state.enableSaveButton,
                    modifier = Modifier.weight(1f),
                    onClick = { onAction(ReviewCategoryEditDetailAction.ClickSave) },
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                ) { Text(text = stringResource(R.string.action_save)) }
                Button(
                    enabled = state.enableSaveButton,
                    onClick = { onAction(ReviewCategoryEditDetailAction.ClickSaveAndAddAnother) },
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                ) { Text(text = stringResource(R.string.action_save_and_add_another)) }
            }
        }
    }
}

private class ReviewCategoryEditDetailScreenUiState(
    val state: ReviewCategoryEditDetailUiState,
)

private class ReviewCategoryEditDetailUiStateParameterProvider :
    PreviewParameterProvider<ReviewCategoryEditDetailScreenUiState> {
    private val reviewCategory = ReviewCategory(
        name = "Example review category name",
    )
    override val values: Sequence<ReviewCategoryEditDetailScreenUiState>
        get() = sequenceOf(
            ReviewCategoryEditDetailScreenUiState(state = ReviewCategoryEditDetailUiState()),
            ReviewCategoryEditDetailScreenUiState(
                state = ReviewCategoryEditDetailUiState(
                    reviewCategory = reviewCategory
                )
            ),
            ReviewCategoryEditDetailScreenUiState(state = ReviewCategoryEditDetailUiState(isLoading = true)),
        )
}

@XentlyPreview
@Composable
private fun ReviewCategoryEditDetailScreenPreview(
    @PreviewParameter(ReviewCategoryEditDetailUiStateParameterProvider::class)
    state: ReviewCategoryEditDetailScreenUiState,
) {
    XentlyTheme {
        ReviewCategoryEditDetailScreen(
            state = state.state,
            snackbarHostState = remember {
                SnackbarHostState()
            },
            modifier = Modifier.fillMaxSize(),
            onClickBack = {},
            onAction = {},
        )
    }
}
