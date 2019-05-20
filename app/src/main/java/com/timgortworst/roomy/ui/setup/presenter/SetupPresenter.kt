package com.timgortworst.roomy.ui.setup.presenter

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import com.timgortworst.roomy.R
import com.timgortworst.roomy.local.HuishoudGenootSharedPref
import com.timgortworst.roomy.model.Role
import com.timgortworst.roomy.repository.HouseholdRepository
import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.ui.setup.view.SetupView
import com.timgortworst.roomy.utils.CoroutineLifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SetupPresenter(
    private val view: SetupView,
    private val householdRepository: HouseholdRepository,
    private val userRepository: UserRepository,
    private val sharedPref: HuishoudGenootSharedPref
) : DefaultLifecycleObserver {

    private val scope = CoroutineLifecycleScope(Dispatchers.Main)

    init {
        if (view is LifecycleOwner) {
            view.lifecycle.addObserver(scope)
        }
    }

    fun setupHousehold(referredHouseholdId: String) = scope.launch {
        if (referredHouseholdId.isNotBlank()) {
            // user has accepted the invite
            if (userHasActiveHousehold()) {
                // caution user that household will be overwritten
                view.presentHouseholdOverwriteDialog()
            } else {
                // no active household, so update
                changeCurrentUserHousehold(referredHouseholdId)
            }
        } else {
            // user is not invited
            if (userHasActiveHousehold()) {
                // user has an active household
                view.goToMainActivity()
            } else {
                // user is not invited and has no household (new user)
                val householdID = householdRepository.createNewHousehold()

                if (householdID != null) {
                    // update household id for user remote
                    userRepository.setOrUpdateUser(
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

    private suspend fun userHasActiveHousehold(): Boolean {
        // check locally
        if (sharedPref.getActiveHouseholdId().isNotBlank()) {
            return true
        }
        // check remote fallback when local is blank
        val user = userRepository.getOrCreateUser()
        // update household id locally
        user?.let { sharedPref.setActiveHouseholdId(it.householdId) }
        return user?.householdId?.isNotBlank() == true
    }

    fun changeCurrentUserHousehold(newHouseholdId: String) = scope.launch {
        userRepository.setOrUpdateUser(
            householdId = newHouseholdId,
            role = Role.NORMAL.name
        )
        val userList = userRepository.getUsersForHouseholdId(sharedPref.getActiveHouseholdId())
        if (userList.isEmpty()) {
            // remove old household if there are no more users in the household
            householdRepository.removeHousehold(sharedPref.getActiveHouseholdId())
        }
        // safe to update local household id
        sharedPref.setActiveHouseholdId(newHouseholdId)
        view.goToMainActivity()
    }
}
