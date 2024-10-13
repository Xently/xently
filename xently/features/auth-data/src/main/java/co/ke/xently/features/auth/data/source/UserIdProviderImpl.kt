package co.ke.xently.features.auth.data.source

import co.ke.xently.features.access.control.data.UserIdProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UserIdProviderImpl @Inject constructor(
    private val database: AuthenticationDatabase,
) : UserIdProvider {
    override val currentUserId: Flow<String>
        get() = database.userDao().findFirst().mapNotNull { it?.id }
}