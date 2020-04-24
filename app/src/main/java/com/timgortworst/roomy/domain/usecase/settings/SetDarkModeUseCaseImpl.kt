package com.timgortworst.roomy.domain.usecase.settings

import com.timgortworst.roomy.data.sharedpref.SharedPrefs
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.presentation.usecase.settings.SetDarkModeUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.lang.Exception

class SetDarkModeUseCaseImpl(
    private val sharedPrefs: SharedPrefs
) : SetDarkModeUseCase {

    data class Params(internal val darkModeSetting: Int)

    override fun execute(params: Params?) = flow {
        checkNotNull(params)

        try {
            emit(Response.Success(sharedPrefs.setDarkModeSetting(params.darkModeSetting)))
        } catch (e: Exception) {
            emit(Response.Error())
        }
    }.flowOn(Dispatchers.Default)
}
