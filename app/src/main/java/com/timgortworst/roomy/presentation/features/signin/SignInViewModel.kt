package com.timgortworst.roomy.presentation.features.signin

import androidx.lifecycle.*
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.domain.model.ui.SignInAction
import com.timgortworst.roomy.domain.usecase.LoginUseCase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SignInViewModel(
    private val setupUseCase: LoginUseCase
) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private val _action = MutableLiveData<SignInAction>()
    val action: LiveData<SignInAction>
        get() = _action


    fun handleLoginResult(response: IdpResponse) {
        viewModelScope.launch {

            setupUseCase.handleLoginResult(
                auth.currentUser,
                response.isNewUser,
                FirebaseInstanceId.getInstance().instanceId.await().token
            ).collect {
                when (it) {
                    Response.Loading -> _action.value = SignInAction.LoadingDialog
                    is Response.Success -> {
                        if (response.isNewUser) {
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