package com.kwanzatukule.features.customer.home.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.kwanzatukule.features.core.presentation.KwanzaPreview
import com.kwanzatukule.features.core.presentation.XentlyAsyncImage
import com.kwanzatukule.features.core.presentation.theme.KwanzaTukuleTheme


@Composable
fun AdvertCard(
    advert: com.kwanzatukule.features.customer.home.data.Advert,
    modifier: Modifier = Modifier,
) {
    ListItem(
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.onSurface,
            headlineColor = MaterialTheme.colorScheme.surface,
            overlineColor = MaterialTheme.colorScheme.surface,
            supportingColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = modifier.clip(MaterialTheme.shapes.medium),
        headlineContent = {
            Text(
                text = advert.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
            )
        },
        overlineContent = if (advert.headline == null) null else {
            {
                Text(
                    text = advert.headline,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        },
        supportingContent = if (advert.subtitle == null) null else {
            {
                Text(
                    text = advert.subtitle,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        },
        trailingContent = if (advert.image == null) null else {
            {
                XentlyAsyncImage(
                    data = advert.image,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth(.5f)
                        .height(100.dp),
                )
            }
        },
    )
}

class AdvertParameterProvider :
    PreviewParameterProvider<com.kwanzatukule.features.customer.home.data.Advert> {
    override val values = sequenceOf(
        com.kwanzatukule.features.customer.home.data.Advert(title = "Today's Deal"),
        com.kwanzatukule.features.customer.home.data.Advert(
            title = "Today's Deal",
            image = "https://picsum.photos/200/300",
        ),
        com.kwanzatukule.features.customer.home.data.Advert(
            title = "Today's Deal",
            subtitle = "20% off",
        ),
        com.kwanzatukule.features.customer.home.data.Advert(
            title = "Today's Deal",
            subtitle = "20% off",
            image = "https://picsum.photos/200/300",
        ),
        com.kwanzatukule.features.customer.home.data.Advert(
            title = "Today's Deal",
            headline = "KES. 80",
        ),
        com.kwanzatukule.features.customer.home.data.Advert(
            title = "Today's Deal",
            headline = "KES. 80",
            image = "https://picsum.photos/200/300",
        ),
        com.kwanzatukule.features.customer.home.data.Advert(
            title = "Today's Deal",
            subtitle = "20% off",
            headline = "KES. 80",
        ),
        com.kwanzatukule.features.customer.home.data.Advert(
            title = "Today's Deal",
            subtitle = "20% off",
            headline = "KES. 80",
            image = "https://picsum.photos/200/300",
        ),
    )
}

@KwanzaPreview
@Composable
private fun AdvertCardPreview(
    @PreviewParameter(AdvertParameterProvider::class)
    advert: com.kwanzatukule.features.customer.home.data.Advert,
) {
    KwanzaTukuleTheme {
        AdvertCard(advert = advert)
    }
}