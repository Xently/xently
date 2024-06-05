package co.ke.xently.features.stores.domain

import kotlinx.serialization.Serializable

@Serializable
data object ActiveStoreNavGraph {
    @Serializable
    internal data object ActiveStore

    @Serializable
    internal data object EditStore

    @Serializable
    internal data object PickLocation
}