package com.kwanzatukule.features.cart.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import co.ke.xently.libraries.ui.core.XentlyPreview
import co.ke.xently.libraries.ui.core.domain.formatPrice
import com.kwanzatukule.features.cart.domain.ShoppingCart
import com.kwanzatukule.features.catalogue.domain.Product
import com.kwanzatukule.features.core.presentation.theme.KwanzaTukuleTheme
import kotlin.random.Random

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ShoppingCartTotalBottomBarCard(
    shoppingCart: ShoppingCart,
    submitLabel: String,
    modifier: Modifier = Modifier,
    onClickSubmit: () -> Unit,
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
        val sheetSnackbarHostState = remember { SnackbarHostState() }
        SnackbarHost(hostState = sheetSnackbarHostState)

        Text(
            text = "Subtotal",
            fontWeight = FontWeight.Light,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .fillMaxWidth()
                .padding(PaddingValues(16.dp)),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PaddingValues(horizontal = 16.dp, vertical = 4.dp)),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = "Shipping", fontWeight = FontWeight.W200)
            Text(text = shoppingCart.shippingPrice.formatPrice("KES"))
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PaddingValues(horizontal = 16.dp, vertical = 4.dp)),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = "VAT (16%)", fontWeight = FontWeight.W200)
            Text(text = shoppingCart.tax.formatPrice("KES"))
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PaddingValues(horizontal = 16.dp, vertical = 4.dp)),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = "Total", fontWeight = FontWeight.W200)
            Text(text = shoppingCart.totalPrice.formatPrice("KES"))
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PaddingValues(horizontal = 16.dp, vertical = 4.dp)),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = "Grand Total", fontWeight = FontWeight.Bold)
            Text(
                text = shoppingCart.totalPriceIncludingTaxAndShipping.formatPrice("KES"),
                fontWeight = FontWeight.Bold,
            )
        }
        Button(
            onClick = onClickSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .padding(PaddingValues(16.dp))
                .navigationBarsPadding(),
            contentPadding = PaddingValues(16.dp),
        ) {
            Text(
                text = submitLabel,
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
private fun ShoppingCartTotalCardPreview() {
    KwanzaTukuleTheme {
        val shoppingCart = remember {
            ShoppingCart(
                shippingPrice = Random.nextInt(500),
                items = listOf(
                    ShoppingCart.Item(
                        Product(
                            name = "Random product name",
                            price = 1256,
                            image = "https://picsum.photos/200/300",
                        ),
                        1,
                    ),
                    ShoppingCart.Item(
                        Product(
                            name = "Random product name",
                            price = 456,
                            image = "https://picsum.photos/200/300",
                        ),
                        3,
                    ),
                    ShoppingCart.Item(
                        Product(
                            name = "Random product name",
                            price = 234,
                            image = "https://picsum.photos/200/300",
                        ),
                        1,
                    ),
                ).mapIndexed { index, item -> item.copy(id = (index + 1).toLong()) },
            )
        }
        ShoppingCartTotalBottomBarCard(shoppingCart = shoppingCart, submitLabel = "Place order") {

        }
    }
}