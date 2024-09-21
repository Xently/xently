package co.ke.xently.features.reviewcategory.data.source

import androidx.paging.PagingData
import co.ke.xently.features.reviewcategory.data.domain.ReviewCategory
import co.ke.xently.features.reviewcategory.data.domain.error.DataError
import co.ke.xently.features.reviewcategory.data.domain.error.Error
import co.ke.xently.features.reviewcategory.data.domain.error.Result
import kotlinx.coroutines.flow.Flow

interface ReviewCategoryRepository {
    suspend fun save(reviewCategory: ReviewCategory): Result<Unit, Error>
    fun findCategoryByName(name: String): Flow<Result<ReviewCategory, DataError.Network.ResourceNotFound>>
    fun findAllReviewCategories(): Flow<Result<List<ReviewCategory>, Error>>
    fun findReviewCategories(url: String): Flow<PagingData<ReviewCategory>>
}
