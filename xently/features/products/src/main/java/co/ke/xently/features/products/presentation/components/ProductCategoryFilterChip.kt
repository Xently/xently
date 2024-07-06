package co.ke.xently.features.products.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import co.ke.xently.features.productcategory.data.domain.ProductCategory
import co.ke.xently.features.products.R
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyThemePreview

@Composable
fun ProductCategoryFilterChip(
    category: ProductCategory,
    modifier: Modifier = Modifier,
    onClickSelectCategory: () -> Unit,
    onClickRemoveCategory: () -> Unit,
) {
    FilterChip(
        modifier = modifier,
        selected = category.selected,
        onClick = onClickSelectCategory,
        label = { Text(text = category.name) },
        trailingIcon = if (!category.selected) null else {
            {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(
                        R.string.content_desc_remove_from_selected_categories,
                        category.name,
                    ),
                    modifier = Modifier
                        .size(InputChipDefaults.AvatarSize)
                        .clickable(onClick = onClickRemoveCategory),
                )
            }
        },
    )
}

private class ProductCategoryFilterChipProvider : PreviewParameterProvider<Boolean> {
    override val values: Sequence<Boolean>
        get() = sequenceOf(true, false)
}

@XentlyThemePreview
@Composable
private fun ProductCategoryFilterChipPreview(
    @PreviewParameter(ProductCategoryFilterChipProvider::class)
    selected: Boolean,
) {
    XentlyTheme {
        ProductCategoryFilterChip(
            category = ProductCategory(
                name = "Electronics",
                selected = selected,
            ),
            onClickSelectCategory = {},
            onClickRemoveCategory = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}