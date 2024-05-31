package com.kwanzatukule.features.catalogue.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import co.ke.xently.libraries.pagination.presentation.PaginatedLazyRow
import co.ke.xently.libraries.ui.core.XentlyPreview
import com.kwanzatukule.features.catalogue.domain.Category
import com.kwanzatukule.features.core.presentation.theme.KwanzaTukuleTheme
import kotlinx.coroutines.flow.flow


@Composable
fun CategoryLazyRow(
    categories: LazyPagingItems<Category>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    onClick: (Category) -> Unit,
) {
    val errorStateContent: @Composable ColumnScope.(Throwable) -> Unit = {
        Text(text = it.message!!)
        Button(onClick = categories::retry) {
            Text(text = "Retry")
        }
    }
    PaginatedLazyRow(
        items = categories,
        modifier = modifier,/*.height(IntrinsicSize.Max)*/
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        emptyContentMessage = "No categories",
        errorStateContent = errorStateContent,
        appendErrorStateContent = errorStateContent,
        prependErrorStateContent = errorStateContent,
    ) {
        items(categories.itemCount, key = { categories[it]!!.id }) { index ->
            val category = categories[index]!!
            CategoryCard(
                modifier = Modifier.fillMaxHeight(),
                category = category,
                onClick = { onClick(category) },
            )
        }
    }
}


@XentlyPreview
@Composable
private fun CategoryLazyRowPreview() {
    val categories = flow {
        emit(
            PagingData.from(
                listOf(
                    Category(name = "Bevarage"),
                    Category(
                        name = "Sugar",
                        image = "https://example.com/category1.jpg",
                    ),
                    Category(
                        name = "Flour",
                        image = "https://example.com/category2.jpg",
                    ),
                    Category(
                        name = "Cooking Oil",
                        image = "https://example.com/category3.jpg",
                    ),
                    Category(
                        name = "Category Name",
                        image = "https://example.com/category4.jpg",
                    ),
                    Category(
                        name = "Category Name",
                        image = "https://example.com/category4.jpg",
                    ),
                )
            )
        )
    }.collectAsLazyPagingItems()
    KwanzaTukuleTheme {
        CategoryLazyRow(
            modifier = Modifier.fillMaxWidth(),
            onClick = {},
            categories = categories,
        )
    }
}