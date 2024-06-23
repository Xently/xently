package co.ke.xently.features.ui.core.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.ke.xently.features.ui.core.R
import co.ke.xently.features.ui.core.presentation.LocalEventHandler
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.LocalAuthenticationState
import co.ke.xently.libraries.ui.core.XentlyThemePreview

@Composable
fun LoginAndRetryButtonsRow(modifier: Modifier = Modifier, onRetry: () -> Unit) {
    val eventHandler = LocalEventHandler.current
    val authenticationState by LocalAuthenticationState.current

    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        if (authenticationState.isAuthenticated) {
            LaunchedEffect(Unit) {
                onRetry()
            }
        }
        OutlinedButton(onClick = onRetry) {
            Text(text = stringResource(R.string.action_retry))
        }
        /*LaunchedEffect(Unit) {
            eventHandler.requestAuthentication()
        }*/

        Button(onClick = eventHandler::requestAuthentication) {
            Text(text = stringResource(R.string.action_login))
        }
    }
}

@XentlyThemePreview
@Composable
private fun LoginAndRetryButtonsRowPreview() {
    XentlyTheme {
        LoginAndRetryButtonsRow {}
    }
}