package com.timgortworst.roomy.presentation.features.main

import android.net.Uri
import androidx.lifecycle.*
import com.timgortworst.roomy.domain.entity.response.Response
import com.timgortworst.roomy.domain.usecase.MainUseCase
import com.timgortworst.roomy.domain.utils.InviteLinkBuilder
import com.timgortworst.roomy.presentation.base.model.Event
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainViewModel(
    private val mainUseCase: MainUseCase
) : ViewModel() {

    private val _uriEvent = MutableLiveData<Event<Uri>>()
    val uriEvent: LiveData<Event<Uri>>
        get() = _uriEvent

    fun inviteUser() = viewModelScope.launch {
        mainUseCase.getCurrentUser().collect {
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

    fun showOrHideAd() = mainUseCase.showOrHideAds()
}