package com.timgortworst.roomy.presentation.features.main

import android.net.Uri
import androidx.lifecycle.*
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.timgortworst.roomy.data.sharedpref.SharedPrefs
import com.timgortworst.roomy.domain.usecase.UserUseCase
import com.timgortworst.roomy.domain.utils.InviteLinkBuilder
import com.timgortworst.roomy.presentation.RoomyApp
import com.timgortworst.roomy.presentation.base.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainViewModel(
    private val userUseCase: UserUseCase,
    private val sharedPrefs: SharedPrefs
) : ViewModel() {

    private val _uriEvent = MutableLiveData<Event<Uri>>()
    val uriEvent: LiveData<Event<Uri>> = _uriEvent

    suspend fun inviteUser() = withContext(Dispatchers.IO) {
        val id = userUseCase.getHouseholdIdForUser()
        _uriEvent.postValue(Event(InviteLinkBuilder.Builder().householdId(id).build()))
    }

    fun showOrHideAd() = liveData {
        val remoteValue = FirebaseRemoteConfig.getInstance().getBoolean(RoomyApp.KEY_ENABLE_ADS)
        val localValue = sharedPrefs.isAdsEnabled()
        emit(remoteValue && localValue)
    }
}