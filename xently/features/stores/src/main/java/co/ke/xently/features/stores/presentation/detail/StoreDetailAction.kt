package co.ke.xently.features.stores.presentation.detail

sealed interface StoreDetailAction {
    data object GetPointsAndReview : StoreDetailAction
    data object DismissQrCodeProcessingDialog : StoreDetailAction
}