package co.ke.xently.business.domain

import co.ke.xently.features.stores.presentation.list.selection.Operation
import kotlinx.serialization.Serializable

@Serializable
data class SelectStoreScreen(val operation: Operation = Operation.Default)
