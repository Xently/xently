package co.ke.xently.features.stores.presentation.list.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Badge
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import co.ke.xently.features.stores.R
import co.ke.xently.features.stores.data.domain.Store
import co.ke.xently.features.stores.domain.DurationToOperationStartOrClosure
import co.ke.xently.features.stores.domain.IsOpen
import co.ke.xently.features.stores.domain.flowOfDistanceAndCurrentlyOpen
import co.ke.xently.features.stores.domain.operationStartOrClosureFlow
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.location.tracker.presentation.LocalLocationState
import co.ke.xently.libraries.ui.core.LocalDispatchersProvider
import co.ke.xently.libraries.ui.core.XentlyThemePreview
import co.ke.xently.libraries.ui.image.XentlyImage
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material3.fade
import com.google.accompanist.placeholder.material3.placeholder
import kotlinx.coroutines.flow.collectLatest

typealias Expanded = Boolean
typealias OnClose = () -> Unit

@Composable
fun StoreItemCard(
    store: Store,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    dropDownMenu: (@Composable (Pair<Expanded, OnClose>) -> Unit)? = null,
) {
    ElevatedCard(modifier = modifier, onClick = onClick, shape = MaterialTheme.shapes.large) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
        ) {
            var index by rememberSaveable(store.id) { mutableIntStateOf(0) }
            XentlyImage(
                data = store.images.getOrNull(index),
                modifier = Modifier
                    .placeholder(
                        visible = isLoading,
                        highlight = PlaceholderHighlight.fade(),
                    )
                    .fillMaxSize(),
                onError = {
                    if (index != store.images.lastIndex) index += 1
                },
            )

            val isOpenAndOverlineText by overlineTextState(store)
            val (isOpen, overlineText) = isOpenAndOverlineText

            when (val operationStartOrClosure = operationStartOrClosureState(store).value) {
                is DurationToOperationStartOrClosure.DurationToOperationClosure -> {
                    Badge(
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .placeholder(
                                visible = isLoading,
                                highlight = PlaceholderHighlight.fade(),
                            ),
                    ) { Text(stringResource(R.string.closes_in, operationStartOrClosure.duration)) }
                }

                is DurationToOperationStartOrClosure.DurationToOperationStart -> {
                    Badge(
                        contentColor = Color.Black,
                        containerColor = Color.Yellow.copy(alpha = 0.5f),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .placeholder(
                                visible = isLoading,
                                highlight = PlaceholderHighlight.fade(),
                            ),
                    ) { Text(stringResource(R.string.opens_in, operationStartOrClosure.duration)) }
                }

                DurationToOperationStartOrClosure.NotOperational, null -> {
                    androidx.compose.animation.AnimatedContent(
                        isOpen,
                        label = "store-operational-status",
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp),
                    ) {
                        when (it) {
                            null -> Unit
                            false -> Badge(
                                modifier = Modifier.placeholder(
                                    visible = isLoading,
                                    highlight = PlaceholderHighlight.fade(),
                                ),
                            ) { Text(stringResource(R.string.closed)) }

                            true -> Badge(
                                contentColor = Color.Black,
                                containerColor = Color.Green.copy(alpha = 0.5f),
                                modifier = Modifier.placeholder(
                                    visible = isLoading,
                                    highlight = PlaceholderHighlight.fade(),
                                ),
                            ) { Text(stringResource(R.string.open)) }
                        }
                    }
                }
            }

            val containerColor = MaterialTheme.colorScheme.background.copy(0.5f)
            ListItem(
                modifier = Modifier.align(Alignment.BottomCenter),
                colors = ListItemDefaults.colors(containerColor = containerColor),
                overlineContent = {
                    androidx.compose.animation.AnimatedVisibility(overlineText.isNotBlank()) {
                        Text(
                            text = overlineText,
                            modifier = Modifier.placeholder(
                                visible = isLoading,
                                highlight = PlaceholderHighlight.fade(),
                            ),
                        )
                    }
                },
                headlineContent = {
                    Text(
                        text = store.shop.name,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.placeholder(
                            visible = isLoading,
                            highlight = PlaceholderHighlight.fade(),
                        ),
                    )
                },
                supportingContent = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = store.name,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier
                                .weight(1f)
                                .placeholder(
                                    visible = isLoading,
                                    highlight = PlaceholderHighlight.fade(),
                                ),
                        )
                        if (dropDownMenu != null) {
                            var expanded by rememberSaveable(store.id) {
                                mutableStateOf(false)
                            }

                            Box {
                                Icon(
                                    Icons.Default.MoreVert,
                                    contentDescription = stringResource(
                                        R.string.content_desc_more_options_for_store,
                                        store.name,
                                        store.shop.name,
                                    ),
                                    modifier = Modifier
                                        .placeholder(
                                            visible = isLoading,
                                            highlight = PlaceholderHighlight.fade(),
                                        )
                                        .clickable(
                                            role = Role.Checkbox,
                                            indication = ripple(bounded = false),
                                            interactionSource = remember { MutableInteractionSource() },
                                        ) { expanded = !expanded },
                                )

                                dropDownMenu(expanded to { expanded = false })
                            }
                        }
                    }
                },
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun overlineTextState(store: Store): State<Pair<IsOpen?, String>> {
    val timePickerState = rememberTimePickerState()
    var isOpenCache by rememberSaveable(store.id) {
        mutableStateOf<IsOpen?>(null)
    }
    var overlineTextCache by rememberSaveable(store.id) {
        mutableStateOf("")
    }

    val is24hour = timePickerState.is24hour
    val currentLocation by LocalLocationState.current
    val dispatcher = LocalDispatchersProvider.current
    return produceState(
        isOpenCache to overlineTextCache,
        store.distance,
        store.openingHours,
        is24hour,
    ) {
        flowOfDistanceAndCurrentlyOpen(
            currentLocation = currentLocation,
            is24hour = is24hour,
            location = store.location,
            fallbackDistanceMeters = store.distance,
            openingHours = store.openingHours,
            dispatcher = dispatcher,
        ).collectLatest { (isOpen, text) ->
            isOpenCache = isOpen
            overlineTextCache = text
            value = isOpen to text
        }
    }
}

@Composable
private fun operationStartOrClosureState(store: Store): State<DurationToOperationStartOrClosure?> {
    var operationStartOrClosureCache by remember(store.id) {
        mutableStateOf<DurationToOperationStartOrClosure?>(null)
    }

    val dispatcher = LocalDispatchersProvider.current
    return produceState(
        operationStartOrClosureCache,
        store.openingHours,
    ) {
        operationStartOrClosureFlow(
            openingHours = store.openingHours,
            dispatchersProvider = dispatcher,
        ).collectLatest { operationStartOrClosure ->
            operationStartOrClosureCache = operationStartOrClosure
            value = operationStartOrClosure
        }
    }
}

private data class StoreCardParameter(
    val store: Store,
    val isLoading: Boolean = false,
)

private class StoreCardParameterProvider :
    PreviewParameterProvider<StoreCardParameter> {
    override val values: Sequence<StoreCardParameter>
        get() = sequenceOf(
            StoreCardParameter(
                Store.DEFAULT
            ),
            StoreCardParameter(
                Store.DEFAULT,
                isLoading = true,
            ),
        )
}

@XentlyThemePreview
@Composable
private fun StoreCardPreview(
    @PreviewParameter(StoreCardParameterProvider::class)
    parameter: StoreCardParameter,
) {
    XentlyTheme {
        StoreItemCard(
            store = parameter.store,
            isLoading = parameter.isLoading,
            onClick = {},
            dropDownMenu = {},
        )
    }
}