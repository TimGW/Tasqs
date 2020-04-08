package com.timgortworst.roomy.presentation.features.signin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.domain.usecase.UseCase
import com.timgortworst.roomy.domain.usecase.account.SignInUseCase
import com.timgortworst.roomy.presentation.base.model.SignInAction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SignInViewModel(
    private val signInUseCase: UseCase<Flow<Response<String>>, SignInUseCase.Params>
) : ViewModel() {

    private val _action = MutableLiveData<SignInAction>()
    val action: LiveData<SignInAction>
        get() = _action


    fun handleLoginResult(response: IdpResponse) {
        viewModelScope.launch {
            val isNewUser = response.isNewUser

            val params = SignInUseCase.Params(isNewUser)
            signInUseCase.execute(params).collect {
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