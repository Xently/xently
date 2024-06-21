package co.ke.xently.libraries.data.network

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * ```
 * {
 *   "detail": "Invalid data",
 *   "instance": "/events",
 *   "status": 400,
 *   "timestamp": "2024-06-21T05:20:47.888771937Z",
 *   "errorCode": "VALIDATION_ERROR",
 *   "fieldErrors": {
 *     "description": [
 *       {
 *         "code": "Length",
 *         "message": "length must be between 0 and 500",
 *         "rejectedValue": "..."
 *       }
 *     ],
 *     "date": [
 *       {
 *         "code": "Future",
 *         "message": "must be a future date",
 *         "rejectedValue": "2022-04-18"
 *       }
 *     ]
 *   }
 * }
 * ```
 */
@Serializable
data class ApiErrorResponse(
    val code: String? = null,
    val error: String? = null,
    val detail: String? = null,
    val instance: String? = null,
    val message: String? = null,
    val status: Int = -1,
    val timestamp: Instant? = null,
    val fieldErrors: Map<String, List<ApiFieldError>>? = null,
) {
    @Serializable
    data class ApiFieldError(
        val code: String? = null,
        val message: String? = null,
        val rejectedValue: String? = null,
    )
}