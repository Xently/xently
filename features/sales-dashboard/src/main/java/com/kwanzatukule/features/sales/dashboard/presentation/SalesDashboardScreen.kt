package com.kwanzatukule.features.sales.dashboard.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.kwanzatukule.features.core.presentation.KwanzaPreview
import com.kwanzatukule.features.core.presentation.theme.KwanzaTukuleTheme
import com.kwanzatukule.features.sales.dashboard.domain.SalesDashboardItem
import kotlin.random.Random

@Composable
fun SalesDashboardScreen(component: SalesDashboardComponent, modifier: Modifier = Modifier) {
    val uiState by component.uiState.subscribeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
    ) {
        AnimatedVisibility(visible = uiState.isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        LazyVerticalGrid(
            modifier = Modifier.weight(1f),
            columns = GridCells.Adaptive(150.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(uiState.content, key = { it.name }) { dashboardItem ->
                Card(onClick = { component.onItemClicked(dashboardItem) }) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            Icons.Default.Analytics,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clipToBounds(),
                        )
                        Text(text = dashboardItem.name)
                        Row(
                            modifier = Modifier.padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            Text(
                                text = dashboardItem.actual.toString(),
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                text = " / ",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.headlineSmall,
                            )
                            Text(
                                text = dashboardItem.target.toString(),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (dashboardItem.status == SalesDashboardItem.Status.AHEAD) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.primary
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

private class SalesDashboardUiStatePreviewProvider :
    PreviewParameterProvider<SalesDashboardUiState> {
    override val values: Sequence<SalesDashboardUiState>
        get() {
            val content = List(20) {
                SalesDashboardItem(
                    name = "Item $it",
                    actual = Random.nextInt(50_000),
                    target = Random.nextInt(20_000, 50_000),
                    status = if ((it + 1) % 3 == 0) {
                        SalesDashboardItem.Status.AHEAD
                    } else {
                        SalesDashboardItem.Status.SHORT
                    },
                )
            }
            return sequenceOf(
                SalesDashboardUiState(),
                SalesDashboardUiState(isLoading = true),
                SalesDashboardUiState(content = content),
                SalesDashboardUiState(isLoading = true, content = content),
            )
        }
}


@KwanzaPreview
@Composable
private fun SalesDashboardScreenPreview(
    @PreviewParameter(SalesDashboardUiStatePreviewProvider::class)
    uiState: SalesDashboardUiState,
) {
    KwanzaTukuleTheme {
        Scaffold {
            SalesDashboardScreen(
                modifier = Modifier.padding(it),
                component = SalesDashboardComponent.Fake(uiState),
            )
        }
    }
}