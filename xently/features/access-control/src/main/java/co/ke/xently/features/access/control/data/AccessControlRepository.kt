package co.ke.xently.features.access.control.data

import co.ke.xently.features.access.control.domain.AccessControl
import co.ke.xently.libraries.data.core.Link
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

abstract class AccessControlRepository {
    abstract fun findAccessControl(): Flow<AccessControl>
    suspend fun getAccessControl(): AccessControl {
        return findAccessControl().first()
    }

    suspend fun findLink(ref: String): Link? {
        return getAccessControl().links[ref]
    }
}
