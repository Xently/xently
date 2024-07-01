package co.ke.xently.features.reviews.presentation.reviewrequest.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import co.ke.xently.features.reviewcategory.data.domain.ReviewCategory
import co.ke.xently.features.reviews.R
import co.ke.xently.features.reviews.presentation.components.StarRatingRow
import co.ke.xently.features.reviews.presentation.reviewrequest.ReviewRequestAction
import co.ke.xently.features.reviews.presentation.reviewrequest.ReviewRequestAction.PostRating
import co.ke.xently.features.reviews.presentation.reviewrequest.ReviewRequestAction.RequestMessageEdit
import co.ke.xently.features.reviews.presentation.reviewrequest.ReviewRequestUiState
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyThemePreview
import co.ke.xently.libraries.ui.core.theme.LocalThemeIsDark
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.days


@Composable
private fun rememberCategoryQuestion(name: String, question: String?): AnnotatedString {
    return remember(question, name) {
        buildAnnotatedString {
            if (!question.isNullOrBlank()) {
                val (prefix, suffix) = question.split(
                    name.lowercase(),
                    limit = 2,
                    ignoreCase = true,
                )
                append(prefix)
                pushStyle(
                    SpanStyle(
                        fontWeight = FontWeight.ExtraBold,
                        textDecoration = TextDecoration.Underline,
                    )
                )
                append(name.toLowerCase(Locale.current))
                pop()
                append(suffix)
            } else {
                append("How do you rate the ")
                pushStyle(
                    SpanStyle(
                        fontWeight = FontWeight.ExtraBold,
                        textDecoration = TextDecoration.Underline,
                    )
                )
                append(name.toLowerCase(Locale.current))
                pop()
                append("?")
            }
        }
    }
}

@Composable
internal fun ReviewCategoryCard(
    category: ReviewCategory,
    index: Int,
    state: ReviewRequestUiState,
    onAction: (ReviewRequestAction) -> Unit,
) {
    val isDark by LocalThemeIsDark.current
    val focusManager = LocalFocusManager.current

    OutlinedCard {
        Text(
            fontWeight = FontWeight.Bold,
            text = rememberCategoryQuestion(category.name, category.question),
            modifier = Modifier
                .padding(top = 16.dp)
                .padding(horizontal = 16.dp),
        )

        val subState by remember(index, category.name, state.categorySubStates) {
            derivedStateOf {
                state.categorySubStates[category.name]
                    ?: ReviewRequestUiState.SubState()
            }
        }

        AnimatedVisibility(subState.isPosting) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        Spacer(modifier = Modifier.height(8.dp))

        var message by rememberSaveable(index, category.myRatingMessage) {
            mutableStateOf(category.myRatingMessage ?: "")
        }
        var selectedStarRating by rememberSaveable(index, category.myRating) {
            mutableIntStateOf(category.myRating)
        }
        StarRatingRow(
            isDark = isDark,
            average = animateFloatAsState(
                selectedStarRating.toFloat(),
                label = "star-ratings",
            ).value,
            maximumStarRating = 5,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            iconSize = 56.dp,
            horizontalArrangement = Arrangement.SpaceBetween,
            onClick = {
                selectedStarRating = it
                val url = category.getReviewPostingUrl(it)
                onAction(
                    PostRating(
                        categoryName = category.name,
                        url = url,
                        message = message.takeIf(String::isNotBlank)
                    ),
                )
            },
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Tap a star to rate publicly",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Light,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
        )

        val hostState = remember(index) { SnackbarHostState() }

        SnackbarHost(hostState = hostState)

        val context = LocalContext.current
        val error = subState.error
        LaunchedEffect(index, error) {
            if (error != null) {
                hostState.showSnackbar(
                    error.asString(context = context),
                    duration = SnackbarDuration.Long,
                )
            }
        }

        val activateEditMode by remember(
            index,
            selectedStarRating,
            message,
            subState.isEditRequested,
        ) {
            derivedStateOf {
                (selectedStarRating > 0 && message.isBlank())
                        || (selectedStarRating > 0 && subState.isEditRequested)
            }
        }

        if (activateEditMode) {
            Spacer(modifier = Modifier.height(8.dp))
            val onSendMessageClick by rememberUpdatedState {
                val url = category.getReviewPostingUrl(selectedStarRating)
                onAction(PostRating(category.name, url, message))
                focusManager.clearFocus()
            }
            OutlinedTextField(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                value = message,
                onValueChange = { message = it },
                placeholder = { Text("Optional description...") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Send,
                ),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (message.isNotBlank()) {
                            onSendMessageClick()
                        }
                    },
                ),
                trailingIcon = {
                    val enabled by remember(selectedStarRating, message, subState.isPosting) {
                        derivedStateOf {
                            selectedStarRating > 0
                                    && message.isNotBlank()
                                    && !subState.isPosting
                        }
                    }
                    IconButton(enabled = enabled, onClick = onSendMessageClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = stringResource(
                                R.string.content_desc_send_review,
                                category.name,
                            ),
                        )
                    }
                }
            )
        } else if (message.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            ListItem(
                headlineContent = { Text(text = message) },
                trailingContent = {
                    IconButton(onClick = { onAction(RequestMessageEdit(category.name)) }) {
                        Icon(
                            Icons.Outlined.Edit,
                            contentDescription = stringResource(
                                R.string.content_desc_edit_review,
                                category.name,
                            ),
                        )
                    }
                },
            )
        }
        Spacer(modifier = Modifier.height(height = 16.dp))
    }
}

