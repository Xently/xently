package co.ke.xently.features.settings.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.ke.xently.features.settings.R
import co.ke.xently.features.settings.domain.Setting
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyPreview
import co.ke.xently.libraries.ui.core.components.NavigateBackIconButton
import co.ke.xently.libraries.ui.core.components.ThemeSetting
import co.ke.xently.libraries.ui.core.components.isDarkState
import co.ke.xently.libraries.ui.core.openUrl


@Composable
fun SettingsScreen(onClickBack: () -> Unit) {
    val viewModel = hiltViewModel<SettingsViewModel>()
    val currentThemeSetting by viewModel.currentThemeSetting.collectAsStateWithLifecycle()

    SettingsScreen(
        themeSetting = currentThemeSetting,
        onClickBack = onClickBack,
        onAction = viewModel::onAction,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsScreen(
    themeSetting: ThemeSetting,
    onClickBack: () -> Unit,
    onAction: (SettingsAction) -> Unit,
) {
    @Suppress("UNUSED_VARIABLE") val isDark by themeSetting.isDarkState()
    var showThemeSettingChangeDialog by rememberSaveable { mutableStateOf(false) }

    if (showThemeSettingChangeDialog) {
        var selectedThemeSetting by rememberSaveable(themeSetting) {
            mutableStateOf(themeSetting)
        }
        AlertDialog(
            onDismissRequest = { showThemeSettingChangeDialog = false },
            title = { Text(text = stringResource(R.string.action_change_theme)) },
            confirmButton = {
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onAction(SettingsAction.ChangeThemeSetting(selectedThemeSetting))
                        showThemeSettingChangeDialog = false
                    },
                ) { Text(text = stringResource(R.string.action_confirm)) }
            },
            text = {
                Column {
                    ThemeSetting.entries.forEachIndexed { index, themeSetting ->
                        ListItem(
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                            leadingContent = {
                                RadioButton(
                                    selected = themeSetting == selectedThemeSetting,
                                    onClick = null,
                                )
                            },
                            headlineContent = {
                                Text(text = stringResource(themeSetting.label))
                            },
                            modifier = Modifier.clickable { selectedThemeSetting = themeSetting },
                        )
                        if (index + 1 < ThemeSetting.entries.size) {
                            HorizontalDivider()
                        }
                    }
                }
            },
        )
    }

    val termsOfService = "https://xently.co.ke"
    val privacyPolicy = "https://xently.co.ke"
    val context = LocalContext.current
    val settings = listOf(
        Setting(
            title = stringResource(R.string.action_label_privacy_policy),
            subtitle = stringResource(R.string.action_read_more),
            onClick = { context.openUrl(privacyPolicy) },
        ),
        Setting(
            title = stringResource(R.string.action_label_terms_of_service),
            subtitle = stringResource(R.string.action_read_more),
            onClick = { context.openUrl(termsOfService) },
        ),
        Setting(
            title = stringResource(R.string.action_contact_us),
            subtitle = "info@xently.co.ke",
        ),
        Setting(
            title = stringResource(R.string.action_change_theme),
            subtitle = stringResource(themeSetting.label),
            onClick = {
                showThemeSettingChangeDialog = true
            },
        )
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    NavigateBackIconButton(onClick = onClickBack)
                },
                title = {
                    Text(
                        fontWeight = FontWeight.Bold,
                        text = stringResource(R.string.settings),
                    )
                },
            )
        },
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            ) {
                items(settings) { setting ->
                    ListItem(
                        modifier = Modifier.clickable(onClick = setting.onClick),
                        headlineContent = {
                            Text(
                                text = setting.title,
                                fontWeight = FontWeight.Light,
                            )
                        },
                        supportingContent = setting.subtitle?.let {
                            {
                                Text(text = it)
                            }
                        },
                    )
                    HorizontalDivider()
                }
            }
        },
    )
}

@XentlyPreview
@Composable
private fun SettingsScreenPreview() {
    XentlyTheme {
        SettingsScreen(
            themeSetting = ThemeSetting.entries.random(),
            onClickBack = {},
            onAction = {},
        )
    }
}