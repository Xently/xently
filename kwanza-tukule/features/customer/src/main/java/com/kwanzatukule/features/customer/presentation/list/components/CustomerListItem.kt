package com.kwanzatukule.features.customer.presentation.list.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Phone
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import co.ke.xently.libraries.ui.core.XentlyPreview
import com.kwanzatukule.features.core.domain.formatNumber
import com.kwanzatukule.features.core.domain.formatPrice
import com.kwanzatukule.features.core.presentation.TextIconButton
import com.kwanzatukule.features.core.presentation.theme.KwanzaTukuleTheme
import com.kwanzatukule.features.customer.presentation.list.LocalCanViewMissedOpportunities
import com.kwanzatukule.libraries.data.customer.domain.Customer
import kotlin.random.Random

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CustomerListItem(
    customer: Customer,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    trailingContent: (@Composable () -> Unit)? = null,
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        shape = RectangleShape,
        colors = CardDefaults.outlinedCardColors(),
    ) {
        ListItem(
            leadingContent = {
                Card(
                    modifier = Modifier.size(60.dp),
                    shape = MaterialTheme.shapes.medium,
                ) {
                    /*XentlyImage(
                        customer.image,
                        contentDescription = "Image of ${customer.name}",
                        modifier = Modifier.fillMaxSize(),
                    )*/
                }
            },
            headlineContent = {
                Text(
                    text = customer.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            supportingContent = {
                TextIconButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Default.Phone, contentDescription = null)
                    Text(
                        text = customer.phone,
                        modifier = Modifier.basicMarquee(),
                        textDecoration = TextDecoration.Underline,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            },
            trailingContent = trailingContent,
        )
        var seeMore by rememberSaveable(customer.id) { mutableStateOf(false) }

        CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.titleMedium) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                TextIconButton(onClick = { /*TODO*/ }, modifier = Modifier.fillMaxWidth(.85f)) {
                    Icon(Icons.Default.MailOutline, contentDescription = null)
                    Text(
                        text = customer.email,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.basicMarquee(),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }

                IconButton(onClick = { seeMore = !seeMore }) {
                    AnimatedContent(targetState = seeMore, label = "Expand/Collapse more") {
                        if (it) {
                            Icon(
                                Icons.Default.KeyboardArrowUp,
                                contentDescription = "See less",
                            )
                        } else {
                            Icon(
                                Icons.Default.KeyboardArrowDown,
                                contentDescription = "See more",
                            )
                        }
                    }
                }
            }
        }

        AnimatedVisibility(visible = seeMore) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CustomerSummary(
                    label = "Route",
                    value = { Text(text = "Kibera") },
                )
                CustomerSummary(
                    label = "Last order date",
                    value = { Text(text = "2019-01-01") },
                )
                CustomerSummary(
                    label = "Pending payments",
                    value = { Text(text = 1234.formatPrice("KES")) },
                )
                CustomerSummary(
                    label = "Average order size per order",
                    value = { Text(text = 1234.formatNumber()) },
                )
                CustomerSummary(
                    label = "Frequency of orders per month",
                    value = { Text(text = 14.formatNumber()) },
                )
                CustomerSummary(
                    label = "Total purchase this month",
                    value = { Text(text = 67594.formatPrice("KES")) },
                )
                CustomerSummary(
                    label = "Total purchase so far",
                    value = { Text(text = 679594.formatPrice("KES")) },
                )
                CustomerSummary(
                    label = "Total gross profit",
                    value = { Text(text = 79594.formatPrice("KES")) },
                )
                CustomerSummary(
                    label = "Last order items",
                    modifier = Modifier.clickable(role = Role.Button, onClick = { /* TODO */ }),
                    value = {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                        )
                    },
                )
                if (LocalCanViewMissedOpportunities.current) {
                    CustomerSummary(
                        label = "Missed opportunities",
                        modifier = Modifier.clickable(role = Role.Button, onClick = { /* TODO */ }),
                        value = {
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                            )
                        },
                    )
                }
                CustomerSummary(
                    label = "Customer complaints",
                    modifier = Modifier.clickable(role = Role.Button, onClick = { /* TODO */ }),
                    value = {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun CustomerSummary(
    modifier: Modifier = Modifier,
    label: String,
    value: @Composable () -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "${label}:",
            fontWeight = FontWeight.Bold,
        )
        value()
    }
}

@XentlyPreview
@Composable
private fun CustomerListItemPreview() {
    KwanzaTukuleTheme {
        val customer = Customer(
            name = "Kibera",
            email = "customer.1@example.com",
            phone = "+2547123456${Random.nextInt(10, 99)}",
        )
        CustomerListItem(customer = customer)
    }
}