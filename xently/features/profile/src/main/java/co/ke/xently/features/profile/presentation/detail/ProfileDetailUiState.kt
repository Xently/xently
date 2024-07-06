package co.ke.xently.features.profile.presentation.detail

import androidx.compose.runtime.Stable
import co.ke.xently.features.profile.data.domain.ProfileStatistic

@Stable
data class ProfileDetailUiState(
    val profileStatistic: ProfileStatistic = ProfileStatistic.DEFAULT,
    val isLoading: Boolean = false,
)