package com.timgortworst.roomy.local

import android.content.SharedPreferences
import com.timgortworst.roomy.utils.Constants.SHARED_PREF_DARK_MODE
import com.timgortworst.roomy.utils.Constants.SHARED_PREF_FIRST_LAUNCH
import com.timgortworst.roomy.utils.Constants.SHARED_PREF_HOUSEHOLD_ID
import com.timgortworst.roomy.utils.Constants.SHARED_PREF_USER_ID

/**
 * Created by tim.gortworst on 07/03/2018.
 */
class HuishoudGenootSharedPref
constructor(private val sharedPreferences: SharedPreferences) {

    fun isFirstLaunch(): Boolean {
        return getBoolValue(SHARED_PREF_FIRST_LAUNCH, true)
    }

    fun setFirstLaunch(setFirstLaunch: Boolean) {
        setBoolValue(SHARED_PREF_FIRST_LAUNCH, setFirstLaunch)
    }

    fun getActiveHouseholdId(): String {
        return sharedPreferences.getString(SHARED_PREF_HOUSEHOLD_ID, "").orEmpty()
    }

    fun setActiveHouseholdId(householdId: String) {
        sharedPreferences.edit().putString(SHARED_PREF_HOUSEHOLD_ID, householdId).apply()
    }

    fun setDisplayModeDark(darkMode : Boolean) {
        setBoolValue(SHARED_PREF_DARK_MODE, darkMode)
    }

    fun isDisplayModeDark(): Boolean {
        return getBoolValue(SHARED_PREF_DARK_MODE)
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
}
