package com.timgortworst.roomy.presentation.features.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.domain.model.ui.SplashAction
import com.timgortworst.roomy.domain.usecase.HouseholdUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashViewModel(
    private val householdUseCase: HouseholdUseCase
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
            else -> _action.postValue(SplashAction.MainActivity)
        }
    }

    private suspend fun referredSetup(referredHouseholdId: String) = withContext(Dispatchers.IO) {
        when {
            householdUseCase.isIdSimilarToActiveId(referredHouseholdId) -> {
                _action.postValue(SplashAction.DialogAlreadyInHousehold)
            }
            householdUseCase.currentHouseholdIdForCurrentUser().isNotBlank() -> {
                _action.postValue(SplashAction.DialogOverride(referredHouseholdId))
            }
            else -> changeCurrentUserHousehold(referredHouseholdId)
        }
    }

    suspend fun changeCurrentUserHousehold(newId: String)= withContext(Dispatchers.IO) {
        householdUseCase.switchHousehold(newId).collect {
            when (it) {
                Response.Loading -> {} // loading dialog
                is Response.Success -> _action.value = SplashAction.MainActivity
                is Response.Error -> {} // todo something went wrong dialog
            }

        }
    }
}
