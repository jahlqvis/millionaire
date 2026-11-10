package se.yourcompany.miljonaren.tv.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import se.yourcompany.miljonaren.data.repository.GameHistoryRepositoryFactory
import se.yourcompany.miljonaren.data.repository.GamePreferences

class SettingsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(SettingsViewModel::class.java))
        return SettingsViewModel(GamePreferences(context), GameHistoryRepositoryFactory.create(context)) as T
    }
}
