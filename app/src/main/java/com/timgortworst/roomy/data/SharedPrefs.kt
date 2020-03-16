package com.timgortworst.roomy.data

import android.content.Context
import androidx.preference.PreferenceManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

@Suppress("SameParameterValue")
class SharedPrefs(context: Context) {
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun isAdsEnabled() = getBoolValue(SHARED_PREF_ADS, true)
    fun setAdsEnabled(setAdsEnabled: Boolean) { setBoolValue(SHARED_PREF_ADS, setAdsEnabled) }

    fun setDarkModeSetting(darkMode: Int) { setIntValue(SHARED_PREF_DARK_MODE, darkMode) }
    fun getDarkModeSetting()= getIntValue(SHARED_PREF_DARK_MODE)

    private fun getStringValue(key: String) = sharedPreferences.getString(key, "") ?: ""
    private fun setStringValue(key: String, value: String) =
        sharedPreferences.edit().putString(key, value).apply()

    private fun getIntValue(key: String, default: Int = 0) = sharedPreferences.getInt(key, default)
    private fun setIntValue(key: String, value: Int) =
        sharedPreferences.edit().putInt(key, value).apply()

    private fun getBoolValue(key: String, default: Boolean = false) =
        sharedPreferences.getBoolean(key, default)
    private fun setBoolValue(key: String, value: Boolean) =
        sharedPreferences.edit().putBoolean(key, value).apply()

    companion object {
        const val SHARED_PREF_ADS = "SHARED_PREF_ADS"
        const val SHARED_PREF_DARK_MODE = "SHARED_PREF_DARK_THEME"
    }
}
