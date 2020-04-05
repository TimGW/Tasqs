package com.timgortworst.roomy.domain.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.timgortworst.roomy.data.repository.UserRepository
import com.timgortworst.roomy.data.sharedpref.SharedPrefs
import com.timgortworst.roomy.domain.UseCase
import com.timgortworst.roomy.domain.entity.response.ErrorHandler
import com.timgortworst.roomy.domain.entity.response.Response
import com.timgortworst.roomy.presentation.RoomyApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class AdsVisibleUseCase(
    private val sharedPrefs: SharedPrefs
) : UseCase<LiveData<Boolean>>() {

    override fun executeUseCase() = liveData {
        val remoteValue = FirebaseRemoteConfig.getInstance().getBoolean(RoomyApp.KEY_ENABLE_ADS)
        val localValue = sharedPrefs.isAdsEnabled()
        emit(remoteValue && localValue)
    }
}
