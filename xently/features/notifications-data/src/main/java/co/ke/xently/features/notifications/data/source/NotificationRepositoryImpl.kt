package co.ke.xently.features.notifications.data.source

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import co.ke.xently.features.access.control.data.AccessControlRepository
import co.ke.xently.features.notifications.data.domain.Notification
import co.ke.xently.features.notifications.data.source.local.NotificationDatabase
import co.ke.xently.features.notifications.data.source.local.NotificationEntity
import co.ke.xently.libraries.data.core.DispatchersProvider
import co.ke.xently.libraries.pagination.data.DataManager
import co.ke.xently.libraries.pagination.data.LookupKeyManager
import co.ke.xently.libraries.pagination.data.PagedResponse
import co.ke.xently.libraries.pagination.data.RemoteMediator
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.URLBuilder
import io.ktor.http.fullPath
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class NotificationRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val database: NotificationDatabase,
    private val accessControlRepository: AccessControlRepository,
    private val dispatchersProvider: DispatchersProvider,
) : NotificationRepository {
    private val notificationDao = database.notificationDao()
    override suspend fun getNotificationsUrl(): String {
        return accessControlRepository.getAccessControl().myNotificationsUrl
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getNotifications(url: String, filters: Any): Flow<PagingData<Notification>> {
        val pagingConfig = PagingConfig(
            pageSize = 20,
//            initialLoadSize = 20,
//            prefetchDistance = 0,
        )

        val urlString = URLBuilder(url).apply {
            encodedParameters.run {
                set("size", pagingConfig.pageSize.toString())
            }
        }.build().fullPath
        val keyManager = LookupKeyManager.URL(url = urlString)

        val dataManager = object : DataManager<Notification> {
            override suspend fun insertAll(lookupKey: String, data: List<Notification>) {
                notificationDao.save(
                    data.map { notification ->
                        NotificationEntity(
                            notification = notification,
                            lookupKey = lookupKey,
                        )
                    },
                )
            }

            override suspend fun deleteByLookupKey(lookupKey: String) {
                notificationDao.deleteByLookupKey(lookupKey)
            }

            override suspend fun fetchData(url: String?): PagedResponse<Notification> {
                return httpClient.get(urlString = url ?: urlString)
                    .body<PagedResponse<Notification>>()
            }
        }
        val lookupKey = keyManager.getLookupKey()
        return Pager(
            config = pagingConfig,
            remoteMediator = RemoteMediator(
                database = database,
                keyManager = keyManager,
                dataManager = dataManager,
                dispatchersProvider = dispatchersProvider,
            ),
        ) {
            notificationDao.getNotificationsByLookupKey(lookupKey = lookupKey)
        }.flow.map { pagingData ->
            pagingData.map {
                it.notification
            }
        }
    }
}