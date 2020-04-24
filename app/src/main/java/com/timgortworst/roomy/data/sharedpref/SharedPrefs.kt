package com.timgortworst.roomy.data.sharedpref

class SharedPrefs(private val spm: SharedPrefManager) {

    fun isAdsEnabled() = spm.getBoolValue(SHARED_PREF_ADS, true)
    fun setAdsEnabled(setAdsEnabled: Boolean) { spm.setBoolValue(SHARED_PREF_ADS, setAdsEnabled) }

    fun setDarkModeSetting(darkMode: Int) { spm.setIntValue(SHARED_PREF_DARK_MODE, darkMode) }
    fun getDarkModeSetting() = spm.getIntValue(SHARED_PREF_DARK_MODE)

    companion object {
        const val SHARED_PREF_ADS = "SHARED_PREF_ADS"
        const val SHARED_PREF_DARK_MODE = "SHARED_PREF_DARK_THEME"
    }
}
