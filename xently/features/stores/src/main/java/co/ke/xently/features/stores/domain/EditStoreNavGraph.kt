package co.ke.xently.features.stores.domain

import kotlinx.serialization.Serializable

@Serializable
data object EditStoreNavGraph {
    @Serializable
    internal data object EditStore

    @Serializable
    internal data object PickLocation
}