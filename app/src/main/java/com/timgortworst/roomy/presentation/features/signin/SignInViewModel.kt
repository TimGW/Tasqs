package com.timgortworst.roomy.presentation.features.signin

import androidx.lifecycle.*
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.roomy.R
import com.timgortworst.roomy.domain.model.ResponseState
import com.timgortworst.roomy.domain.model.User
import com.timgortworst.roomy.domain.usecase.SetupUseCase
import com.timgortworst.roomy.domain.usecase.UserUseCase
import com.timgortworst.roomy.presentation.base.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignInViewModel(
    private val setupUseCase: SetupUseCase
) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    fun handleLoginResult(response: IdpResponse) = liveData {
        emit(Event(setupUseCase.handleLoginResult(auth.currentUser, response.isNewUser)))
    }
}