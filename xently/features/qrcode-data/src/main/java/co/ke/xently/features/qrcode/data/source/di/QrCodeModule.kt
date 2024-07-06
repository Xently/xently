package co.ke.xently.features.qrcode.data.source.di

import co.ke.xently.features.qrcode.data.source.QrCodeRepository
import co.ke.xently.features.qrcode.data.source.QrCodeRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class QrCodeModule {
    @Binds
    abstract fun bindsStoreRepository(repository: QrCodeRepositoryImpl): QrCodeRepository
}