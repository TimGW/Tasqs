package com.timgortworst.roomy.data

import android.content.Context
import androidx.preference.PreferenceManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

class SharedPrefs(context: Context) {
    private val sharedPreferences =  PreferenceManager.getDefaultSharedPreferences(context)

    fun isFirstLaunch(): Boolean {
        return getBoolValue(SHARED_PREF_FIRST_LAUNCH, true)
    }

    fun setFirstLaunch(setFirstLaunch: Boolean) {
        setBoolValue(SHARED_PREF_FIRST_LAUNCH, setFirstLaunch)
    }

    fun isAdsEnabled(): Boolean {
        return getBoolValue(SHARED_PREF_ADS, true)
    }

    fun setAdsEnabled(setAdsEnabled: Boolean) {
        setBoolValue(SHARED_PREF_ADS, setAdsEnabled)
    }

    fun setDarkModeSetting(darkMode : Int) {
        setIntValue(SHARED_PREF_DARK_MODE, darkMode)
    }

    fun getDarkModeSetting(): Int {
        return getIntValue(SHARED_PREF_DARK_MODE)
    }

    fun setHouseholdId(id : String) {
        setStringValue(SHARED_PREF_HOUSEHOLD_ID, id)
    }

    fun getHouseholdId(): String {
        return getStringValue(SHARED_PREF_HOUSEHOLD_ID)
    }

    private fun getStringValue(key: String): String {
        return sharedPreferences.getString(key, "") ?: ""
    }

    private fun setStringValue(key: String, value: String) {
        return sharedPreferences.edit().putString(key, value).apply()
    }

    private fun getIntValue(key: String, default: Int = 0): Int {
        return sharedPreferences.getInt(key, default)
    }

    private fun setIntValue(key: String, value: Int) {
        return sharedPreferences.edit().putInt(key, value).apply()
    }

    private fun getBoolValue(key: String, default: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, default)
    }

    private fun setBoolValue(key: String, value: Boolean) {
        sharedPreferences
            .edit()
            .putBoolean(key, value)
            .apply()
    }

    companion object {
        const val SHARED_PREF_FIRST_LAUNCH = "SHARED_PREF_FIRST_LAUNCH"
        const val SHARED_PREF_HOUSEHOLD_ID = "SHARED_PREF_HOUSEHOLD_ID"
        const val SHARED_PREF_ADS = "SHARED_PREF_ADS"
        const val SHARED_PREF_DARK_MODE = "SHARED_PREF_DARK_THEME"
    }
}
