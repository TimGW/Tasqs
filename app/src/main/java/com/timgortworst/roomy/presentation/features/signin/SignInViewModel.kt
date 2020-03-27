package com.timgortworst.roomy.presentation.features.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.timgortworst.roomy.domain.usecase.LoginUseCase
import com.timgortworst.roomy.presentation.base.Event
import kotlinx.coroutines.tasks.await

class SignInViewModel(
    private val setupUseCase: LoginUseCase
) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    fun handleLoginResult(response: IdpResponse) = liveData {
        emit(
            Event(
                setupUseCase.handleLoginResult(
                    auth.currentUser,
                    response.isNewUser,
                    FirebaseInstanceId.getInstance().instanceId.await().token
                )
            )
        )
    }
}