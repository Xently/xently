package com.kwanzatukule.features.cart.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.kwanzatukule.features.cart.domain.ShoppingCart
import com.kwanzatukule.features.core.domain.formatPrice

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ShoppingCartTotalCard(
    shoppingCart: ShoppingCart,
    submitLabel: String,
    modifier: Modifier = Modifier,
    onClickSubmit: () -> Unit,
) {
    Card(modifier = modifier, shape = BottomSheetDefaults.ExpandedShape) {
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
            Text(text = "VAT", fontWeight = FontWeight.Bold)
            Text(text = 0.formatPrice("KES"))
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PaddingValues(horizontal = 16.dp, vertical = 4.dp)),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = "Total", fontWeight = FontWeight.Bold)
            Text(text = shoppingCart.totalPrice.formatPrice("KES"))
        }
        Button(
            onClick = onClickSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .padding(PaddingValues(16.dp)),
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