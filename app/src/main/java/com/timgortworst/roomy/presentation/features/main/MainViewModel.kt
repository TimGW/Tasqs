package com.timgortworst.roomy.presentation.features.main

import android.net.Uri
import androidx.lifecycle.*
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.presentation.usecase.settings.AdsVisibleUseCase
import com.timgortworst.roomy.presentation.usecase.user.InviteLinkBuilderUseCase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainViewModel(
    private val adsVisibleUseCase: AdsVisibleUseCase,
    private val inviteLinkBuilderUseCase: InviteLinkBuilderUseCase
) : ViewModel() {

    private val _uriEvent = MutableLiveData<Response<Uri>>()
    val uriEvent: LiveData<Response<Uri>>
        get() = _uriEvent

    fun inviteUser() = viewModelScope.launch {
        inviteLinkBuilderUseCase.execute().collect { response ->
            when (response) {
                Response.Loading -> _uriEvent.value = Response.Loading
                is Response.Success -> {
                    val uri = response.data ?: run {
                        _uriEvent.value = Response.Error()
                        return@collect
                    }
                    _uriEvent.value = Response.Success(uri)
                }
                is Response.Error -> _uriEvent.value = Response.Error(response.error)
                is Response.Empty -> _uriEvent.value = Response.Empty(response.msg)
            }
        }
    }

    fun showOrHideAd() = adsVisibleUseCase.execute().asLiveData(viewModelScope.coroutineContext)
}