package co.ke.xently.libraries.location.tracker.presentation

import android.Manifest
import android.content.Intent
import android.provider.Settings
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import co.ke.xently.libraries.location.tracker.domain.utils.Launcher
import co.ke.xently.libraries.location.tracker.presentation.utils.LocationPermissionsState
import com.google.accompanist.permissions.ExperimentalPermissionsApi

val LocalLocationPermissionsState = staticCompositionLocalOf<LocationPermissionsState> {
    LocationPermissionsState.CoarseAndFine
}

typealias Granted = Boolean

@OptIn(ExperimentalPermissionsApi::class)
@Composable
inline fun rememberLocationPermissionLauncher(
    autoProcessStateOnRender: Boolean = true,
    crossinline onPermissionGranted: (Granted) -> Unit,
): Launcher {
    val permissions = if (LocalInspectionMode.current) {
        remember { LocationPermissionsState.Simulated }
    } else {
        LocalLocationPermissionsState.current
    } {
        val isGranted = it[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
                || it[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        onPermissionGranted(isGranted)
    }

    if (autoProcessStateOnRender) {
        SideEffect {
            onPermissionGranted(permissions.allPermissionsGranted)
        }
    }
    return remember(permissions) {
        Launcher {
            permissions.launchMultiplePermissionRequest()
        }
    }
}


@Composable
fun rememberEnableLocationGPSLauncher(snackbarHostState: SnackbarHostState): Launcher {
    val ctx = LocalContext.current
    return remember {
        Launcher {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            val canNavigateToGPSSettings =
                intent.resolveActivity(ctx.packageManager) != null

            val result = snackbarHostState.showSnackbar(
                message = "GPS disabled",
                actionLabel = if (!canNavigateToGPSSettings) {
                    null
                } else {
                    "ENABLE"
                },
                withDismissAction = true,
                duration = SnackbarDuration.Indefinite,
            )

            when (result) {
                SnackbarResult.Dismissed -> {

                }

                SnackbarResult.ActionPerformed -> {
                    if (canNavigateToGPSSettings) {
                        ctx.startActivity(intent)
                    }
                }
            }
        }
    }
}
