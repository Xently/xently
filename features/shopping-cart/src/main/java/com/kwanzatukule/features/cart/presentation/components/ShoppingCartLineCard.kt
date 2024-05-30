package com.kwanzatukule.features.cart.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import co.ke.xently.libraries.ui.core.XentlyPreview
import com.kwanzatukule.features.cart.domain.ShoppingCart
import com.kwanzatukule.features.catalogue.domain.Product
import com.kwanzatukule.features.core.domain.formatNumber
import com.kwanzatukule.features.core.domain.formatPrice
import com.kwanzatukule.features.core.presentation.theme.KwanzaTukuleTheme

@Composable
fun ShoppingCartLineCart(
    item: ShoppingCart.Item,
    modifier: Modifier = Modifier,
    trailingContent: (@Composable () -> Unit)? = null,
    subTrailingContent: @Composable RowScope.() -> Unit = { },
) {
    Card(modifier = modifier, colors = CardDefaults.outlinedCardColors()) {
        ShoppingListItem(item = item, trailingContent = trailingContent)
        CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.titleMedium) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                modifier = Modifier.padding(horizontal = 16.dp),
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = item.product.price.formatPrice(item.product.currency),
                    fontWeight = FontWeight.Bold,
                )
                subTrailingContent()
            }
        }
    }
}

@Composable
fun ShoppingListItem(
    item: ShoppingCart.Item,
    trailingContent: @Composable (() -> Unit)? = null,
) {
    ListItem(
        leadingContent = {
            Card(
                modifier = Modifier.size(60.dp),
                shape = MaterialTheme.shapes.medium,
            ) {
                co.ke.xently.libraries.ui.image.XentlyAsyncImage(
                    item.product.image,
                    contentDescription = "Image of ${item.product.name}",
                    modifier = Modifier.fillMaxSize(),
                )
            }
        },
        headlineContent = {
            Text(
                text = item.product.name,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        },
        supportingContent = {
            Text(text = "Qty: ${item.quantity}")
        },
        trailingContent = trailingContent,
    )
}

@Composable
internal fun ShoppingCartLineCart(
    item: ShoppingCart.Item,
    modifier: Modifier = Modifier,
    remove: () -> Unit,
    decrementQuantity: () -> Unit,
    incrementQuantity: () -> Unit,
) {
    ShoppingCartLineCart(
        item = item,
        modifier = modifier,
        trailingContent = {
            IconButton(onClick = remove) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Remove ${item.product.name} from shopping cart",
                )
            }
        },
    ) {
        IconButton(onClick = decrementQuantity) {
            Icon(
                Icons.Default.Remove,
                contentDescription = "Decrement quantity of ${item.product.name} by 1",
            )
        }
        Text(
            text = item.quantity.formatNumber(),
            fontWeight = FontWeight.SemiBold,
        )
        /*val focusManager = LocalFocusManager.current
        OutlinedTextField(
            modifier = Modifier.size(48.dp),
            value = item.quantity.toString(),
            onValueChange = updateQuantity,
            singleLine = true,
            maxLines = 1,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            keyboardActions = KeyboardActions {
                focusManager.clearFocus()
            },
        )*/
        IconButton(onClick = incrementQuantity) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Increment quantity of ${item.product.name} by 1",
            )
        }
    }
}

@XentlyPreview
@Composable
private fun ShoppingCartLineCartPreview() {
    KwanzaTukuleTheme {
        ShoppingCartLineCart(
            item = ShoppingCart.Item(
                Product(
                    name = "Product with a really really long name that occupies more than three lines",
                    price = 456,
                    image = "https://picsum.photos/200/300",
                ),
                3,
            ),
            remove = {},
            decrementQuantity = {},
            incrementQuantity = {},
        )
    }
}