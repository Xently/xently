package co.ke.xently.features.qrcode.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import co.ke.xently.features.qrcode.R
import co.ke.xently.features.qrcode.data.QrCodeResponse
import co.ke.xently.features.ui.core.presentation.components.PrimaryButton
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.data.core.Link
import co.ke.xently.libraries.ui.core.XentlyThemePreview
import kotlin.random.Random


@Composable
fun ScanQrCodeAlertDialog(
    response: QrCodeResponse?,
    onDismissRequest: () -> Unit,
    onPositiveButtonClick: () -> Unit,
) {
    AlertDialog(
        confirmButton = {},
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(dismissOnClickOutside = false),
        text = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 150.dp),
            ) {
                if (response == null) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(
                            48.dp,
                            Alignment.CenterVertically,
                        ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(top = 24.dp)
                                .size(120.dp),
                            strokeWidth = 8.dp,
                        )
                        Text(
                            text = "Please wait, as we verify that you are at this location...",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                        )
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(
                            24.dp,
                            Alignment.CenterVertically,
                        ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(150.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text = stringResource(
                                R.string.message_successful_scan,
                                response.pointsEarned,
                                response.totalXentlyPoints,
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.titleLarge,
                        )
                        PrimaryButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = onPositiveButtonClick,
                            label = if (response.reviewCategoriesUrl == null) {
                                stringResource(R.string.action_okay)
                            } else {
                                stringResource(R.string.action_share_experience)
                            }.toUpperCase(LocaleList.current),
                        )
                        AnimatedVisibility(!response.reviewCategoriesUrl.isNullOrBlank()) {
                            TextButton(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = onDismissRequest,
                            ) { Text(stringResource(R.string.action_skip_share_experience)) }
                        }
                    }
                }
            }
        }
    )
}


private class QrCodeResponsePreviewProvider : PreviewParameterProvider<QrCodeResponse?> {
    val response = QrCodeResponse(
        pointsEarned = Random.nextInt(10, 100),
        storePoints = Random.nextInt(10, 100),
        storeVisitCount = Random.nextInt(10, 100),
        totalXentlyPoints = Random.nextInt(10, 100),
    )
    override val values: Sequence<QrCodeResponse?>
        get() = sequenceOf(
            null,
            response,
            response.copy(
                links = mapOf(
                    "store-review-categories-with-my-ratings" to Link(
                        href = "https://www.google.com",
                    ),
                )
            ),
        )

}

@XentlyThemePreview
@Composable
private fun ScanQrCodeAlertDialogPreview(
    @PreviewParameter(QrCodeResponsePreviewProvider::class)
    response: QrCodeResponse?,
) {
    XentlyTheme {
        ScanQrCodeAlertDialog(
            response = response,
            onDismissRequest = {},
            onPositiveButtonClick = {},
        )
    }
}