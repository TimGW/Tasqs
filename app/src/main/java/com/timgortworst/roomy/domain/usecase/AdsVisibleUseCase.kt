package com.timgortworst.roomy.domain.usecase

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.timgortworst.roomy.data.sharedpref.SharedPrefs
import com.timgortworst.roomy.domain.UseCase
import com.timgortworst.roomy.presentation.RoomyApp

class AdsVisibleUseCase(
    private val sharedPrefs: SharedPrefs
) : UseCase<Boolean> {

    override fun executeUseCase(): Boolean {
        val remoteValue = FirebaseRemoteConfig.getInstance().getBoolean(RoomyApp.KEY_ENABLE_ADS)
        val localValue = sharedPrefs.isAdsEnabled()
        return remoteValue && localValue
    }
}
