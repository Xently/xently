package co.ke.xently.libraries.pagination.data

interface DataManager<T> {
    suspend fun insertAll(lookupKey: String, data: List<T>)
    suspend fun deleteByLookupKey(lookupKey: String)
    suspend fun fetchData(url: String?): PagedResponse<T>
}