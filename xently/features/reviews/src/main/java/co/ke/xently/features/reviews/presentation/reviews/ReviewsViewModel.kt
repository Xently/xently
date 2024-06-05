package co.ke.xently.features.reviews.presentation.reviews

import androidx.lifecycle.ViewModel
import co.ke.xently.features.reviews.data.source.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
internal class ReviewsViewModel @Inject constructor(
    private val repository: ReviewRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReviewsUiState(null, null, emptyList()))
    val uiState: StateFlow<ReviewsUiState> = _uiState.asStateFlow()

    private val _event = Channel<ReviewsEvent>()
    val event: Flow<ReviewsEvent> = _event.receiveAsFlow()

    private val _statisticsResponse = Channel<StatisticsResponse>()
    val statisticsResponse: Flow<StatisticsResponse> = _statisticsResponse.receiveAsFlow()

    fun onAction(action: ReviewsAction) {
        when (action) {
            is ReviewsAction.SelectReviewCategory -> {
                _uiState.update {
                    it.copy(selectedCategory = action.category)
                }
            }
        }
    }
}