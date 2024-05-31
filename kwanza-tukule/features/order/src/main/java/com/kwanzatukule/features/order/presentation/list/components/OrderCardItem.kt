package com.kwanzatukule.features.order.presentation.list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import co.ke.xently.libraries.ui.core.XentlyPreview
import com.kwanzatukule.features.core.presentation.theme.KwanzaTukuleTheme
import com.kwanzatukule.features.order.domain.Order
import com.kwanzatukule.libraries.data.customer.domain.Customer
import com.kwanzatukule.libraries.data.route.domain.Route
import com.kwanzatukule.libraries.data.route.domain.RouteSummary
import kotlin.random.Random

@Composable
fun OrderCardItem(
    order: Order,
    modifier: Modifier = Modifier,
    onClickViewShoppingList: () -> Unit = {},
) {
    Card(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp)
                .padding(top = 8.dp),
        ) {
            Text(
                text = "Order No:",
                fontWeight = FontWeight.Bold,
            )
            Text(text = order.id)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
        ) {
            Text(
                text = "Route:",
                fontWeight = FontWeight.Bold,
            )
            Text(text = order.route.name)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
        ) {
            Text(
                text = "Outlet:",
                fontWeight = FontWeight.Bold,
            )
            Text(text = order.customer.name)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
        ) {
            Text(
                text = "Phone No:",
                fontWeight = FontWeight.Bold,
            )
            Text(text = order.customer.phone, textDecoration = TextDecoration.Underline)
        }
        Surface(
            color = order.status.color(),
            shape = RoundedCornerShape(30),
            modifier = Modifier.padding(horizontal = 14.dp),
        ) {
            Text(
                text = stringResource(order.status.localeName),
                modifier = Modifier.padding(PaddingValues(horizontal = 8.dp)),
            )
        }
        OutlinedButton(
            onClick = onClickViewShoppingList,
            contentPadding = PaddingValues(),
            shape = RoundedCornerShape(30),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
        ) {
            Text(text = "View Shopping List")
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
            )
        }
    }
}

@XentlyPreview
@Composable
private fun OrderCardItemPreview() {
    val order = remember {
        val route = Route(
            id = 1,
            name = "Kibera",
            description = "Kibera route description...",
            summary = RouteSummary(
                bookedOrder = Random.nextInt(100),
                variance = Random.nextInt(Int.MIN_VALUE, Int.MAX_VALUE),
                numberOfCustomers = Random.nextInt(100),
                totalRouteCustomers = Random.nextInt(100),
                geographicalDistance = Random.nextInt(1_000, 10_000),
            ),
        )
        val customer = Customer(
            id = 1,
            name = "John Doe",
            email = "customer@example.com",
            phone = "+2547123456${Random.nextInt(10, 99)}",
        )
        Order(
            id = "ORDER123",
            customer = customer,
            route = route,
            status = Order.Status.entries.random(),
        )
    }
    KwanzaTukuleTheme {
        OrderCardItem(order = order)
    }
}