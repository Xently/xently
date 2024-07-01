package co.ke.xently.features.stores.presentation.detail

import androidx.compose.runtime.Stable
import co.ke.xently.features.qrcode.data.domain.QrCodeResponse
import co.ke.xently.features.stores.data.domain.Store

@Stable
internal data class StoreDetailUiState(
    val isLoading: Boolean = false,
    val isProcessingQrCode: Boolean = false,
    val qrCodeScanResponse: QrCodeResponse? = null,
    val store: Store? = null,
)