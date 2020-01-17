package com.timgortworst.roomy.presentation.features.settings.presenter

import com.timgortworst.roomy.R
import com.timgortworst.roomy.data.SharedPrefs
import com.timgortworst.roomy.domain.utils.betweenUntil
import com.timgortworst.roomy.presentation.features.settings.view.SettingsView
import javax.inject.Inject

class SettingsPresenter @Inject constructor(
        private val view: SettingsView,
        private val sharedPrefs: SharedPrefs
) {

    fun onAppVersionClick(counter: Int) = when {
        counter.betweenUntil(CLICKS_FOR_MESSAGE, CLICKS_FOR_EASTER_EGG) -> {
            view.toasti(R.string.easter_egg_message, CLICKS_FOR_EASTER_EGG - counter)
        }
        counter == CLICKS_FOR_EASTER_EGG -> {
            sharedPrefs.setAdsEnabled(false)
            view.toasti(R.string.easter_egg_enabled)
        }
        else -> { }
    }

    companion object {
        private const val CLICKS_FOR_EASTER_EGG: Int = 10
        private const val CLICKS_FOR_MESSAGE: Int = 7
    }
}
