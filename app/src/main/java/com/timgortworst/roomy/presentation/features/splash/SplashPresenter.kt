package com.timgortworst.roomy.presentation.features.splash

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.auth.FirebaseAuth
import com.timgortworst.roomy.data.SharedPrefs
import com.timgortworst.roomy.domain.model.Role
import com.timgortworst.roomy.domain.usecase.SetupUseCase
import com.timgortworst.roomy.presentation.base.CoroutineLifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SplashPresenter(
        private val view: SplashView,
        private val setupUseCase: SetupUseCase,
        private val sharedPrefs: SharedPrefs
) : DefaultLifecycleObserver {
    private val scope = CoroutineLifecycleScope(Dispatchers.Main)
    private val uId = FirebaseAuth.getInstance().currentUser?.uid

    init {
        if (view is LifecycleOwner) {
            view.lifecycle.addObserver(scope)
        }
    }

    fun initializeUser(referredHouseholdId: String) = scope.launch {
        when {
            sharedPrefs.isFirstLaunch() || uId.isNullOrEmpty() -> view.goToOnboardingActivity()
            referredHouseholdId.isNotBlank() -> referredSetup(referredHouseholdId)
            else -> view.goToMainActivity()
        }
    }

    private fun referredSetup(referredHouseholdId: String) = scope.launch {
        when {
//            setupUseCase.userBlackListedForHousehold(referredHouseholdId) -> {
//                view.presentUserIsBannedDialog()
//            }
//            setupUseCase.isHouseholdFull(referredHouseholdId) -> {
//                view.presentHouseholdFullDialog()
//            }
            setupUseCase.isIdSimilarToActiveId(referredHouseholdId) -> {
                view.presentAlreadyInHouseholdDialog()
            }
            setupUseCase.currentHouseholdIdForCurrentUser().isNotBlank() -> {
                view.presentHouseholdOverwriteDialog()
            }
            else -> changeCurrentUserHousehold(referredHouseholdId)
        }
    }

    fun changeCurrentUserHousehold(newHouseholdId: String) = scope.launch {
        val oldHouseholdId = setupUseCase.currentHouseholdIdForCurrentUser()
        setupUseCase.switchHousehold(
                householdId = newHouseholdId,
                role = Role.NORMAL.name
        )
        setupUseCase.userListForCurrentHousehold()?.let {
            // todo clear old tasks?
            if (it.isEmpty()) setupUseCase.deleteHousehold(oldHouseholdId)
        }

        view.goToMainActivity()
    }
}
