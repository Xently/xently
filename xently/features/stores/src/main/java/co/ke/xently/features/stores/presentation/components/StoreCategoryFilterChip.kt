package co.ke.xently.features.stores.presentation.components

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
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import co.ke.xently.features.storecategory.data.domain.StoreCategory
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyThemePreview

@Composable
internal fun StoreCategoryFilterChip(
    item: StoreCategory,
    modifier: Modifier = Modifier,
    onClickSelectCategory: () -> Unit,
    onClickRemoveCategory: () -> Unit,
) {
    FilterChip(
        modifier = modifier,
        selected = item.selected,
        onClick = onClickSelectCategory,
        label = { Text(text = item.name) },
        trailingIcon = if (!item.selected) null else {
            {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove ${item.name} from selected categories",
                    modifier = Modifier
                        .size(InputChipDefaults.AvatarSize)
                        .clickable(onClick = onClickRemoveCategory),
                )
            }
        },
    )
}

private class StoreCategoryFilterChipProvider : PreviewParameterProvider<Boolean> {
    override val values: Sequence<Boolean>
        get() = sequenceOf(true, false)
}

@XentlyThemePreview
@Composable
private fun StoreCategoryFilterChipPreview(
    @PreviewParameter(StoreCategoryFilterChipProvider::class)
    selected: Boolean,
) {
    XentlyTheme {
        StoreCategoryFilterChip(
            item = StoreCategory(
                name = "Electronics",
                selected = selected,
            ),
            onClickSelectCategory = {},
            onClickRemoveCategory = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}