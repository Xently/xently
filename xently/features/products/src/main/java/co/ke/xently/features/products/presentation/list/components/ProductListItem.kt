package co.ke.xently.features.products.presentation.list.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
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
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material3.placeholder
import com.google.accompanist.placeholder.material3.shimmer

@Composable
fun ProductListItem(
    product: Product,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    trailingTitleContent: @Composable (() -> Unit) = {},
) {
    ListItem(
        modifier = modifier,
        leadingContent = {
            Card(
                modifier = Modifier
                    .size(size = 60.dp)
                    .placeholder(visible = isLoading, highlight = PlaceholderHighlight.shimmer())
            ) {
                var index by rememberSaveable(product.id) { mutableIntStateOf(0) }
                XentlyImage(
                    data = product.images.getOrNull(index),
                    modifier = Modifier.fillMaxSize(),
                    onError = {
                        if (index != product.images.lastIndex) index += 1
                    },
                )
            }
        },
        headlineContent = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = product.toString(),
                    modifier = Modifier
                        .weight(1f)
                        .placeholder(
                            visible = isLoading,
                            highlight = PlaceholderHighlight.shimmer(),
                        ),
                    fontWeight = FontWeight.Bold,
                    maxLines = if (product.description.isNullOrBlank()) 3 else 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelLarge,
                )
                Text(
                    text = product.unitPrice.formatPrice("KES", 0),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.placeholder(
                        visible = isLoading,
                        highlight = PlaceholderHighlight.shimmer(),
                    ),
                )
                trailingTitleContent()
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
                    modifier = Modifier
                        .placeholder(
                            visible = isLoading,
                            highlight = PlaceholderHighlight.shimmer(),
                        )
                        .clickable(
                            role = Role.Checkbox,
                            indication = ripple(radius = 1_000.dp),
                            interactionSource = remember { MutableInteractionSource() },
                        ) { expand = !expand },
                )
            }
        },
    )
}

internal data class ProductListItemParameter(
    val product: Product,
    val isLoading: Boolean = false,
)

internal class ProductListItemParameterProvider :
    PreviewParameterProvider<ProductListItemParameter> {
    override val values: Sequence<ProductListItemParameter>
        get() = sequenceOf(
            ProductListItemParameter(
                Product(
                    name = "Chips Kuku",
                    unitPrice = 19234.0,
                    description = "A mix of pilau and roasted potatoes garnished with a side of paprika grilled tomatoes.",
                )
            ),
            ProductListItemParameter(
                Product(
                    name = "Chips Kuku",
                    unitPrice = 19234.0,
                    description = "A mix of pilau and roasted potatoes garnished with a side of paprika grilled tomatoes.",
                ), isLoading = true
            ),
            ProductListItemParameter(
                Product(
                    name = "Chips Kuku, Bhajia, Smokies, Fish, Yoghurt, Sugar & Chicken",
                    unitPrice = 134.0,
                )
            ),
            ProductListItemParameter(
                Product(
                    name = "Chips Kuku, Bhajia, Smokies, Fish, Yoghurt, Sugar & Chicken",
                    unitPrice = 134.0,
                ), isLoading = true
            ),
        )
}

@XentlyThemePreview
@Composable
private fun ProductListItemPreview(
    @PreviewParameter(ProductListItemParameterProvider::class)
    parameter: ProductListItemParameter,
) {
    XentlyTheme {
        ProductListItem(
            product = parameter.product,
            isLoading = parameter.isLoading,
        )
    }
}