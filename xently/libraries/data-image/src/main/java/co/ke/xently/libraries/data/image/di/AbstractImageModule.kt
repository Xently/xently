package co.ke.xently.libraries.data.image.di

import co.ke.xently.libraries.data.image.domain.UriToByteArrayConverter
import co.ke.xently.libraries.data.image.domain.UriToByteArrayConverterImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AbstractImageModule {
    @Binds
    @Singleton
    abstract fun bindUriToByteArrayConverter(converter: UriToByteArrayConverterImpl): UriToByteArrayConverter
}