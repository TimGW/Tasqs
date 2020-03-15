package com.timgortworst.roomy.presentation.features.splash

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.auth.FirebaseAuth
import com.timgortworst.roomy.domain.usecase.SetupUseCase
import com.timgortworst.roomy.presentation.base.CoroutineLifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SplashPresenter(
    private val view: SplashView,
    private val setupUseCase: SetupUseCase
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
            auth.currentUser == null ||
                    auth.currentUser?.uid?.isBlank() == true -> view.goToSignInActivity()

            // then check if the user accepted an invite link
            referredHouseholdId.isNotBlank() -> referredSetup(referredHouseholdId)

            // continue to the app
            else -> view.goToMainActivity()
        }
    }

    private fun referredSetup(referredHouseholdId: String) = scope.launch {
        when {
            setupUseCase.isIdSimilarToActiveId(referredHouseholdId) -> {
                view.presentAlreadyInHouseholdDialog()
            }
            setupUseCase.currentHouseholdIdForCurrentUser().isNotBlank() -> {
                view.presentHouseholdOverwriteDialog()
            }
            else -> changeCurrentUserHousehold(referredHouseholdId)
        }
    }

    fun changeCurrentUserHousehold(newId: String) = scope.launch {
        setupUseCase.switchHousehold(newId)

        view.goToMainActivity()
    }
}
