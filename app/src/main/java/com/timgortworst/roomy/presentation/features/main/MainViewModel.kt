package com.timgortworst.roomy.presentation.features.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.timgortworst.roomy.R
import com.timgortworst.roomy.data.SharedPrefs
import com.timgortworst.roomy.domain.usecase.UserUseCase
import com.timgortworst.roomy.domain.utils.InviteLinkBuilder
import com.timgortworst.roomy.presentation.base.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(
    private val userUseCase: UserUseCase,
    private val sharedPrefs: SharedPrefs
) : ViewModel() {

    fun inviteUser() = liveData {
        val id = userUseCase.getHouseholdIdForUser()
        emit(Event(InviteLinkBuilder.Builder().householdId(id).build()))
    }

    fun showOrHideAd() = liveData { emit(sharedPrefs.isAdsEnabled()) }
}