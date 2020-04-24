package com.timgortworst.roomy.domain.usecase.settings

import com.timgortworst.roomy.data.sharedpref.SharedPrefs
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.presentation.usecase.settings.GetDarkModeUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class GetDarkModeUseCaseImpl(
    private val sharedPrefs: SharedPrefs
) : GetDarkModeUseCase {

    override fun execute(params: Unit?) = flow {
        checkNotNull(params)


        emit(Response.Success(1))

    }.flowOn(Dispatchers.Default)
}
