package com.timgortworst.roomy.presentation.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.timgortworst.roomy.domain.usecase.UserUseCase

class SettingsViewModel(
    private val userUseCase: UserUseCase
) : ViewModel() {

    fun fetchUser() = liveData {
        emit(userUseCase.getCurrentUser())
    }
}