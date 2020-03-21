package com.timgortworst.roomy.presentation.features.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.timgortworst.roomy.domain.usecase.SetupUseCase
import com.timgortworst.roomy.domain.usecase.UserUseCase
import com.timgortworst.roomy.presentation.base.Event
import kotlinx.coroutines.tasks.await

class SignInViewModel(
    private val setupUseCase: SetupUseCase,
    private val userUseCase: UserUseCase
) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    fun handleLoginResult(response: IdpResponse) = liveData {
        val token = FirebaseInstanceId.getInstance().instanceId.await().token
        userUseCase.addTokenToUser(token)

        emit(Event(setupUseCase.handleLoginResult(auth.currentUser, response.isNewUser)))
    }
}