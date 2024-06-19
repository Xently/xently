package co.ke.xently.features.stores.presentation.active

import co.ke.xently.libraries.data.image.domain.Image

sealed interface ActiveStoreAction {
    class ProcessImageData(val image: Image) : ActiveStoreAction
    class ProcessImageUpdateData(val data: Pair<Int, Image>) : ActiveStoreAction
    class RemoveImageAtPosition(val position: Int) : ActiveStoreAction
}