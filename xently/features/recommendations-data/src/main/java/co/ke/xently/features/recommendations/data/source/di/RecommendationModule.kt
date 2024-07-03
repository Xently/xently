package co.ke.xently.features.recommendations.data.source.di

import co.ke.xently.features.recommendations.data.source.RecommendationRepository
import co.ke.xently.features.recommendations.data.source.RecommendationRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RecommendationModule {
    @Binds
    abstract fun bindsRecommendationRepository(repository: RecommendationRepositoryImpl): RecommendationRepository
}