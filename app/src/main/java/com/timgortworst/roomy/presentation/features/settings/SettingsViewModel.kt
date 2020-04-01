package com.timgortworst.roomy.presentation.features.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.timgortworst.roomy.R
import com.timgortworst.roomy.data.sharedpref.SharedPrefs
import com.timgortworst.roomy.domain.model.ui.EasterEgg
import com.timgortworst.roomy.domain.usecase.UserUseCase

class SettingsViewModel(
    private val userUseCase: UserUseCase,
    private val sharedPrefs: SharedPrefs
) : ViewModel() {

    private val _easterEgg = MutableLiveData<EasterEgg>()
    val easterEgg: LiveData<EasterEgg>
        get() = _easterEgg

    fun fetchUser() = liveData {
        emit(userUseCase.getCurrentUser())
    }

    fun onAppVersionClick(counter: Int) {
        if (sharedPrefs.isAdsEnabled()) {
            when {
                betweenUntil(counter, CLICKS_FOR_MESSAGE, CLICKS_FOR_EASTER_EGG) -> {
                    _easterEgg.value =
                        EasterEgg(
                            R.string.easter_egg_message,
                            (CLICKS_FOR_EASTER_EGG - counter)
                        )
                }
                counter == CLICKS_FOR_EASTER_EGG -> {
                    sharedPrefs.setAdsEnabled(false)
                    _easterEgg.value =
                        EasterEgg(R.string.easter_egg_enabled)
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