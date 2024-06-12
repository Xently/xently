package co.ke.xently.features.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.ke.xently.features.settings.data.SettingKeys
import co.ke.xently.libraries.ui.core.components.ThemeSetting
import com.russhwolf.settings.Settings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(private val settings: Settings) : ViewModel() {
    private fun Settings.getCurrentThemeSetting(): ThemeSetting {
        return getString(
            key = SettingKeys.THEME_SETTING,
            defaultValue = ThemeSetting.SystemDefault.name,
        ).let { ThemeSetting.valueOf(it) }
    }

    private val _currentThemeSetting = MutableStateFlow(settings.getCurrentThemeSetting())
    val currentThemeSetting = _currentThemeSetting.asStateFlow()

    fun onAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.ChangeThemeSetting -> {
                _currentThemeSetting.update { action.selectedThemeSetting }
                viewModelScope.launch(Dispatchers.IO) {
                    settings.putString(SettingKeys.THEME_SETTING, action.selectedThemeSetting.name)
                }
            }
        }
    }
}