package co.ke.xently.features.location.picker.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.ke.xently.features.location.picker.R
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyThemePreview

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun PickLocationBottomBarCard(
    modifier: Modifier = Modifier,
    enableUseMyLocation: Boolean = true,
    enableConfirmSelection: Boolean = true,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onClickUseMyLocation: () -> Unit,
    onClickConfirmSelection: () -> Unit,
) {
    ElevatedCard(
        modifier = modifier,
        shape = BottomSheetDefaults.ExpandedShape,
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 16.dp,
        ),
        colors = CardDefaults.cardColors(
            containerColor = BottomSheetDefaults.ContainerColor,
        ),
    ) {
        SnackbarHost(hostState = snackbarHostState)

        OutlinedButton(
            onClick = onClickUseMyLocation,
            enabled = enableUseMyLocation,
            modifier = Modifier
                .fillMaxWidth()
                .padding(PaddingValues(16.dp)),
            contentPadding = PaddingValues(16.dp),
        ) {
            Text(
                text = stringResource(R.string.action_use_my_current_location),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge,
            )
        }

        Button(
            onClick = onClickConfirmSelection,
            enabled = enableConfirmSelection,
            modifier = Modifier
                .fillMaxWidth()
                .padding(PaddingValues(16.dp))
                .navigationBarsPadding(),
            contentPadding = PaddingValues(16.dp),
        ) {
            Text(
                text = stringResource(R.string.action_confirm_selection),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
        }
    }
}

@XentlyThemePreview
@Composable
private fun PickLocationBottomBarCardPreview() {
    XentlyTheme {
        PickLocationBottomBarCard(
            modifier = Modifier.padding(16.dp),
            onClickUseMyLocation = {},
            onClickConfirmSelection = {},
        )
    }
}