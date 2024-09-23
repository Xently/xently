package co.ke.xently.features.stores.presentation.active.components

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import co.ke.xently.features.shops.data.domain.Shop
import co.ke.xently.features.stores.R
import co.ke.xently.features.stores.data.domain.Store
import co.ke.xently.features.ui.core.presentation.components.CircularButton
import co.ke.xently.features.ui.core.presentation.components.PlaceHolderImageThumbnail
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyThemePreview
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material3.placeholder
import com.google.accompanist.placeholder.material3.shimmer


@Composable
internal fun StoreSummaryListItem(isLoading: Boolean, store: Store, onClickEdit: () -> Unit) {
    ListItem(
        headlineContent = {
            Text(
                text = store.shop.name,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .placeholder(visible = isLoading, highlight = PlaceholderHighlight.shimmer())
                    .basicMarquee(),
            )
        },
        supportingContent = {
            var maxLines by rememberSaveable { mutableIntStateOf(1) }
            Text(
                text = store.name,
                fontWeight = FontWeight.Light,
                maxLines = maxLines,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .placeholder(visible = isLoading, highlight = PlaceholderHighlight.shimmer())
                    .clickable(
                        role = Role.Checkbox,
                        indication = ripple(radius = 1_000.dp),
                        interactionSource = remember { MutableInteractionSource() },
                    ) { maxLines = if (maxLines == 1) Int.MAX_VALUE else 1 },
            )
        },
        leadingContent = {
            PlaceHolderImageThumbnail(
                size = 60.dp,
                modifier = Modifier.placeholder(
                    visible = isLoading,
                    highlight = PlaceholderHighlight.shimmer(),
                ),
            ) {
                Icon(Icons.Default.Person, contentDescription = null)
            }
        },
        trailingContent = {
            CircularButton(
                onClick = onClickEdit,
                modifier = Modifier.placeholder(
                    visible = isLoading,
                    highlight = PlaceholderHighlight.shimmer(),
                ),
                content = {
                    Icon(
                        Icons.Outlined.Edit,
                        contentDescription = stringResource(R.string.content_desc_edit_store),
                    )
                },
            )
        }
    )
}

private class StoreSummaryListItemPreviewProvider : PreviewParameterProvider<Boolean> {
    override val values: Sequence<Boolean>
        get() = sequenceOf(false, true)
}

@XentlyThemePreview
@Composable
private fun StoreSummaryListItemPreview(
    @PreviewParameter(StoreSummaryListItemPreviewProvider::class)
    isLoading: Boolean,
) {
    XentlyTheme {
        val store = remember {
            Store(
                name = "Westlands",
                shop = Shop(name = "Ranalo K'Osewe"),
                description = """Short description about the business/hotel will go here.
                |Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
                |
                |Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem.""".trimMargin(),
            )
        }
        StoreSummaryListItem(
            store = store,
            isLoading = isLoading,
            onClickEdit = {},
        )
    }
}