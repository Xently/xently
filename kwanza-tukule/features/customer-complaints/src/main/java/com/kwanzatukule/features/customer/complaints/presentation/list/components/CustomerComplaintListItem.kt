package com.kwanzatukule.features.customer.complaints.presentation.list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.ke.xently.libraries.ui.core.XentlyPreview
import com.kwanzatukule.features.core.presentation.theme.KwanzaTukuleTheme
import com.kwanzatukule.features.customer.complaints.domain.CustomerComplaint
import kotlin.random.Random

@Composable
internal fun CustomerComplaintListItem(customer: CustomerComplaint) {
    Surface {
        Row(
            modifier = Modifier.padding(start = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val backgroundColor = MaterialTheme.colorScheme.primary
            CompositionLocalProvider(LocalContentColor provides contentColorFor(backgroundColor)) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .background(color = backgroundColor, shape = CircleShape),
                )
            }
            ListItem(
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                overlineContent = {
                    Text(text = customer.name)
                },
                headlineContent = {
                    Text(text = customer.phone)
                },
                supportingContent = {
                    Text(text = customer.email)
                },
            )
        }
    }
}

@XentlyPreview
@Composable
private fun CustomerComplaintListItemPreview() {
    KwanzaTukuleTheme {
        val customer = CustomerComplaint(
            name = "Kibera",
            email = "customer.1@example.com",
            phone = "+2547123456${Random.nextInt(10, 99)}",
        )
        CustomerComplaintListItem(customer = customer)
    }
}