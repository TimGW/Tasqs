package com.timgortworst.roomy.ui.setup.presenter

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.timgortworst.roomy.R
import com.timgortworst.roomy.model.Role
import com.timgortworst.roomy.repository.HouseholdRepository
import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.ui.setup.view.SetupView
import com.timgortworst.roomy.utils.CoroutineLifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class SetupPresenter @Inject constructor(
    private val view: SetupView,
    private val householdRepository: HouseholdRepository,
    private val userRepository: UserRepository
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
            if (isHouseholdActive()) {
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
            if (isHouseholdActive()) {
                // user has an active household
                view.goToMainActivity()
            } else {
                // user is not invited and has no household (new user)
                val householdID = householdRepository.createNewHousehold()

                if (householdID != null) {
                    // update household id for user remote
                    userRepository.updateUser(
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
        return referredHouseholdId == userRepository.getHouseholdIdForCurrentUser()
    }

    private suspend fun isHouseholdActive(): Boolean {
        return userRepository.getHouseholdIdForCurrentUser().isNotBlank()
    }

    fun changeCurrentUserHousehold(newHouseholdId: String) = scope.launch {
        userRepository.updateUser(
            householdId = newHouseholdId,
            role = Role.NORMAL.name
        )
        val userList = userRepository.getUsersForHouseholdId(userRepository.getHouseholdIdForCurrentUser())
        if (userList.isEmpty()) {
            // remove old household if there are no more users in the household
            // todo remove nested objects
            householdRepository.removeHousehold(userRepository.getHouseholdIdForCurrentUser())
        }
        view.goToMainActivity()
    }
}
