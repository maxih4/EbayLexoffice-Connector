package storage

import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import java.util.prefs.Preferences

class kvstore {

    val settings: Settings = PreferencesSettings(Preferences.userRoot())
}