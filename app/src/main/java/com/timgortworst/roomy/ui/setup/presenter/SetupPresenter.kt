package com.timgortworst.roomy.ui.setup.presenter

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.timgortworst.roomy.R
import com.timgortworst.roomy.domain.SetupInteractor
import com.timgortworst.roomy.model.Role
import com.timgortworst.roomy.ui.setup.view.SetupView
import com.timgortworst.roomy.utils.CoroutineLifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class SetupPresenter @Inject constructor(
        private val view: SetupView,
        private val setupInteractor: SetupInteractor
) : DefaultLifecycleObserver {

    private val scope = CoroutineLifecycleScope(Dispatchers.Main)

    init {
        if (view is LifecycleOwner) {
            view.lifecycle.addObserver(scope)
        }
    }

    fun setupHousehold(referredHouseholdId: String) = scope.launch {
        setupInteractor.createUser()

        // user has accepted the invite
        if (referredHouseholdId.isNotBlank()) {
            when {
                setupInteractor.userBlackListedForHousehold(referredHouseholdId) -> {
                    view.presentUserIsBannedDialog()
                }
                isIdSimilarToActiveId(referredHouseholdId) -> {
                    view.presentAlreadyInHouseholdDialog()
                }
                setupInteractor.getHouseholdIdForUser().isNotBlank() -> {
                    view.presentHouseholdOverwriteDialog()
                }
                else -> changeCurrentUserHousehold(referredHouseholdId)
            }
        } else {
            setupInteractor.initializeHousehold()?.let {
                // update household id for user remote
                setupInteractor.switchHousehold(
                        householdId = it,
                        role = Role.ADMIN.name
                )
                view.goToMainActivity()
                return@launch
            }

            view.presentToastError(R.string.generic_error)
        }
    }

    private suspend fun isIdSimilarToActiveId(referredHouseholdId: String): Boolean {
        return referredHouseholdId == setupInteractor.getHouseholdIdForUser()
    }

    fun changeCurrentUserHousehold(newHouseholdId: String) = scope.launch {
        val oldHouseholdId = setupInteractor.getHouseholdIdForUser()

        setupInteractor.switchHousehold(
                householdId = newHouseholdId,
                role = Role.NORMAL.name
        )
        val userList = setupInteractor.getUserListForHousehold(setupInteractor.getHouseholdIdForUser())
        if (userList?.isEmpty() == true) {
            setupInteractor.deleteHousehold(oldHouseholdId)
        }
        view.goToMainActivity()
    }
}
