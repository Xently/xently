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
import co.ke.xently.features.stores.domain.IsOpen
import co.ke.xently.features.stores.domain.isCurrentlyOpen
import co.ke.xently.features.stores.domain.toSmallestDistanceUnit
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyThemePreview
import co.ke.xently.libraries.ui.core.components.shimmer
import co.ke.xently.libraries.ui.image.XentlyImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

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
                modifier = Modifier.fillMaxSize(),
                onError = {
                    if (index != store.images.lastIndex) index += 1
                },
            )
            val isOpenAndOverlineText by overlineTextState(store)
            val (isOpen, overlineText) = isOpenAndOverlineText

            androidx.compose.animation.AnimatedContent(
                isOpen,
                label = "store-operational-status",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
            ) {
                when (it) {
                    null -> Unit
                    false -> Badge { Text(stringResource(R.string.closed)) }
                    true -> Badge(
                        contentColor = Color.Black,
                        containerColor = Color.Green.copy(alpha = 0.5f),
                    ) { Text(stringResource(R.string.open)) }
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
                            modifier = Modifier.shimmer(isLoading),
                        )
                    }
                },
                headlineContent = {
                    Text(
                        text = store.shop.name,
                        modifier = Modifier.shimmer(isLoading),
                        style = MaterialTheme.typography.bodyMedium,
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
                            modifier = Modifier
                                .weight(1f)
                                .shimmer(isLoading),
                            style = MaterialTheme.typography.bodySmall,
                        )
                        if (dropDownMenu != null) {
                            var expanded by rememberSaveable {
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
                                    modifier = Modifier.clickable(
                                        role = Role.Checkbox,
                                        indication = ripple(bounded = false),
                                        interactionSource = remember { MutableInteractionSource() },
                                    ) { expanded = !isLoading },
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

    val is24hour = timePickerState.is24hour
    return produceState<Pair<IsOpen?, String>>(
        null to "",
        store.distance,
        store.openingHours,
        is24hour,
    ) {
        flow {
            while (true) {
                val deferredDistance = async {
                    store.distance?.toSmallestDistanceUnit()?.toString() ?: ""
                }
                val deferredIsCurrentlyOpen = async {
                    store.openingHours.isCurrentlyOpen()
                }
                emit(deferredDistance.await() to deferredIsCurrentlyOpen.await())
                delay(1_000)
            }
        }.distinctUntilChanged { a, b ->
            val (_, isCurrentlyOpenA) = a
            val (_, isCurrentlyOpenB) = b
            val (dayOfWeekA, isOpenA, _) = isCurrentlyOpenA
            val (dayOfWeekB, isOpenB, _) = isCurrentlyOpenB

            dayOfWeekA == dayOfWeekB
                    && isOpenA == isOpenB
        }.flowOn(Dispatchers.Default).collect { (distance, isCurrentlyOpen) ->
            val (dayOfWeek, isOpen, operationHours) = isCurrentlyOpen

            val formattedOperationTime =
                operationHours.joinToString(separator = " • ") { (hour, _) ->
                    buildString {
                        append(hour.openTime.toString(is24hour))
                        append(" - ")
                        append(hour.closeTime.toString(is24hour))
                    }
                }

            value = isOpen to buildString {
                var separator = ""
                if (distance.isNotBlank()) {
                    append(distance)
                    separator = " | "
                }
                if (formattedOperationTime.isNotBlank()) {
                    append(separator)
                    append(formattedOperationTime)
                    append(" | ")
                    append(dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() })
                }
            }
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