package com.timgortworst.roomy.domain.usecase.settings

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.timgortworst.roomy.data.sharedpref.SharedPrefs
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.presentation.RoomyApp
import com.timgortworst.roomy.presentation.usecase.settings.AdsVisibleUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class AdsVisibleUseCaseImpl(
    private val sharedPrefs: SharedPrefs
) : AdsVisibleUseCase {
    private val remoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

    override fun execute(params: Unit?) = flow {
        val remoteValue = remoteConfig.getBoolean(RoomyApp.KEY_ENABLE_ADS)
        val localValue = sharedPrefs.isAdsEnabled()
        emit(Response.Success(remoteValue && localValue))
    }.flowOn(Dispatchers.Default)
}