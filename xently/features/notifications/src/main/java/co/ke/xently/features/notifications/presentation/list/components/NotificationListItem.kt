package co.ke.xently.features.notifications.presentation.list.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import co.ke.xently.features.notifications.data.domain.Notification
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.data.core.Time
import co.ke.xently.libraries.ui.core.XentlyThemePreview
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NotificationListItem(
    notification: Notification,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        shape = RectangleShape,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.onSurface,
        ),
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        val timePickerState = rememberTimePickerState()

        val dateOfRating = remember(notification.timeSent, timePickerState) {
            notification.timeSent
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .let {
                    val time = Time(hour = it.time.hour, minute = it.time.minute)
                    "${it.date} ${time.toString(timePickerState.is24hour)}"
                }
        }
        Text(
            text = dateOfRating,
            textAlign = TextAlign.Right,
            fontWeight = FontWeight.Light,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth(),
        )
        Text(
            text = notification.message.message,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp),
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

private class NotificationListItemParameterProvider : PreviewParameterProvider<Notification> {
    override val values: Sequence<Notification>
        get() = sequenceOf(
            Notification(
                id = 1,
                timeSent = Clock.System.now(),
                message = Notification.Message(
                    title = "Notification title",
                    message = "New deal 50% off on all meals at the new Imara Daima Hotel",
                ),
            ),
        )
}

@XentlyThemePreview
@Composable
private fun NotificationListItemPreview(
    @PreviewParameter(NotificationListItemParameterProvider::class)
    notification: Notification,
) {
    XentlyTheme {
        NotificationListItem(
            notification = notification,
            modifier = Modifier.padding(8.dp),
        )
    }
}