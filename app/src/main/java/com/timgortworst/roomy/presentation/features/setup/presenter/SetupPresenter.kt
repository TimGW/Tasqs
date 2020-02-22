package com.timgortworst.roomy.presentation.features.setup.presenter

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.timgortworst.roomy.R
import com.timgortworst.roomy.domain.model.Role
import com.timgortworst.roomy.domain.usecase.SetupUseCase
import com.timgortworst.roomy.presentation.base.CoroutineLifecycleScope
import com.timgortworst.roomy.presentation.features.setup.view.SetupView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent

class SetupPresenter(
        private val view: SetupView,
        private val setupUseCase: SetupUseCase
) : DefaultLifecycleObserver, KoinComponent {

    private val scope = CoroutineLifecycleScope(Dispatchers.Main)

    init {
        if (view is LifecycleOwner) {
            view.lifecycle.addObserver(scope)
        }
    }

    fun setupHousehold(referredHouseholdId: String) = scope.launch {
        setupUseCase.createUser()

        // user has accepted the invite
        if (referredHouseholdId.isNotBlank()) {
            when {
                setupUseCase.userBlackListedForHousehold(referredHouseholdId) -> {
                    view.presentUserIsBannedDialog()
                }
                setupUseCase.isHouseholdFull(referredHouseholdId) -> {
                    view.presentHouseholdFullDialog()
                }
                setupUseCase.isIdSimilarToActiveId(referredHouseholdId) -> {
                    view.presentAlreadyInHouseholdDialog()
                }
                setupUseCase.getHouseholdIdForUser().isNotBlank() -> {
                    view.presentHouseholdOverwriteDialog()
                }
                else -> changeCurrentUserHousehold(referredHouseholdId)
            }
        } else {
            setupUseCase.initializeHousehold()?.let {
                // update household id for user remote
                setupUseCase.switchHousehold(
                        householdId = it,
                        role = Role.ADMIN.name
                )
                view.goToMainActivity()
                return@launch
            }

            view.presentToastError(R.string.error_generic)
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
