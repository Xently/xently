package co.ke.xently.features.reviews.presentation.reviewrequest

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import co.ke.xently.features.reviewcategory.data.domain.ReviewCategory
import co.ke.xently.features.reviewcategory.data.source.ReviewCategoryRepository
import co.ke.xently.features.reviews.data.source.ReviewRepository
import co.ke.xently.libraries.pagination.data.XentlyPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
internal class ReviewRequestViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: ReviewRepository,
    private val reviewCategoryRepository: ReviewCategoryRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReviewRequestUiState())
    val uiState: StateFlow<ReviewRequestUiState> = _uiState.asStateFlow()

    private val _event = Channel<ReviewRequestEvent>()
    val event: Flow<ReviewRequestEvent> = _event.receiveAsFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val reviewCategories: Flow<PagingData<ReviewCategory>> = savedStateHandle.getStateFlow(
        key = "reviewCategoriesUrl",
        initialValue = "",
    ).flatMapLatest { reviewCategoriesUrl ->
        Pager(PagingConfig(pageSize = 20, enablePlaceholders = true)) {
            XentlyPagingSource(dataLookupKey = "myReviewCategoryRatingApiResponses") { url ->
                reviewCategoryRepository.findReviewCategories(
                    url = url ?: reviewCategoriesUrl,
                )
            }
        }.flow
    }.cachedIn(viewModelScope)

    fun onAction(action: ReviewRequestAction) {
        when (action) {
            is ReviewRequestAction.ChangeMessage -> {

            }

            is ReviewRequestAction.PostRating -> {

            }

            is ReviewRequestAction.RequestMessageEdit -> {

            }
        }
    }
}