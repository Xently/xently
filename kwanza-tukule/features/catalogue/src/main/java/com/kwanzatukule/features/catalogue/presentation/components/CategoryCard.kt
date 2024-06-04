package com.kwanzatukule.features.catalogue.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import co.ke.xently.libraries.ui.core.XentlyPreview
import com.kwanzatukule.features.catalogue.domain.Category
import com.kwanzatukule.features.core.presentation.theme.KwanzaTukuleTheme

@Composable
fun CategoryCard(modifier: Modifier = Modifier, category: Category, onClick: () -> Unit) {
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Card(
            modifier = Modifier.size(128.dp),
            shape = MaterialTheme.shapes.medium,
        ) {
            co.ke.xently.libraries.ui.image.XentlyImage(
                category.image,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
            )
        }
        Text(
            text = category.name,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

private class CategoryPreviewProvider : PreviewParameterProvider<Category> {
    override val values: Sequence<Category>
        get() = sequenceOf(
            Category(name = "Category Name"),
            Category(
                name = "Category Name",
                image = "https://example.com/category1.jpg",
            ),
        )
}

@XentlyPreview
@Composable
private fun CategoryCardPreview(
    @PreviewParameter(CategoryPreviewProvider::class)
    category: Category,
) {
    KwanzaTukuleTheme {
        CategoryCard(
            category = category,
            onClick = {},
        )
    }
}