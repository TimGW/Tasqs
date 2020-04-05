package com.timgortworst.roomy.presentation.features.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.timgortworst.roomy.domain.entity.response.Response
import com.timgortworst.roomy.presentation.base.model.SplashAction
import com.timgortworst.roomy.domain.usecase.SplashUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashViewModel(
    private val splashUseCase: SplashUseCase
) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private val _action = MutableLiveData<SplashAction>()
    val action: LiveData<SplashAction>
        get() = _action

    fun handleAppStartup(referredHouseholdId: String) = viewModelScope.launch {
        when {
            // first check if user has valid authentication
            (auth.currentUser == null ||
                    auth.currentUser?.uid?.isBlank() == true) -> _action.postValue(
                SplashAction.SignInActivity)

            // then check if the user accepted an invite link
            referredHouseholdId.isNotBlank() -> referredSetup(referredHouseholdId)

            // continue to the app
            else -> {
                splashUseCase.fetchHouseholdId() // fetch to update cache
                _action.postValue(SplashAction.MainActivity)
            }
        }
    }

    private suspend fun referredSetup(referredHouseholdId: String) = withContext(Dispatchers.IO) {
        when {
            splashUseCase.isIdSimilarToActiveId(referredHouseholdId) -> {
                _action.postValue(SplashAction.DialogAlreadyInHousehold)
            }
            splashUseCase.fetchHouseholdId().isNotBlank() -> {
                _action.postValue(SplashAction.DialogOverride(referredHouseholdId))
            }
            else -> changeCurrentUserHousehold(referredHouseholdId)
        }
    }

    suspend fun changeCurrentUserHousehold(newId: String)= withContext(Dispatchers.IO) {
        splashUseCase.switchHousehold(newId).collect {
            when (it) {
                Response.Loading -> _action.postValue(SplashAction.DialogLoading)
                is Response.Success -> _action.postValue(SplashAction.MainActivity)
                is Response.Error -> _action.postValue(SplashAction.DialogError())
            }
        }
    }
}
