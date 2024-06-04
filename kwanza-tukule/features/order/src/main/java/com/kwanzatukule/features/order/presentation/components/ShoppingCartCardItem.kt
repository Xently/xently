package com.kwanzatukule.features.order.presentation.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import co.ke.xently.libraries.ui.core.XentlyPreview
import co.ke.xently.libraries.ui.core.domain.formatNumber
import co.ke.xently.libraries.ui.core.domain.formatPrice
import com.kwanzatukule.features.cart.domain.ShoppingCart
import com.kwanzatukule.features.cart.presentation.components.ShoppingCartLineCart
import com.kwanzatukule.features.catalogue.domain.Product
import com.kwanzatukule.features.core.presentation.theme.KwanzaTukuleTheme


@Composable
internal fun ShoppingCartLineCart(item: ShoppingCart.Item, modifier: Modifier = Modifier) {
    ShoppingCartLineCart(item = item, modifier = modifier) {
        Text(
            text = "x",
            fontWeight = FontWeight.Light,
        )
        Text(
            text = item.quantity.formatNumber(),
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = item.totalPrice.formatPrice("KES"),
            fontWeight = FontWeight.Bold,
        )
    }
}

@XentlyPreview
@Composable
private fun ShoppingCartLineCartPreview() {
    KwanzaTukuleTheme {
        val item = ShoppingCart.Item(
            Product(
                name = "Product with a really really long name that occupies more than three lines",
                price = 456,
                image = "https://picsum.photos/200/300",
            ),
            3,
        )
        ShoppingCartLineCart(item = item)
    }
}