package co.ke.xently.features.stores.presentation.moredetails.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.ke.xently.libraries.ui.core.components.shimmer

@Composable
internal fun MoreDetailListItem(
    title: String,
    showDivider: Boolean = true,
    isLoading: Boolean = false,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ListItem(
            supportingContent = content,
            headlineContent = {
                Text(
                    text = title,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.shimmer(isLoading),
                )
            },
        )
        if (showDivider) {
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        }
    }
}