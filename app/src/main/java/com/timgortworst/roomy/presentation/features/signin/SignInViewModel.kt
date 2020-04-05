package com.timgortworst.roomy.presentation.features.signin

import androidx.lifecycle.*
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.timgortworst.roomy.domain.entity.response.Response
import com.timgortworst.roomy.presentation.base.model.SignInAction
import com.timgortworst.roomy.domain.usecase.SignInUseCase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SignInViewModel(
    private val signInUseCase: SignInUseCase
) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private val _action = MutableLiveData<SignInAction>()
    val action: LiveData<SignInAction>
        get() = _action


    fun handleLoginResult(response: IdpResponse) {
        viewModelScope.launch {

            val isNewUser = response.isNewUser
            val token = FirebaseInstanceId.getInstance().instanceId.await().token

            signInUseCase.handleLoginResult(
                auth.currentUser,
                isNewUser,
                token
            ).collect {
                when (it) {
                    Response.Loading -> _action.value = SignInAction.LoadingDialog
                    is Response.Success -> {
                        if (isNewUser) {
                            _action.value = SignInAction.MainActivity
                        } else {
                            _action.value = SignInAction.WelcomeBack(it.data.orEmpty())
                        }
                    }
                    is Response.Error -> _action.value = SignInAction.Failed()
                }
            }
        }
    }
}