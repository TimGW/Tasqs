package com.timgortworst.roomy.presentation.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.timgortworst.roomy.domain.usecase.UserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SettingsViewModel(
    private val userUseCase: UserUseCase
) : ViewModel() {

    fun fetchUser() = liveData {
        emit(userUseCase.getCurrentUser())
    }

    suspend fun deleteFirestoreData() = withContext(Dispatchers.IO) {
        userUseCase.deleteFirestoreData()
    }
}