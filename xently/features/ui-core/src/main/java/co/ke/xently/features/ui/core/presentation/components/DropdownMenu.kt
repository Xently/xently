package co.ke.xently.features.ui.core.presentation.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import co.ke.xently.features.ui.core.R
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyPreview
import co.ke.xently.libraries.ui.core.openUrl


@Composable
fun DropdownMenuWithLegalRequirements(
    expanded: Boolean,
    onExpandChanged: (Boolean) -> Unit,
    preLegalRequirements: @Composable ColumnScope.() -> Unit = {},
    postLegalRequirements: @Composable ColumnScope.() -> Unit = {},
) {
    val context = LocalContext.current
    DropdownMenu(expanded = expanded, onDismissRequest = { onExpandChanged(false) }) {
        preLegalRequirements()

        DropdownMenuItem(
            onClick = {
                context.openUrl("https://xently.co.ke/privacy.html")
                onExpandChanged(false)
            },
            text = { Text(text = stringResource(R.string.action_privacy_policy)) },
            leadingIcon = {
                Icon(
                    Icons.Default.Policy,
                    contentDescription = stringResource(R.string.action_privacy_policy),
                )
            },
        )

        DropdownMenuItem(
            onClick = {
                context.openUrl("https://xently.co.ke/termsandconditions.html")
                onExpandChanged(false)
            },
            text = {
                Text(text = stringResource(R.string.action_term_of_services))
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Balance,
                    contentDescription = stringResource(R.string.action_term_of_services),
                )
            },
        )

        postLegalRequirements()
    }
}


@Composable
fun DropdownMenuWithUpdateAndDelete(
    expanded: Boolean,
    onExpandChanged: (Boolean) -> Unit,
    onClickUpdate: (() -> Unit)?,
    onClickDelete: (() -> Unit)?,
) {
    val rememberOnUpdateClick by rememberUpdatedState(onClickUpdate)
    val rememberOnDeleteClick by rememberUpdatedState(onClickDelete)

    val expand by remember(rememberOnUpdateClick, rememberOnDeleteClick, expanded) {
        derivedStateOf {
            expanded
                    && (rememberOnUpdateClick != null || rememberOnDeleteClick != null)
        }
    }
    DropdownMenu(expanded = expand, onDismissRequest = { onExpandChanged(false) }) {
        rememberOnUpdateClick?.let {
            DropdownMenuItem(
                onClick = it,
                text = {
                    Text(text = stringResource(R.string.action_update))
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = stringResource(R.string.action_update),
                    )
                },
            )
        }

        rememberOnDeleteClick?.let {
            DropdownMenuItem(
                onClick = it,
                text = {
                    Text(text = stringResource(R.string.action_delete))
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = stringResource(R.string.action_delete),
                    )
                },
            )
        }
    }
}

@XentlyPreview
@Composable
private fun DropdownMenuWithLegalRequirementsPreview() {
    XentlyTheme {
        DropdownMenuWithLegalRequirements(
            expanded = true,
            onExpandChanged = {},
        )
    }
}

@XentlyPreview
@Composable
private fun DropdownMenuWithUpdateAndDeletePreview() {
    XentlyTheme {
        DropdownMenuWithUpdateAndDelete(
            expanded = true,
            onExpandChanged = {},
            onClickUpdate = {},
            onClickDelete = {},
        )
    }
}