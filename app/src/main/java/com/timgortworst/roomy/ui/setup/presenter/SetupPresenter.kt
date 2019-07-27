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

    // todo split up in smaller pieces
    fun setupHousehold(referredHouseholdId: String) = scope.launch {
        if (referredHouseholdId.isNotBlank()) {
            if (setupInteractor.userBlackListedForHousehold(referredHouseholdId)) {
                view.presentUserIsBannedDialog()
                return@launch
            }

            // user has accepted the invite
            if (setupInteractor.getHouseholdIdForUser().isNotBlank()) {
                if(isIdSimilarToActiveId(referredHouseholdId)){
                    view.presentAlreadyInHouseholdDialog()
                } else {
                    // caution user that household will be overwritten
                    view.presentHouseholdOverwriteDialog()
                }
            } else {
                // no active household, so update
                changeCurrentUserHousehold(referredHouseholdId)
            }
        } else {
            // user is not invited
            if (setupInteractor.getHouseholdIdForUser().isNotBlank()) {
                // user has an active household
                view.goToMainActivity()
            } else {
                // user is not invited and has no household (new user)
                val householdID = setupInteractor.initializeHousehold()

                if (householdID != null) {
                    // update household id for user remote
                    setupInteractor.switchHousehold(
                        householdId = householdID,
                        role = Role.ADMIN.name
                    )
                    view.goToMainActivity()
                } else {
                    view.presentToastError(R.string.generic_error)
                }
            }
        }
    }

    private suspend fun isIdSimilarToActiveId(referredHouseholdId: String): Boolean {
        return referredHouseholdId == setupInteractor.getHouseholdIdForUser()
    }

    fun changeCurrentUserHousehold(newHouseholdId: String) = scope.launch {
        setupInteractor.switchHousehold(
            householdId = newHouseholdId,
            role = Role.NORMAL.name
        )
        val userList = setupInteractor.getUserListForHousehold(setupInteractor.getHouseholdIdForUser())
        if (userList?.isEmpty() == true) {
            setupInteractor.deleteHousehold(setupInteractor.getHouseholdIdForUser())
        }
        view.goToMainActivity()
    }
}
