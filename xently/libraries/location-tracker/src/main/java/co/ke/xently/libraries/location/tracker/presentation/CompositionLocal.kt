package co.ke.xently.libraries.location.tracker.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import co.ke.xently.libraries.location.tracker.domain.Location

val LocalLocationState = compositionLocalOf<State<Location?>> {
    mutableStateOf(null)
}