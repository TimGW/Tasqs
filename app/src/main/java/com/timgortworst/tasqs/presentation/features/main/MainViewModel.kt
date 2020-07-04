package com.timgortworst.tasqs.presentation.features.main

import android.net.Uri
import androidx.lifecycle.*
import com.timgortworst.tasqs.domain.model.response.Response
import com.timgortworst.tasqs.domain.usecase.None
import com.timgortworst.tasqs.presentation.usecase.settings.AdsVisibleUseCase
import com.timgortworst.tasqs.presentation.usecase.user.InviteLinkBuilderUseCase
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
        inviteLinkBuilderUseCase.execute(None).collect { response ->
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

    fun showOrHideAd() = adsVisibleUseCase.execute(None).asLiveData(viewModelScope.coroutineContext)
}