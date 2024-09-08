package com.kwanzatukule.features.catalogue.presentation.components


import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.RemoveShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconToggleButton
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import co.ke.xently.libraries.ui.core.XentlyPreview
import co.ke.xently.libraries.ui.core.domain.formatPrice
import com.kwanzatukule.features.catalogue.domain.Product
import com.kwanzatukule.features.core.presentation.theme.KwanzaTukuleTheme

@Composable
fun ProductCard(
    modifier: Modifier = Modifier,
    product: Product,
    onClick: () -> Unit,
    addToOrRemoveFromShoppingCart: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .width(IntrinsicSize.Max)
            .then(modifier)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Card(
                shape = MaterialTheme.shapes.small,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(128.dp),
            ) {
                co.ke.xently.libraries.ui.image.XentlyImage(
                    data = product.image,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds,
                )
            }

            var isInShoppingCart by remember(product.inShoppingCart) {
                mutableStateOf(product.inShoppingCart)
            }

            val toggleButtonColors = IconButtonDefaults.outlinedIconToggleButtonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                checkedContainerColor = MaterialTheme.colorScheme.errorContainer,
            )
            OutlinedIconToggleButton(
                checked = isInShoppingCart,
                onCheckedChange = {
                    isInShoppingCart = it
                    addToOrRemoveFromShoppingCart(it)
                },
                colors = toggleButtonColors,
            ) {
                AnimatedContent(
                    targetState = isInShoppingCart,
                    label = "Add or remove product from shopping cart",
                ) { inShoppingCart ->
                    if (inShoppingCart) {
                        Icon(
                            Icons.Default.RemoveShoppingCart,
                            contentDescription = "Remove ${product.name} from shopping cart",
                        )
                    } else {
                        Icon(
                            Icons.Default.AddShoppingCart,
                            contentDescription = "Add ${product.name} to shopping cart",
                            tint = contentColorFor(toggleButtonColors.containerColor),
                        )
                    }
                }
            }
        }
        Text(
            text = product.name,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(PaddingValues(horizontal = 8.dp))
                .basicMarquee(),
        )
        Text(
            text = remember(product) {
                derivedStateOf {
                    product.price.formatPrice(product.currency)
                }
            }.value,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .padding(bottom = 8.dp)
                .padding(horizontal = 8.dp),
        )
    }
}

private class ProductPreviewProvider : PreviewParameterProvider<Product> {
    override val values: Sequence<Product>
        get() = sequenceOf(
            Product(name = "Product Name", price = 100),
            Product(
                name = "Product Name",
                price = 200,
                image = "https://example.com/product1.jpg",
            ),
            Product(
                name = "Product Name",
                price = 2_080,
                image = "https://example.com/product1.jpg",
            ),
            Product(
                name = "Product with a long name that should be truncated",
                price = 2_080,
                image = "https://example.com/product1.jpg",
            ),
        )
}

@XentlyPreview
@Composable
private fun ProductCardPreview(
    @PreviewParameter(ProductPreviewProvider::class)
    product: Product,
) {
    KwanzaTukuleTheme {
        ProductCard(
            product = product,
            onClick = {},
            addToOrRemoveFromShoppingCart = {},
        )
    }
}