package co.ke.xently.features.stores.presentation.detail.components

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.basicMarquee
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.PreviewParameter
import co.ke.xently.features.stores.R
import co.ke.xently.features.stores.data.domain.Store
import co.ke.xently.features.stores.presentation.detail.StoreDetailUiState
import co.ke.xently.features.stores.presentation.detail.StoreDetailUiStateParameterProvider
import co.ke.xently.features.ui.core.presentation.components.CircularButton
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.location.tracker.domain.Location
import co.ke.xently.libraries.location.tracker.domain.utils.Launcher
import co.ke.xently.libraries.ui.core.XentlyThemePreview
import co.ke.xently.libraries.ui.core.rememberSnackbarHostState
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material3.fade
import com.google.accompanist.placeholder.material3.placeholder
import kotlinx.coroutines.launch

@Stable
internal fun interface UrlOpener {
    fun open()
}

@Composable
internal fun rememberUrlOpener(url: String?, vararg backupUrls: String?): UrlOpener {
    val context = LocalContext.current

    return remember(context, url, *backupUrls) {
        UrlOpener {
            val urls = buildList {
                add(url)
                addAll(backupUrls)
            }

            for (u in urls) {
                val uri = u?.let { Uri.parse(it) }
                    ?: continue

                val intent = Intent().apply {
                    action = Intent.ACTION_VIEW
                    data = uri
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }

                try {
                    context.startActivity(intent)
                    break
                } catch (_: ActivityNotFoundException) {
                    continue
                }
            }
        }
    }
}


@Composable
private fun rememberGoogleMapsDirectionLauncher(
    location: Location,
    snackbarHostState: SnackbarHostState,
): Launcher {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val googleMapsPackageName = "com.google.android.apps.maps"
    val urlOpener = rememberUrlOpener(
        "market://details?id=$googleMapsPackageName",
        "https://play.google.com/store/apps/details?id=$googleMapsPackageName",
    )

    return remember {
        Launcher {
            val navigationQuery = location.let {
                "${it.latitude},${it.longitude}"
            }
            val uri = Uri.parse("google.navigation:q=$navigationQuery")
            val mapIntent = Intent(Intent.ACTION_VIEW, uri)
            if (mapIntent.resolveActivity(context.packageManager) == null) {
                scope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = context.getString(R.string.error_message_navigation_app_not_found),
                        actionLabel = context.getString(R.string.action_install)
                            .toUpperCase(Locale.current),
                        duration = SnackbarDuration.Long,
                    )

                    if (result == SnackbarResult.ActionPerformed) {
                        urlOpener.open()
                    }
                }
            } else {
                mapIntent.run {
                    setPackage(googleMapsPackageName)
                    if (resolveActivity(context.packageManager) != null) {
                        // Prefer Google Maps over other apps
                        context.startActivity(this)
                    } else {
                        context.startActivity(mapIntent)
                    }
                }
            }
        }
    }
}

@Composable
internal fun StoreDetailListItem(
    store: Store,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    snackbarHostState: SnackbarHostState = rememberSnackbarHostState(),
) {
    val scope = rememberCoroutineScope()

    ListItem(
        modifier = modifier,
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        headlineContent = {
            Text(
                text = store.shop.name,
                modifier = Modifier
                    .basicMarquee()
                    .placeholder(visible = isLoading, highlight = PlaceholderHighlight.fade()),
            )
        },
        supportingContent = {
            val supportingTest = remember(store.name, store.categories) {
                buildString {
                    store.categories.firstOrNull()?.name?.let { category ->
                        append(category)
                        append(" | ")
                    }
                    append(store.name)
                }
            }
            Text(
                text = supportingTest,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.placeholder(
                    visible = isLoading,
                    highlight = PlaceholderHighlight.fade(),
                ),
            )
        },
        trailingContent = {
            val launcher = rememberGoogleMapsDirectionLauncher(
                location = store.location,
                snackbarHostState = snackbarHostState,
            )
            CircularButton(
                modifier = Modifier.placeholder(
                    visible = isLoading,
                    highlight = PlaceholderHighlight.fade(),
                ),
                color = MaterialTheme.colorScheme.primary,
                onClick = {
                    scope.launch {
                        launcher.launch()
                    }
                },
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = stringResource(R.string.content_description_navigate_to_location),
                )
            }
        },
    )
}

@XentlyThemePreview
@Composable
private fun StoreDetailListItemPreview(
    @PreviewParameter(StoreDetailUiStateParameterProvider::class)
    state: Pair<StoreDetailUiState, Boolean>,
) {
    XentlyTheme {
        StoreDetailListItem(
            isLoading = state.first.isLoading,
            store = state.first.store ?: Store.DEFAULT,
        )
    }
}