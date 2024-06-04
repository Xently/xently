package co.ke.xently.features.openinghours.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import co.ke.xently.features.openinghours.R
import co.ke.xently.features.openinghours.data.domain.OpeningHour
import co.ke.xently.features.openinghours.domain.ChangeOpeningHourTime
import co.ke.xently.features.ui.core.presentation.components.LabeledCheckbox
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.data.core.Time
import co.ke.xently.libraries.ui.core.XentlyPreview
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.isoDayNumber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyOpeningHourInput(
    modifier: Modifier,
    enableInteraction: Boolean,
    openingHours: List<OpeningHour>,
    onTimeChange: (ChangeOpeningHourTime) -> Unit,
    onSelectedOpeningHourChange: (OpeningHour) -> Unit,
    onOpenStatusChange: (Boolean) -> Unit,
) {
    var initialHour by rememberSaveable { mutableIntStateOf(0) }
    var initialMinute by rememberSaveable { mutableIntStateOf(0) }
    var showingPicker by rememberSaveable { mutableStateOf(true) }
    var showTimePicker by rememberSaveable { mutableStateOf(false) }
    var isOpenTime by rememberSaveable { mutableStateOf(true) }

    if (showTimePicker) {
        val state = rememberTimePickerState(
            initialHour = initialHour,
            initialMinute = initialMinute,
        )
        AlertDialog(
            title = {
                val stringResource by remember(showingPicker) {
                    derivedStateOf {
                        if (showingPicker) {
                            R.string.action_select_time
                        } else {
                            R.string.action_enter_time
                        }
                    }
                }
                Text(text = stringResource(stringResource))
            },
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.onBackground),
                        onClick = { showingPicker = !showingPicker },
                    ) {
                        val icon = if (showingPicker) {
                            Icons.Outlined.Keyboard
                        } else {
                            Icons.Outlined.Schedule
                        }
                        val contentDescriptionResource by remember(showingPicker) {
                            derivedStateOf {
                                if (showingPicker) {
                                    R.string.content_desc_switch_to_text_input
                                } else {
                                    R.string.content_desc_switch_to_text_input
                                }
                            }
                        }
                        Icon(
                            icon,
                            contentDescription = stringResource(contentDescriptionResource),
                        )
                    }
                    TextButton(
                        onClick = {
                            onTimeChange(ChangeOpeningHourTime(isOpenTime, state))
                            showTimePicker = false
                        },
                    ) {
                        Text(text = stringResource(R.string.ok_shorthand))
                    }
                }
            },
            text = {
                if (showingPicker) {
                    TimePicker(state = state)
                } else {
                    TimeInput(state = state)
                }
            },
        )
    }
    Column(modifier = Modifier.then(modifier), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(text = stringResource(R.string.headline_opening_hours))
        val is24hour = rememberTimePickerState().is24hour
        for (openingHour in openingHours) {
            DailyOpeningHourInput(
                modifier = Modifier.fillMaxWidth(),
                enableInteraction = enableInteraction,
                dayOfWeek = openingHour.dayOfWeek,
                from = openingHour.openTime.toString(is24hour),
                to = openingHour.closeTime.toString(is24hour),
                isOpen = openingHour.open,
                onOpenStatusChange = {
                    onSelectedOpeningHourChange(openingHour)
                    onOpenStatusChange(it)
                },
                onFromClick = {
                    onSelectedOpeningHourChange(openingHour)
                    initialHour = openingHour.openTime.hour
                    initialMinute = openingHour.openTime.minute
                    showTimePicker = true
                    isOpenTime = true
                },
                onToClick = {
                    onSelectedOpeningHourChange(openingHour)
                    initialHour = openingHour.closeTime.hour
                    initialMinute = openingHour.closeTime.minute
                    showTimePicker = true
                    isOpenTime = false
                },
            )
        }
    }
}

@Composable
private fun DailyOpeningHourInput(
    modifier: Modifier,
    enableInteraction: Boolean,
    dayOfWeek: DayOfWeek,
    from: String,
    to: String,
    isOpen: Boolean,
    onOpenStatusChange: (Boolean) -> Unit,
    onFromClick: () -> Unit,
    onToClick: () -> Unit,
) {
    Row(
        modifier = Modifier.then(modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = dayOfWeek.name
                .take(3)
                .toLowerCase(LocaleList.current)
                .capitalize(LocaleList.current),
            modifier = Modifier.weight(.4f),
        )
        OutlinedCard(
            enabled = enableInteraction,
            onClick = onFromClick,
            content = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 16.dp),
                    contentAlignment = Alignment.CenterStart,
                ) { Text(text = from, maxLines = 1) }
            },
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier
                .weight(1f)
                .requiredHeight(OutlinedTextFieldDefaults.MinHeight),
            colors = CardDefaults.outlinedCardColors(containerColor = Color.Transparent),
        )
        Text(text = stringResource(R.string.to))
        OutlinedCard(
            enabled = enableInteraction,
            onClick = onToClick,
            content = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 16.dp),
                    contentAlignment = Alignment.CenterStart,
                ) { Text(text = to, maxLines = 1) }
            },
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier
                .weight(1f)
                .requiredHeight(OutlinedTextFieldDefaults.MinHeight),
            colors = CardDefaults.outlinedCardColors(containerColor = Color.Transparent),
        )
        LabeledCheckbox(
            enabled = enableInteraction,
            checked = isOpen,
            label = stringResource(R.string.action_open),
            horizontalSpacing = 0.dp,
            onCheckedChange = onOpenStatusChange,
        )
    }
}

@XentlyPreview
@Composable
private fun WeeklyOpeningHourInputPreview() {
    XentlyTheme {
        WeeklyOpeningHourInput(
            modifier = Modifier
                .wrapContentSize()
                .padding(16.dp),
            enableInteraction = true,
            openingHours = remember {
                DayOfWeek.entries.map {
                    OpeningHour(
                        dayOfWeek = it,
                        openTime = Time(7, 0),
                        closeTime = Time(17, 0),
                        open = it.isoDayNumber !in setOf(6, 7),
                    )
                }
            },
            onTimeChange = {},
            onSelectedOpeningHourChange = {},
            onOpenStatusChange = {},
        )
    }
}