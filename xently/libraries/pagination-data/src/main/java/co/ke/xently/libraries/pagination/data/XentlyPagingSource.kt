package co.ke.xently.libraries.pagination.data

import androidx.paging.PagingState
import io.ktor.http.Url
import kotlinx.coroutines.yield
import androidx.paging.PagingSource as AndroidPagingSource


class XentlyPagingSource<T : Any>(
    private val dataLookupKey: String? = null,
    private val apiCall: suspend (initialKey: String?) -> PagedResponse<T>,
) : AndroidPagingSource<String, T>() {
    override fun getRefreshKey(state: PagingState<String, T>): String? {
        // Try to find the page key of the closest page to anchorPosition from
        // either the prevKey or the nextKey; you need to handle nullability
        // here.
        //  * prevKey == null -> anchorPage is the first page.
        //  * nextKey == null -> anchorPage is the last page.
        //  * both prevKey and nextKey are null -> anchorPage is the
        //    initial page, so return null.
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.let { url ->
                (Url(url).parameters["page"]?.toInt() ?: 0).let {
                    url.replace("page=$it", "page=${it + 1}")
                }
            } ?: anchorPage?.nextKey?.let { url ->
                (Url(url).parameters["page"]?.toInt() ?: 0).let {
                    url.replace("page=$it", "page=${it - 1}")
                }
            }
        }
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, T> {
        val response = try {
            apiCall(params.key)
        } catch (ex: Exception) {
            yield()
            return LoadResult.Error(throwable = ex)
        }
        return LoadResult.Page(
            data = response.getNullable(dataLookupKey)
                ?: emptyList(),
            prevKey = response.links.prev?.href,
            nextKey = response.links.next?.href,
        )
    }
}
