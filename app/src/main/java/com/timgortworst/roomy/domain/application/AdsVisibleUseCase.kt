package com.timgortworst.roomy.domain.application

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.timgortworst.roomy.data.sharedpref.SharedPrefs
import com.timgortworst.roomy.presentation.RoomyApp

class AdsVisibleUseCase(
    private val sharedPrefs: SharedPrefs,
    private val remoteConfig: FirebaseRemoteConfig
) : UseCase<Boolean> {

    override fun invoke(): Boolean {
        val remoteValue = remoteConfig.getBoolean(RoomyApp.KEY_ENABLE_ADS)
        val localValue = sharedPrefs.isAdsEnabled()
        return remoteValue && localValue
    }
}
