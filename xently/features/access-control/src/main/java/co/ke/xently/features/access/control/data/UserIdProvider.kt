package co.ke.xently.features.access.control.data

import kotlinx.coroutines.flow.Flow

interface UserIdProvider {
    val currentUserId: Flow<String>
}