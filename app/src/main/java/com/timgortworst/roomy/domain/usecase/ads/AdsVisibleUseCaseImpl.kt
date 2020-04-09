package com.timgortworst.roomy.domain.usecase.ads

import androidx.lifecycle.liveData
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.timgortworst.roomy.data.sharedpref.SharedPrefs
import com.timgortworst.roomy.domain.usecase.UseCase
import com.timgortworst.roomy.presentation.RoomyApp
import com.timgortworst.roomy.presentation.usecase.AdsVisibleUseCase

class AdsVisibleUseCaseImpl(
    private val sharedPrefs: SharedPrefs,
    private val remoteConfig: FirebaseRemoteConfig
) : AdsVisibleUseCase {

    override fun execute(params: Unit?) = liveData {
        val remoteValue = remoteConfig.getBoolean(RoomyApp.KEY_ENABLE_ADS)
        val localValue = sharedPrefs.isAdsEnabled()
        emit(remoteValue && localValue)
    }
}
