package com.timgortworst.roomy.presentation.features.splash

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.auth.FirebaseAuth
import com.timgortworst.roomy.domain.usecase.HouseholdUseCase
import com.timgortworst.roomy.presentation.base.CoroutineLifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SplashPresenter(
    private val view: SplashView,
    private val householdUseCase: HouseholdUseCase
) : DefaultLifecycleObserver {
    private val scope = CoroutineLifecycleScope(Dispatchers.Main)
    private val auth = FirebaseAuth.getInstance()

    init {
        if (view is LifecycleOwner) {
            view.lifecycle.addObserver(scope)
        }
    }

    fun handleAppStartup(referredHouseholdId: String) = scope.launch {
        when {
            // first check if user has valid authentication
            (auth.currentUser == null ||
                    auth.currentUser?.uid?.isBlank() == true) -> view.goToSignInActivity()

            // then check if the user accepted an invite link
            referredHouseholdId.isNotBlank() -> referredSetup(referredHouseholdId)

            // continue to the app
            else -> view.goToMainActivity()
        }
    }

    private fun referredSetup(referredHouseholdId: String) = scope.launch {
        when {
            householdUseCase.isIdSimilarToActiveId(referredHouseholdId) -> {
                view.presentAlreadyInHouseholdDialog()
            }
            householdUseCase.currentHouseholdIdForCurrentUser().isNotBlank() -> {
                view.presentHouseholdOverwriteDialog(referredHouseholdId)
            }
            else -> changeCurrentUserHousehold(referredHouseholdId)
        }
    }

    fun changeCurrentUserHousehold(newId: String) = scope.launch {
        householdUseCase.switchHousehold(newId)

        view.goToMainActivity()
    }
}
