package co.ke.xently.features.stores.presentation.active

import co.ke.xently.libraries.data.image.domain.File

sealed interface ActiveStoreAction {
    class ProcessImageData(val image: File) : ActiveStoreAction
    class ProcessImageUpdateData(val data: Pair<Int, File>) : ActiveStoreAction
    class RemoveImageAtPosition(val position: Int) : ActiveStoreAction
}