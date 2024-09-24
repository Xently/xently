package co.ke.xently.libraries.data.core.domain

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

interface DispatchersProvider {
    val io: CoroutineDispatcher
    val main: CoroutineDispatcher
    val mainImmediate: CoroutineDispatcher
    val default: CoroutineDispatcher

    object Default : DispatchersProvider {
        override val io: CoroutineDispatcher = Dispatchers.IO
        override val main: CoroutineDispatcher = Dispatchers.Main
        override val mainImmediate: CoroutineDispatcher = Dispatchers.Main.immediate
        override val default: CoroutineDispatcher = Dispatchers.Default
    }
}
