package co.ke.xently.libraries.pagination.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import kotlinx.coroutines.yield
import kotlinx.datetime.Clock
import timber.log.Timber
import kotlin.time.Duration.Companion.hours

@OptIn(ExperimentalPagingApi::class)
class XentlyRemoteMediator<Key : Any, Value : Any, Data>(
    private val database: RemoteKeyDatabase,
    private val keyManager: LookupKeyManager,
    private val dataManager: DataManager<Data>,
    private val dataLookupKey: String? = null,
) : RemoteMediator<Key, Value>() {
    private val remoteKeyDao = database.remoteKeyDao()

    override suspend fun initialize(): InitializeAction {
        val lookupKey = keyManager.getLookupKey()
        val remoteKey = remoteKeyDao.remoteKeyByLookupKey(lookupKey = lookupKey)
            ?: return super.initialize().also {
                Timber.tag(TAG)
                    .d("%s(%s): Initialization.", it, lookupKey)
            }

        return if ((Clock.System.now() - remoteKey.dateRecorded) < 1.hours) {
            // Cached data is up-to-date, so there is no need to re-fetch
            // from the network.
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            // Need to refresh cached data from network; returning
            // LAUNCH_INITIAL_REFRESH here will also block RemoteMediator's
            // APPEND and PREPEND from running until REFRESH succeeds.
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }.also {
            Timber.tag(TAG)
                .d("%s(%s): Initialization.", it, lookupKey)
        }
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Key, Value>): MediatorResult {
        val lookupKey = keyManager.getLookupKey()
        return try {
            // The network load method takes an optional String
            // parameter. For every page after the first, pass the String
            // token returned from the previous page to let it continue
            // from where it left off. For REFRESH, pass null to load the
            // first page.
            val url = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKey = database.withTransactionFacade {
                        remoteKeyDao.remoteKeyByLookupKey(lookupKey = lookupKey)
                    }

                    // You must explicitly check if the page key is null when
                    // appending, since null is only valid for initial load.
                    // If you receive null for APPEND, that means you have
                    // reached the end of pagination and there are no more
                    // items to load.
                    remoteKey?.links?.first?.hrefWithoutQueryParamTemplates()
                }

                LoadType.PREPEND -> {
                    // In this example, you never need to prepend, since REFRESH
                    // will always load the first page in the list. Immediately
                    // return, reporting end of pagination.
                    Timber.tag(TAG)
                        .d("%s(%s): Reached end of pagination.", loadType, lookupKey)
                    return MediatorResult.Success(endOfPaginationReached = true)
                }

                LoadType.APPEND -> {
                    val remoteKey = database.withTransactionFacade {
                        remoteKeyDao.remoteKeyByLookupKey(lookupKey = lookupKey)
                    }

                    // You must explicitly check if the page key is null when
                    // appending, since null is only valid for initial load.
                    // If you receive null for APPEND, that means you have
                    // reached the end of pagination and there are no more
                    // items to load.
                    remoteKey?.links?.next?.hrefWithoutQueryParamTemplates()
                        ?: return run {
                            Timber.tag(TAG)
                                .d("%s(%s): Reached end of pagination.", loadType, lookupKey)
                            MediatorResult.Success(endOfPaginationReached = true)
                        }
                }
            }

            Timber.tag(TAG).d("%s(%s): Fetching data from %s", loadType, lookupKey, url)

            // Suspending network load via Retrofit. This doesn't need to
            // be wrapped in a withContext(Dispatcher.IO) { ... } block
            // since Retrofit's Coroutine CallAdapter dispatches on a
            // worker thread.
            val response = dataManager.fetchData(url = url)

            // Store loaded data, and next key in transaction, so that
            // they're always consistent.
            database.withTransactionFacade {
                if (loadType == LoadType.REFRESH) {
                    Timber.tag(TAG).d("%s(%s): Clearing all existing data", loadType, lookupKey)
                    remoteKeyDao.deleteByLookupKey(lookupKey = lookupKey)
                    dataManager.deleteByLookupKey(lookupKey = lookupKey)
                }

                // Update RemoteKey for this query.
                remoteKeyDao.insertOrReplace(
                    RemoteKey(
                        lookupKey = lookupKey,
                        links = response.links,
                    )
                )

                // Insert new users into database, which invalidates the
                // current PagingData, allowing Paging to present the updates
                // in the DB.
                val data = response.getNullable(lookupKey = dataLookupKey)
                    ?: response.embedded.values.flatten()
                dataManager.insertAll(lookupKey = lookupKey, data = data)
            }

            Timber.tag(TAG).d(
                "%s(%s): Successfully fetched data. Is end of pagination? %s",
                loadType,
                lookupKey,
                response.links.next == null
            )
            MediatorResult.Success(endOfPaginationReached = response.links.next == null)
        } catch (e: Exception) {
            yield()
            Timber.tag(TAG).d(e, "%s(%s): Error fetching data", loadType, lookupKey)
            MediatorResult.Error(e)
        }
    }

    companion object {
        private const val TAG = "XentlyRemoteMediator"
    }
}