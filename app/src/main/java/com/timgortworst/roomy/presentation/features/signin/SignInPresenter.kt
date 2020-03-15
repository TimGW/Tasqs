package com.timgortworst.roomy.presentation.features.signin

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.timgortworst.roomy.domain.model.ResponseState
import com.timgortworst.roomy.domain.usecase.SetupUseCase
import com.timgortworst.roomy.presentation.base.CoroutineLifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignInPresenter(
    private val view: SignInView,
    private val setupUseCase: SetupUseCase
) : DefaultLifecycleObserver {
    private val auth = FirebaseAuth.getInstance()
    private val scope = CoroutineLifecycleScope(Dispatchers.Main)

    init {
        if (view is LifecycleOwner) {
            view.lifecycle.addObserver(scope)
        }
    }

    fun handleLoginResult(response: IdpResponse) = scope.launch {
        when(val result = setupUseCase.handleLoginResult(auth.currentUser, response.isNewUser)) {
            is ResponseState.Success<*> -> {
                if (response.isNewUser) {
                    view.loginSuccessful()
                } else {
                    view.welcomeBack(auth.currentUser?.displayName)
                }
            }
            is ResponseState.Error -> view.loginFailed(result.message)
        }
    }
}
