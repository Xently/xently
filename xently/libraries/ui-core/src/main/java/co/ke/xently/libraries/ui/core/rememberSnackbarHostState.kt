package co.ke.xently.libraries.ui.core

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun rememberSnackbarHostState(): SnackbarHostState {
    return remember {
        SnackbarHostState()
    }
}
