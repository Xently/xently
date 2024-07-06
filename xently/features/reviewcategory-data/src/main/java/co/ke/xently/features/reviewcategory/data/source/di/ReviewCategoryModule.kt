package co.ke.xently.features.reviewcategory.data.source.di

import co.ke.xently.features.reviewcategory.data.source.ReviewCategoryRepository
import co.ke.xently.features.reviewcategory.data.source.ReviewCategoryRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ReviewCategoryModule {
    @Binds
    abstract fun bindsReviewCategoryRepository(repository: ReviewCategoryRepositoryImpl): ReviewCategoryRepository
}