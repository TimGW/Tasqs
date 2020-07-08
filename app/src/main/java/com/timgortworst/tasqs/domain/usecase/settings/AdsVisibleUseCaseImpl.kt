package com.timgortworst.tasqs.domain.usecase.settings

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.timgortworst.tasqs.data.sharedpref.SharedPrefs
import com.timgortworst.tasqs.domain.model.response.Response
import com.timgortworst.tasqs.domain.usecase.None
import com.timgortworst.tasqs.presentation.TasqsApp
import com.timgortworst.tasqs.presentation.usecase.settings.AdsVisibleUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class AdsVisibleUseCaseImpl : AdsVisibleUseCase {
    private val remoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

    override fun execute(params: None) = flow {
        val remoteValue = remoteConfig.getBoolean(TasqsApp.KEY_ENABLE_ADS)
        emit(Response.Success(remoteValue))
    }.flowOn(Dispatchers.Default)
}
