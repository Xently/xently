package com.kwanzatukule.features.cart.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.kwanzatukule.features.cart.presentation.LocalShoppingCartState
import com.kwanzatukule.features.core.presentation.KwanzaPreview
import com.kwanzatukule.features.core.presentation.theme.KwanzaTukuleTheme

@Composable
fun ShoppingCartBadge(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val shoppingCart by LocalShoppingCartState.current

    BadgedBox(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clickable(
                onClick = onClick,
                enabled = shoppingCart.numberOfItems > 0,
                role = Role.Button,
                interactionSource = remember { MutableInteractionSource() },
                indication = androidx.compose.material.ripple.rememberRipple(
                    bounded = false,
                    radius = 40.dp / 2
                ),
            ),
        content = {
            Icon(
                Icons.Default.ShoppingCart,
                contentDescription = "Open shopping cart",
            )
        },
        badge = {
            Badge {
                val numberOfItems by remember(shoppingCart) {
                    derivedStateOf {
                        shoppingCart.numberOfItems.toString()
                    }
                }
                Text(
                    numberOfItems,
                    modifier = Modifier.semantics {
                        contentDescription =
                            "$numberOfItems items in the shopping cart"
                    }
                )
            }
        },
    )
}

@KwanzaPreview
@Composable
private fun ShoppingCartBadgePreview() {
    KwanzaTukuleTheme {
        Surface {
            ShoppingCartBadge(onClick = { })
        }
    }
}