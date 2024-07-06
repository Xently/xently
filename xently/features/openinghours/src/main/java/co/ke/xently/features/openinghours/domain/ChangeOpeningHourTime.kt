package co.ke.xently.features.openinghours.domain

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Stable

@Stable
class ChangeOpeningHourTime @OptIn(ExperimentalMaterial3Api::class) constructor(
    val isOpenTime: Boolean,
    val state: TimePickerState,
)