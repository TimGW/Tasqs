package com.timgortworst.roomy.domain.usecase.easteregg

import com.timgortworst.roomy.R
import com.timgortworst.roomy.data.sharedpref.SharedPrefs
import com.timgortworst.roomy.domain.usecase.UseCase
import com.timgortworst.roomy.presentation.base.model.EasterEgg
import com.timgortworst.roomy.presentation.usecase.EasterEggUseCase

class EasterEggUseCaseImpl(
    private val sharedPrefs: SharedPrefs
) : EasterEggUseCase {

    data class Params(internal val count: Int)

    override fun execute(params: Params?): EasterEgg? {
        checkNotNull(params)

        if (sharedPrefs.isAdsEnabled()) {
            return when {
                betweenUntil(
                    params.count,
                    CLICKS_FOR_MESSAGE,
                    CLICKS_FOR_EASTER_EGG
                ) -> {
                    EasterEgg(
                        R.string.easter_egg_message,
                        (CLICKS_FOR_EASTER_EGG - params.count)
                    )
                }
                params.count == CLICKS_FOR_EASTER_EGG -> {
                    sharedPrefs.setAdsEnabled(false)
                    EasterEgg(R.string.easter_egg_enabled)
                }
                else -> null
            }
        } else {
            return if (params.count == CLICKS_FOR_EASTER_EGG) {
                EasterEgg(R.string.easter_egg_already_enabled)
            } else {
                null
            }
        }
    }

    private fun betweenUntil(comparable: Int, x: Int, y: Int): Boolean = (comparable in x until y)

    companion object {
        private const val CLICKS_FOR_EASTER_EGG: Int = 10
        private const val CLICKS_FOR_MESSAGE: Int = 7
    }
}
