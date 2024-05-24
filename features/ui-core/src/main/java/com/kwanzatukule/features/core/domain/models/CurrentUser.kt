package com.kwanzatukule.features.core.domain.models

import androidx.compose.runtime.Stable

@Stable
data class CurrentUser(
    val uid: Int,
    val firstName: String?,
    val lastName: String?,
) {
    val displayName: String
        get() = listOfNotNull(firstName, lastName)
            .joinToString(" ")
}
