package com.kwanzatukule.features.delivery.dispatch.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kwanzatukule.features.core.domain.Time
import com.kwanzatukule.features.core.presentation.KwanzaPreview
import com.kwanzatukule.features.core.presentation.theme.KwanzaTukuleTheme
import com.kwanzatukule.features.delivery.dispatch.domain.Dispatch
import com.kwanzatukule.features.delivery.dispatch.domain.Driver
import com.kwanzatukule.libraries.data.route.domain.Route
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun DispatchCardItem(
    dispatch: Dispatch,
    modifier: Modifier = Modifier,
    onClickViewRoute: () -> Unit = {},
    onClickViewOrders: () -> Unit = {},
) {
    Card(modifier = modifier) {
        DispatchCardContent(dispatch = dispatch)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
        ) {
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = onClickViewRoute,
                contentPadding = PaddingValues(),
                shape = RectangleShape,
            ) { Text(text = "View Route") }
            Button(
                modifier = Modifier.weight(1f),
                onClick = onClickViewOrders,
                contentPadding = PaddingValues(),
                shape = RectangleShape,
            ) { Text(text = "View Orders") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DispatchCardContent(dispatch: Dispatch, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp)
                .padding(top = 8.dp),
        ) {
            Text(
                text = "Dispatch No:",
                fontWeight = FontWeight.Bold,
            )
            Text(text = dispatch.id)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
        ) {
            Text(
                text = "Date:",
                fontWeight = FontWeight.Bold,
            )
            val timePickerState = rememberTimePickerState()
            Text(
                text = remember(dispatch, timePickerState) {
                    dispatch.date.toLocalDateTime(TimeZone.currentSystemDefault()).let {
                        val time = Time(hour = it.time.hour, minute = it.time.minute)
                        "${it.date} - ${time.toString(timePickerState.is24hour)}"
                    }
                },
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
        ) {
            Text(
                text = "Driver:",
                fontWeight = FontWeight.Bold,
            )
            Text(text = dispatch.driver.name)
        }
        Surface(
            color = dispatch.status.color(),
            shape = RoundedCornerShape(30),
            modifier = Modifier.padding(horizontal = 14.dp),
        ) {
            Text(
                text = stringResource(dispatch.status.localeName),
                modifier = Modifier.padding(PaddingValues(horizontal = 8.dp)),
            )
        }
    }
}

@KwanzaPreview
@Composable
private fun DispatchCardItemPreview() {
    val dispatch = remember {
        Dispatch(
            id = "ABCDEFGHIJ",
            date = Clock.System.now(),
            driver = Driver(name = "John Doe"),
            route = Route(
                id = 1,
                name = "Kibera",
                description = "Kibera route description...",
                summary = null,
            ),
            status = Dispatch.Status.entries.random(),
        )
    }
    KwanzaTukuleTheme {
        DispatchCardItem(dispatch = dispatch)
    }
}