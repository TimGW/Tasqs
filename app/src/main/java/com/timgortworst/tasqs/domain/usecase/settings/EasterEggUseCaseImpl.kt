package com.timgortworst.tasqs.domain.usecase.settings

import com.timgortworst.tasqs.R
import com.timgortworst.tasqs.data.sharedpref.SharedPrefs
import com.timgortworst.tasqs.domain.model.response.Response
import com.timgortworst.tasqs.presentation.base.model.EasterEgg
import com.timgortworst.tasqs.presentation.usecase.settings.EasterEggUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class EasterEggUseCaseImpl(
    private val sharedPrefs: SharedPrefs
) : EasterEggUseCase {

    data class Params(internal val count: Int)

    override fun execute(params: Params) = flow {
        if (sharedPrefs.isAdsEnabled()) {
            when {
                betweenUntil(params.count,
                    CLICKS_FOR_MESSAGE,
                    CLICKS_FOR_EASTER_EGG
                ) -> {
                    emit(
                        Response.Success(
                            EasterEgg(
                                R.string.easter_egg_message,
                                (CLICKS_FOR_EASTER_EGG - params.count)
                            )
                        )
                    )
                }
                params.count == CLICKS_FOR_EASTER_EGG -> {
                    sharedPrefs.setAdsEnabled(false)
                    emit(Response.Success(EasterEgg(R.string.easter_egg_enabled)))
                }
                else -> emit(Response.Success(null))
            }
        } else {
            if (params.count == CLICKS_FOR_EASTER_EGG) {
                emit(Response.Success(EasterEgg(R.string.easter_egg_already_enabled)))
            } else {
                emit(Response.Success(null))
            }
        }
    }.flowOn(Dispatchers.Default)

    private fun betweenUntil(comparable: Int, x: Int, y: Int): Boolean = (comparable in x until y)

    companion object {
        private const val CLICKS_FOR_EASTER_EGG: Int = 10
        private const val CLICKS_FOR_MESSAGE: Int = 7
    }
}
