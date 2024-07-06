package co.ke.xently.features.customers.presentation.list.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import co.ke.xently.features.customers.R
import co.ke.xently.features.customers.data.domain.Customer
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyThemePreview
import kotlin.random.Random

@Composable
internal fun CustomerListItem(
    customer: Customer,
    position: Int,
    modifier: Modifier = Modifier,
) {
    ListItem(
        modifier = modifier,
        leadingContent = {
            Card(modifier = Modifier.size(size = 60.dp)) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally),
                )
            }
        },
        headlineContent = {
            Text(
                text = stringResource(R.string.position_points, position, customer.totalPoints),
                fontWeight = FontWeight.Bold,
            )
        },
        supportingContent = {
            Text(
                text = customer.name ?: stringResource(R.string.anonymous),
                fontWeight = FontWeight.Light,
            )
        },
    )
}

private class CustomerListItemParameterProvider : PreviewParameterProvider<Customer> {
    override val values: Sequence<Customer>
        get() = sequenceOf(
            Customer(
                name = "Jane Doe",
                id = "1",
                position = Random.nextInt(1, 2),
                totalPoints = Random.nextInt(10, 1_000),
            ),
            Customer(
                name = "John Doe",
                id = "1",
                position = Random.nextInt(1, 2),
                totalPoints = Random.nextInt(10, 1_000),
            ),
        )
}

@XentlyThemePreview
@Composable
private fun CustomerListItemPreview(
    @PreviewParameter(CustomerListItemParameterProvider::class)
    customer: Customer,
) {
    XentlyTheme {
        CustomerListItem(
            customer = customer,
            position = Random.nextInt(1, 2),
        )
    }
}