private data class ReviewCategoryState(
    val category: ReviewCategory,
    val state: ReviewRequestUiState,
    val index: Int = 0,
)

private class ReviewCategoryStatePreviewProvider : PreviewParameterProvider<ReviewCategoryState> {
    override val values: Sequence<ReviewCategoryState>
        get() {
            val category = ReviewCategory(
                name = "Staff friendliness",
                question = "How do you rate the Staff friendliness?",
                myRating = 5,
                myRatingMessage = "This is a test message",
                averageStarRating = 4.5,
                myLatestRatingDate = Clock.System.now().minus(20.days),
            )
            val subState = ReviewRequestUiState.SubState(
                isEditRequested = true,
                message = "This is a test message",
            )
            return sequenceOf(
                ReviewCategoryState(
                    category = category.copy(
                        question = null,
                    ),
                    state = ReviewRequestUiState(
                        isLoading = true,
                        categorySubStates = mapOf(
                            category.name to subState,
                        ),
                    ),
                ),
                ReviewCategoryState(
                    category = category.copy(
                        question = null,
                        myRatingMessage = null,
                        myRating = 0,
                        myLatestRatingDate = null,
                    ),
                    state = ReviewRequestUiState(
                        isLoading = true,
                        categorySubStates = mapOf(
                            category.name to subState,
                        ),
                    ),
                ),
                ReviewCategoryState(
                    category = category.copy(
                        myRatingMessage = null,
                        myRating = 0,
                        myLatestRatingDate = null,
                    ),
                    state = ReviewRequestUiState(
                        isLoading = true,
                        categorySubStates = mapOf(
                            category.name to subState,
                        ),
                    ),
                ),
                ReviewCategoryState(
                    category = category.copy(
                        myRatingMessage = null,
                    ),
                    state = ReviewRequestUiState(
                        isLoading = true,
                        categorySubStates = mapOf(
                            category.name to subState,
                        ),
                    ),
                ),
                ReviewCategoryState(
                    category = category,
                    state = ReviewRequestUiState(
                        isLoading = true,
                        categorySubStates = mapOf(
                            category.name to subState,
                        ),
                    ),
                ),
                ReviewCategoryState(
                    category = category,
                    state = ReviewRequestUiState(
                        isLoading = false,
                        categorySubStates = mapOf(
                            category.name to subState,
                        ),
                    ),
                ),
                ReviewCategoryState(
                    category = category,
                    state = ReviewRequestUiState(
                        isLoading = true,
                        categorySubStates = mapOf(
                            category.name to subState.copy(isEditRequested = false),
                        ),
                    ),
                ),
                ReviewCategoryState(
                    category = category,
                    state = ReviewRequestUiState(
                        isLoading = false,
                        categorySubStates = mapOf(
                            category.name to subState.copy(isEditRequested = false),
                        ),
                    ),
                ),
            )
        }
}

@XentlyThemePreview
@Composable
private fun ReviewCategoryCardPreview(
    @PreviewParameter(ReviewCategoryStatePreviewProvider::class)
    state: ReviewCategoryState,
) {
    XentlyTheme {
        ReviewCategoryCard(
            index = state.index,
            state = state.state,
            category = state.category,
            onAction = {},
        )
    }
}