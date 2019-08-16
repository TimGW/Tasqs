package com.timgortworst.roomy.presentation.features.splash.presenter

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.roomy.domain.usecase.SetupUseCase
import com.timgortworst.roomy.presentation.base.CoroutineLifecycleScope
import com.timgortworst.roomy.presentation.features.splash.view.SplashView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class SplashPresenter @Inject constructor(
        private val view: SplashView,
        private val setupUseCase: SetupUseCase
) : DefaultLifecycleObserver {

    private val scope = CoroutineLifecycleScope(Dispatchers.Main)

    init {
        if (view is LifecycleOwner) {
            view.lifecycle.addObserver(scope)
        }
    }

    fun initializeUser(referredHouseholdId: String = "") = scope.launch {
        // Google sign in
        if (FirebaseAuth.getInstance().currentUser == null) {
            view.goToGoogleSignInActivity()
            return@launch
        }

        // continue to main activity if possible
        try {
            if (setupUseCase.getHouseholdIdForUser().isNotBlank()) {
                view.goToMainActivity()
                return@launch
            }
        } catch (fireStoreEx: FirebaseFirestoreException) { }

        // setup new or referred user
        if (referredHouseholdId.isNotBlank()) {
            view.goToSetupActivityReferred(referredHouseholdId)
        } else {
            view.goToSetupActivity()
        }
    }
}
