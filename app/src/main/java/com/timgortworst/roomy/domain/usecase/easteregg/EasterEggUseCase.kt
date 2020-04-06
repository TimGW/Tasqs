package com.timgortworst.roomy.domain.usecase.easteregg

import com.timgortworst.roomy.R
import com.timgortworst.roomy.data.sharedpref.SharedPrefs
import com.timgortworst.roomy.domain.usecase.UseCase
import com.timgortworst.roomy.presentation.base.model.EasterEgg

class EasterEggUseCase(
    private val sharedPrefs: SharedPrefs
) : UseCase<EasterEgg?> {
    private var count : Int? = null

    fun init(count: Int): EasterEggUseCase {
        this.count = count
        return this
    }

    override fun invoke(): EasterEgg? {
        if (count == null) {
            throw IllegalArgumentException("init not called, or called with null argument.")
        }

        if (sharedPrefs.isAdsEnabled()) {
            return when {
                betweenUntil(count!!,
                    CLICKS_FOR_MESSAGE,
                    CLICKS_FOR_EASTER_EGG
                ) -> {
                    EasterEgg(
                        R.string.easter_egg_message,
                        (CLICKS_FOR_EASTER_EGG - count!!)
                    )
                }
                count!! == CLICKS_FOR_EASTER_EGG -> {
                    sharedPrefs.setAdsEnabled(false)
                    EasterEgg(R.string.easter_egg_enabled)
                }
                else -> null
            }
        } else {
            return if (count!! == CLICKS_FOR_EASTER_EGG) {
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
