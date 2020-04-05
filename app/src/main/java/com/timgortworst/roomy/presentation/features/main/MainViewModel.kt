package com.timgortworst.roomy.presentation.features.main

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timgortworst.roomy.domain.entity.response.Response
import com.timgortworst.roomy.domain.usecase.AdsVisibleUseCase
import com.timgortworst.roomy.domain.usecase.GetUserUseCase
import com.timgortworst.roomy.domain.utils.InviteLinkBuilder
import com.timgortworst.roomy.presentation.base.model.Event
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainViewModel(
    private val adsVisibleUseCase: AdsVisibleUseCase,
    private val getUserUseCase: GetUserUseCase
) : ViewModel() {

    private val _uriEvent = MutableLiveData<Event<Uri>>()
    val uriEvent: LiveData<Event<Uri>>
        get() = _uriEvent

    fun inviteUser() = viewModelScope.launch {
        getUserUseCase.executeUseCase().collect {
            when (it) {
                Response.Loading -> {} //TODO()
                is Response.Success -> {
                    val id = it.data?.householdId ?: return@collect
                    _uriEvent.postValue(
                        Event(
                            InviteLinkBuilder.Builder().householdId(id).build()
                        )
                    )
                }
                is Response.Error -> {} //TODO()
                is Response.Empty -> {} //TODO()
            }
        }
    }

    fun showOrHideAd() = adsVisibleUseCase.executeUseCase()
}