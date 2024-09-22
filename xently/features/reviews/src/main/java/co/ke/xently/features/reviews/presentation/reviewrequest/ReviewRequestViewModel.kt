package co.ke.xently.features.reviews.presentation.reviewrequest

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import co.ke.xently.features.reviewcategory.data.source.ReviewCategoryRepository
import co.ke.xently.features.reviews.data.domain.error.Result
import co.ke.xently.features.reviews.data.source.ReviewRepository
import co.ke.xently.features.reviews.presentation.utils.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class ReviewRequestViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: ReviewRepository,
    private val reviewCategoryRepository: ReviewCategoryRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReviewRequestUiState())
    val uiState: StateFlow<ReviewRequestUiState> = _uiState.asStateFlow()

    private val _event = Channel<ReviewRequestEvent>()
    val event: Flow<ReviewRequestEvent> = _event.receiveAsFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val reviewCategories = savedStateHandle.getStateFlow(
        key = "reviewCategoriesUrl",
        initialValue = "",
    ).flatMapLatest { reviewCategoriesUrl ->
        reviewCategoryRepository.findReviewCategories(url = reviewCategoriesUrl)
    }.cachedIn(viewModelScope)

    fun onAction(action: ReviewRequestAction) {
        when (action) {
            is ReviewRequestAction.ChangeMessage -> {
                _uiState.update {
                    val categorySubStates = it.getUpdatedSubCategoryState(action.categoryName) {
                        copy(message = action.message)
                    }
                    it.copy(categorySubStates = categorySubStates)
                }
            }

            is ReviewRequestAction.RequestMessageEdit -> {
                _uiState.update {
                    val categorySubStates = it.getUpdatedSubCategoryState(action.categoryName) {
                        copy(isEditRequested = true)
                    }
                    it.copy(categorySubStates = categorySubStates)
                }
            }

            is ReviewRequestAction.PostRating -> {
                viewModelScope.launch {
                    _uiState.update {
                        val categorySubStates = it.getUpdatedSubCategoryState(action.categoryName) {
                            copy(isPosting = true, error = null)
                        }
                        it.copy(categorySubStates = categorySubStates)
                    }
                    when (val result = repository.postRating(action.url, action.message)) {
                        is Result.Failure -> {
                            _uiState.update {
                                val categorySubStates =
                                    it.getUpdatedSubCategoryState(action.categoryName) {
                                        copy(isPosting = false, error = result.error.asUiText())
                                    }
                                it.copy(categorySubStates = categorySubStates)
                            }
                        }

                        is Result.Success -> {
                            _uiState.update {
                                val categorySubStates =
                                    it.getUpdatedSubCategoryState(action.categoryName) {
                                        copy(
                                            isPosting = false,
                                            error = null,
                                            isEditRequested = action.message.isNullOrBlank(),
                                        )
                                    }
                                it.copy(categorySubStates = categorySubStates)
                            }
                        }
                    }
                }
            }
        }
    }
}