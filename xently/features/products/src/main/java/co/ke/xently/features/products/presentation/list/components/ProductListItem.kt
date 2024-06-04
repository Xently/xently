package co.ke.xently.features.products.presentation.list.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import co.ke.xently.features.products.data.domain.Product
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyThemePreview
import co.ke.xently.libraries.ui.core.domain.formatPrice
import co.ke.xently.libraries.ui.image.XentlyImage

@Composable
internal fun ProductListItem(product: Product, modifier: Modifier = Modifier) {
    ListItem(
        modifier = modifier,
        leadingContent = {
            Card(modifier = Modifier.size(60.dp)) {
                val image = product.images.firstOrNull()
                if (image != null) {
                    XentlyImage(
                        data = image,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        },
        headlineContent = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = product.descriptiveName.ifBlank { product.name },
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold,
                    maxLines = if (product.description.isNullOrBlank()) 2 else 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelLarge,
                )
                Text(
                    text = product.unitPrice.formatPrice("KES", false),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        },
        supportingContent = if (product.description.isNullOrBlank()) null else {
            {
                var expand by rememberSaveable { mutableStateOf(false) }
                Text(
                    text = product.description!!,
                    maxLines = if (expand) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.clickable { expand = !expand },
                )
            }
        },
    )
}

private class ProductListItemParameterProvider : PreviewParameterProvider<Product> {
    override val values: Sequence<Product>
        get() = sequenceOf(
            Product(
                name = "Chips Kuku",
                unitPrice = 19234.0,
                description = "A mix of pilau and roasted potatoes garnished with a side of paprika grilled tomatoes.",
            ),
            Product(
                name = "Chips Kuku, Bhajia, Smokies, Fish, Yoghurt, Sugar & Chicken",
                unitPrice = 134.0,
            ),
        )
}

@XentlyThemePreview
@Composable
private fun ProductListItemPreview(
    @PreviewParameter(ProductListItemParameterProvider::class)
    product: Product,
) {
    XentlyTheme {
        ProductListItem(
            product = product,
        )
    }
}