package com.timgortworst.roomy.presentation.features.main

import android.net.Uri
import androidx.lifecycle.*
import com.timgortworst.roomy.domain.model.User
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.domain.usecase.UseCase
import com.timgortworst.roomy.domain.usecase.ads.AdsVisibleUseCase
import com.timgortworst.roomy.domain.usecase.user.GetUserUseCase
import com.timgortworst.roomy.domain.utils.InviteLinkBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainViewModel(
    private val adsVisibleUseCase: UseCase<Boolean, Unit>,
    private val getUserUseCase: UseCase<Flow<Response<User>>, GetUserUseCase.Params>
) : ViewModel() {

    private val _uriEvent = MutableLiveData<Response<Uri>>()
    val uriEvent: LiveData<Response<Uri>>
        get() = _uriEvent

    fun inviteUser() = viewModelScope.launch {
        //todo combine loading of getUser and build uri
        getUserUseCase.execute().collect { response ->
            when (response) {
                Response.Loading -> _uriEvent.value = Response.Loading
                is Response.Success -> {
                    val id = response.data?.householdId ?: return@collect
                    _uriEvent.value = Response.Success(InviteLinkBuilder.Builder().householdId(id).build())
                }
                is Response.Error -> _uriEvent.value = Response.Error(response.error)
                is Response.Empty -> _uriEvent.value = Response.Empty(response.msg)
            }
        }
    }

    fun showOrHideAd()= liveData { emit(adsVisibleUseCase.execute()) }
}