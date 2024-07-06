package co.ke.xently.features.stores.presentation.active.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import co.ke.xently.features.stores.R
import co.ke.xently.features.ui.core.presentation.components.CircularButton
import co.ke.xently.features.ui.core.presentation.components.DropdownMenuWithUpdateAndDelete
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.data.core.Link
import co.ke.xently.libraries.data.image.domain.File
import co.ke.xently.libraries.data.image.domain.UploadResponse
import co.ke.xently.libraries.ui.core.XentlyPreview
import co.ke.xently.libraries.ui.image.XentlyImage


@Composable
internal fun StoreImageListItem(
    image: File,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    onClickUpdate: () -> Unit,
    onClickConfirmDelete: () -> Unit,
) {
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            text = { Text(text = stringResource(R.string.message_confirm_store_image_deletion)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onClickConfirmDelete()
                    },
                ) { Text(text = stringResource(R.string.action_delete)) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(text = stringResource(R.string.action_cancel))
                }
            },
        )
    }

    Card(modifier = modifier, shape = MaterialTheme.shapes.large) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentAlignment = Alignment.TopEnd,
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.matchParentSize(),
            ) {
                XentlyImage(
                    data = image,
                    modifier = Modifier.matchParentSize(),
                )
                if (isLoading) CircularProgressIndicator()
            }

            var expanded by rememberSaveable { mutableStateOf(false) }

            Box {
                CircularButton(
                    onClick = { expanded = true },
                    shape = MaterialTheme.shapes.large.copy(
                        topStart = CornerSize(0.dp),
                        bottomEnd = CornerSize(0.dp),
                    ),
                    content = {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.content_desc_action_more_store_image_options),
                        )
                    },
                )

                DropdownMenuWithUpdateAndDelete(
                    expanded = expanded,
                    onExpandChanged = { expanded = it },
                    onClickUpdate = { onClickUpdate(); expanded = false },
                    onClickDelete = {
                        showDeleteDialog = true
                        expanded = false
                    },
                )
            }
        }
    }
}

private class StoreImageListItemParameterProvider : PreviewParameterProvider<Boolean> {
    override val values: Sequence<Boolean>
        get() = sequenceOf(true, false)
}

@XentlyPreview
@Composable
private fun StoreImageListItemPreview(
    @PreviewParameter(StoreImageListItemParameterProvider::class)
    isLoading: Boolean,
) {
    XentlyTheme {
        val image = remember {
            val link = Link(href = "https://picsum.photos/id/1/300/300")
            UploadResponse(links = mapOf("media" to link))
        }
        StoreImageListItem(
            image = image,
            isLoading = isLoading,
            onClickUpdate = { },
            onClickConfirmDelete = { },
            modifier = Modifier.padding(16.dp),
        )
    }
}