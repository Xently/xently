package co.ke.xently.libraries.pagination.data

import org.junit.Assert.assertEquals
import org.junit.Test

class URLLookupKeyManagerTest {
    @Test
    fun `should return lookup key for string URL without path`() {
        val url = "https://www.google.com"
        val manager = LookupKeyManager.URL(url)

        val actual = manager.getLookupKey()

        assertEquals("?", actual)
    }

    @Test
    fun `should return lookup key for string URL with path`() {
        val url = "https://www.google.com/path/to/resource"
        val manager = LookupKeyManager.URL(url)

        val actual = manager.getLookupKey()

        assertEquals("/path/to/resource?", actual)
    }

    @Test
    fun `should return lookup key for string URL with path and query parameters`() {
        val url =
            "https://www.google.com/path/to/resource?sort=eg,asc&sort=age,asc&param1=value1&param2=value2&page=1"
        val manager = LookupKeyManager.URL(url)

        val actual = manager.getLookupKey()

        assertEquals(
            "/path/to/resource?param1=value1&param2=value2&sort=age,asc&sort=eg,asc",
            actual
        )
    }

    @Test
    fun `should return lookup key for url path 1`() {
        val urlPath = "/path/to/resource"
        val manager = LookupKeyManager.URL(urlPath = urlPath, queryParams = "")

        val actual = manager.getLookupKey()

        assertEquals("/path/to/resource?", actual)
    }

    @Test
    fun `should return lookup key for url path 2`() {
        val urlPath = "path/to/resource"
        val manager = LookupKeyManager.URL(urlPath = urlPath, queryParams = "")

        val actual = manager.getLookupKey()

        assertEquals("/path/to/resource?", actual)
    }

    @Test
    fun `should return lookup key for url path and query parameters 1`() {
        val urlPath = "/path/to/resource"
        val queryParams = "sort=eg,asc&sort=age,asc&param1=value1&param2=value2&page=1"
        val manager = LookupKeyManager.URL(urlPath, queryParams)

        val actual = manager.getLookupKey()

        assertEquals(
            "/path/to/resource?param1=value1&param2=value2&sort=age,asc&sort=eg,asc",
            actual
        )
    }
}