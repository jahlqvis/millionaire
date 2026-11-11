package se.yourcompany.miljonaren.data.repository

import android.content.Context
import se.yourcompany.miljonaren.domain.usecase.GameSettingsStore

class GamePreferences(context: Context) : GameSettingsStore {
    private val preferences = context.applicationContext.getSharedPreferences("game_settings", Context.MODE_PRIVATE)

    override var historyLimit: Int
        get() = preferences.getInt("history_limit", 100).coerceIn(1, 1000)
        set(value) {
            require(value in 1..1000)
            preferences.edit().putInt("history_limit", value).apply()
        }

    override var soundEnabled: Boolean
        get() = preferences.getBoolean("sound_enabled", true)
        set(value) { preferences.edit().putBoolean("sound_enabled", value).apply() }
}
