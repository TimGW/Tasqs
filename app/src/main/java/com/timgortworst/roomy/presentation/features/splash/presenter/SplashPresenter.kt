package com.timgortworst.roomy.presentation.features.splash.presenter

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.timgortworst.roomy.data.SharedPrefs
import com.timgortworst.roomy.domain.model.Role
import com.timgortworst.roomy.domain.usecase.SetupUseCase
import com.timgortworst.roomy.presentation.base.CoroutineLifecycleScope
import com.timgortworst.roomy.presentation.features.splash.view.SplashView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SplashPresenter(
        private val view: SplashView,
        private val setupUseCase: SetupUseCase,
        private val sharedPrefs: SharedPrefs
) : DefaultLifecycleObserver {
    private val scope = CoroutineLifecycleScope(Dispatchers.Main)

    init {
        if (view is LifecycleOwner) {
            view.lifecycle.addObserver(scope)
        }
    }

    fun initializeUser(referredHouseholdId: String) = scope.launch {
        when {
            sharedPrefs.isFirstLaunch() -> view.goToOnboardingActivity()
            referredHouseholdId.isNotBlank() -> referredSetup(referredHouseholdId)
            else -> view.goToMainActivity()
        }
    }

    private fun referredSetup(referredHouseholdId: String) = scope.launch {
        when {
            // todo create seperate referred activity
//            setupUseCase.userBlackListedForHousehold(referredHouseholdId) -> {
//                view.presentUserIsBannedDialog()
//            }
//            setupUseCase.isHouseholdFull(referredHouseholdId) -> {
//                view.presentHouseholdFullDialog()
//            }
            setupUseCase.isIdSimilarToActiveId(referredHouseholdId) -> {
                view.presentAlreadyInHouseholdDialog()
            }
            setupUseCase.getHouseholdIdForUser().isNotBlank() -> {
                view.presentHouseholdOverwriteDialog()
            }
            else -> changeCurrentUserHousehold(referredHouseholdId)
        }
    }

    fun changeCurrentUserHousehold(newHouseholdId: String) = scope.launch {
        val oldHouseholdId = setupUseCase.getHouseholdIdForUser()

        setupUseCase.switchHousehold(
                householdId = newHouseholdId,
                role = Role.NORMAL.name
        )
        val userList = setupUseCase.getUserListForHousehold(setupUseCase.getHouseholdIdForUser())
        if (userList?.isEmpty() == true) {
            setupUseCase.deleteHousehold(oldHouseholdId)
        }
        view.goToMainActivity()
    }
}
