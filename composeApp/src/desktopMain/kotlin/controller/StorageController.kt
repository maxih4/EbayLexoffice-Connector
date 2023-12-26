package controller

import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import java.util.prefs.Preferences

class StorageController {

    val settings: Settings = PreferencesSettings(Preferences.userRoot())

}