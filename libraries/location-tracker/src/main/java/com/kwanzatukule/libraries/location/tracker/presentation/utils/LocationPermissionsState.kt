package com.kwanzatukule.libraries.location.tracker.presentation.utils

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@Suppress("unused")
@OptIn(ExperimentalPermissionsApi::class)
sealed interface LocationPermissionsState {
    @Composable
    operator fun invoke(onPermissionsResult: (Map<String, Boolean>) -> Unit): MultiplePermissionsState

    data object Fine : LocationPermissionsState {
        @Composable
        override fun invoke(onPermissionsResult: (Map<String, Boolean>) -> Unit): MultiplePermissionsState {
            return rememberMultiplePermissionsState(
                permissions = listOf(Manifest.permission.ACCESS_FINE_LOCATION),
                onPermissionsResult = onPermissionsResult,
            )
        }
    }

    data object Coarse : LocationPermissionsState {
        @Composable
        override fun invoke(onPermissionsResult: (Map<String, Boolean>) -> Unit): MultiplePermissionsState {
            return rememberMultiplePermissionsState(
                permissions = listOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                onPermissionsResult = onPermissionsResult,
            )
        }
    }

    data object CoarseAndFine : LocationPermissionsState {
        @Composable
        override fun invoke(onPermissionsResult: (Map<String, Boolean>) -> Unit): MultiplePermissionsState {
            return rememberMultiplePermissionsState(
                permissions = listOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                ),
                onPermissionsResult = onPermissionsResult,
            )
        }
    }

    /**
     * This could be used in a compose preview
     */
    data object Simulated : LocationPermissionsState {
        @Composable
        override fun invoke(onPermissionsResult: (Map<String, Boolean>) -> Unit): MultiplePermissionsState {
            return remember {
                object : MultiplePermissionsState {
                    override val allPermissionsGranted: Boolean
                        get() = false
                    override val permissions: List<PermissionState>
                        get() = emptyList()
                    override val revokedPermissions: List<PermissionState>
                        get() = emptyList()
                    override val shouldShowRationale: Boolean
                        get() = false

                    override fun launchMultiplePermissionRequest() {

                    }
                }
            }
        }
    }
}