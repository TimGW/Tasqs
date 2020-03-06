package com.timgortworst.roomy.presentation.features.settings

import com.timgortworst.roomy.R
import com.timgortworst.roomy.data.SharedPrefs

class SettingsPresenter(
        private val view: SettingsView,
        private val sharedPrefs: SharedPrefs
) {

    fun onAppVersionClick(counter: Int) {
        if (sharedPrefs.isAdsEnabled()) {
            when {
                betweenUntil(counter, CLICKS_FOR_MESSAGE, CLICKS_FOR_EASTER_EGG) -> {
                    view.toasti(R.string.easter_egg_message, CLICKS_FOR_EASTER_EGG - counter)
                }
                counter == CLICKS_FOR_EASTER_EGG -> {
                    sharedPrefs.setAdsEnabled(false)
                    view.toasti(R.string.easter_egg_enabled)
                }
                else -> { /* do nothing */
                }
            }
        }
    }

    private fun betweenUntil(comparable: Int, x: Int, y: Int): Boolean = (comparable in x until y)

    companion object {
        private const val CLICKS_FOR_EASTER_EGG: Int = 10
        private const val CLICKS_FOR_MESSAGE: Int = 7
    }
}