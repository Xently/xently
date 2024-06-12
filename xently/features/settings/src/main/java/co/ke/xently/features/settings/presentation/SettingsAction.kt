package co.ke.xently.features.settings.presentation

import co.ke.xently.libraries.ui.core.components.ThemeSetting

sealed interface SettingsAction {
    class ChangeThemeSetting(val selectedThemeSetting: ThemeSetting) : SettingsAction
}
