package co.ke.xently.features.profile.presentation.edit

import androidx.compose.runtime.Stable
import co.ke.xently.features.profile.data.domain.ProfileStatistic
import co.ke.xently.features.profile.data.domain.error.LocalFieldError

@Stable
data class ProfileEditDetailUiState(
    val profileStatistic: ProfileStatistic = ProfileStatistic.DEFAULT,
    val firstName: String = "",
    val firstNameError: List<LocalFieldError>? = null,
    val lastName: String = "",
    val lastNameError: List<LocalFieldError>? = null,
    val email: String = "",
    val emailError: List<LocalFieldError>? = null,
    val isLoading: Boolean = false,
    val disableFields: Boolean = false,
) {
    val enableSaveButton: Boolean = !isLoading
            && !disableFields
            && firstName.isNotBlank()
            && lastName.isNotBlank()
            && email.isNotBlank()
    val isFormValid: Boolean = firstNameError.isNullOrEmpty()
            && lastNameError.isNullOrEmpty()
            && emailError.isNullOrEmpty()
}