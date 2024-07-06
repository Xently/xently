package co.ke.xently.features.profile.presentation.edit

import androidx.compose.runtime.Stable
import co.ke.xently.features.profile.data.domain.ProfileStatistic
import co.ke.xently.features.profile.data.domain.error.LocalFieldError

@Stable
data class ProfileEditDetailUiState(
    val profileStatistic: ProfileStatistic = ProfileStatistic.DEFAULT,
    val name: String = "",
    val nameError: List<LocalFieldError>? = null,
    val isLoading: Boolean = false,
    val disableFields: Boolean = false,
) {
    val enableSaveButton: Boolean = !isLoading && !disableFields
    val isFormValid: Boolean = nameError.isNullOrEmpty()
}