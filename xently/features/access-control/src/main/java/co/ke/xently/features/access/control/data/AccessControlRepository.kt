package co.ke.xently.features.access.control.data

import co.ke.xently.features.access.control.domain.AccessControl
import kotlinx.coroutines.flow.Flow

interface AccessControlRepository {
    fun findAccessControl(): Flow<AccessControl>
}
