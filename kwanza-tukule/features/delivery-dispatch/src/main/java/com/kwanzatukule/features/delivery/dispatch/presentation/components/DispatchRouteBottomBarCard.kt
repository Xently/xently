package com.kwanzatukule.features.delivery.dispatch.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.ke.xently.libraries.ui.core.XentlyPreview
import com.kwanzatukule.features.core.presentation.theme.KwanzaTukuleTheme
import com.kwanzatukule.features.delivery.dispatch.domain.Dispatch
import com.kwanzatukule.features.delivery.dispatch.domain.Driver
import com.kwanzatukule.libraries.data.route.domain.Route
import kotlinx.datetime.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DispatchRouteBottomBarCard(
    dispatch: Dispatch,
    modifier: Modifier = Modifier,
    onClickStartJourney: () -> Unit,
) {
    ElevatedCard(
        modifier = modifier,
        shape = RectangleShape,
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 16.dp,
        ),
        colors = CardDefaults.cardColors(
            containerColor = BottomSheetDefaults.ContainerColor,
        ),
    ) {
        DispatchCardContent(
            dispatch = dispatch,
            modifier = Modifier.padding(top = 16.dp),
        )
        Button(
            onClick = onClickStartJourney,
            modifier = Modifier
                .fillMaxWidth()
                .padding(PaddingValues(16.dp))
                .navigationBarsPadding(),
            shape = RoundedCornerShape(30),
            contentPadding = PaddingValues(16.dp),
        ) {
            Text(
                text = "Start Journey",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
        }
    }
}

@XentlyPreview
@Composable
private fun DispatchRouteBottomBarCardPreview() {
    KwanzaTukuleTheme {
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
        DispatchRouteBottomBarCard(
            dispatch = dispatch,
            onClickStartJourney = {},
        )
    }
}