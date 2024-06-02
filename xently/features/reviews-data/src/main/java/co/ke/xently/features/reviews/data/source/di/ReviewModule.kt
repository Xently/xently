package co.ke.xently.features.reviews.data.source.di

import co.ke.xently.features.reviews.data.source.ReviewRepository
import co.ke.xently.features.reviews.data.source.ReviewRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ReviewModule {
    @Binds
    abstract fun bindsReviewRepository(repository: ReviewRepositoryImpl): ReviewRepository
}