package co.ke.xently.features.reviewcategory.presentation.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.ke.xently.features.reviewcategory.data.domain.ReviewCategory
import co.ke.xently.features.reviewcategory.data.domain.error.Result
import co.ke.xently.features.reviewcategory.data.source.ReviewCategoryRepository
import co.ke.xently.features.reviewcategory.presentation.utils.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class ReviewCategoryEditDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: ReviewCategoryRepository,
) : ViewModel() {
    private companion object {
        private const val KEY =
            "co.ke.xently.features.reviewcategory.presentation.edit.SELECTED_REVIEW_CATEGORIES"
    }

    private val _uiState = MutableStateFlow(ReviewCategoryEditDetailUiState())
    val uiState: StateFlow<ReviewCategoryEditDetailUiState> = _uiState.asStateFlow()

    private val _event = Channel<ReviewCategoryEditDetailEvent>()
    val event: Flow<ReviewCategoryEditDetailEvent> = _event.receiveAsFlow()

    fun onAction(action: ReviewCategoryEditDetailAction) {
        when (action) {
            is ReviewCategoryEditDetailAction.SelectCategory -> {
                val reviewCategories = (savedStateHandle.get<Set<ReviewCategory>>(KEY)
                    ?: emptySet())

                savedStateHandle[KEY] = reviewCategories + action.category
            }

            is ReviewCategoryEditDetailAction.RemoveCategory -> {
                val reviewCategories = (savedStateHandle.get<Set<ReviewCategory>>(KEY)
                    ?: emptySet())
                savedStateHandle[KEY] = reviewCategories - action.category
            }

            is ReviewCategoryEditDetailAction.ChangeName -> {
                _uiState.update {
                    it.copy(name = action.name)
                }
            }

            ReviewCategoryEditDetailAction.ClickSaveDetails -> {
                viewModelScope.launch {
                    val state = _uiState.updateAndGet {
                        it.copy(isLoading = true)
                    }
                    val reviewCategory = state.reviewCategory.copy(
                        name = state.name,
                    )
                    when (val result = repository.save(reviewCategory = reviewCategory)) {
                        is Result.Failure -> {
                            _event.send(
                                ReviewCategoryEditDetailEvent.Error(
                                    result.error.asUiText(),
                                    result.error,
                                )
                            )
                        }

                        is Result.Success -> {
                            _event.send(ReviewCategoryEditDetailEvent.Success)
                        }
                    }
                }.invokeOnCompletion {
                    _uiState.update {
                        it.copy(isLoading = false)
                    }
                }
            }
        }
    }
}