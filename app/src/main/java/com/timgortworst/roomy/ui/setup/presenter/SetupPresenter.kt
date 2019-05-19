package com.timgortworst.roomy.ui.setup.presenter

import com.timgortworst.roomy.R
import com.timgortworst.roomy.local.HuishoudGenootSharedPref
import com.timgortworst.roomy.model.AuthenticationResult
import com.timgortworst.roomy.repository.HouseholdRepository
import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.ui.setup.view.SetupView


class SetupPresenter(
    private val view: SetupView,
    private val householdRepository: HouseholdRepository,
    private val userRepository: UserRepository,
    private val sharedPref: HuishoudGenootSharedPref
) {

    fun setupHousehold(referredHouseholdId: String) {

        if (referredHouseholdId.isNotBlank()) {

            // user has accepted the invite
            if (userHasActiveHousehold()) {

                // caution user that household will be overwritten
                view.presentHouseholdOverwriteDialog()
            } else {

                // no active household, so update
                changeUserHousehold(referredHouseholdId)
            }
        } else {

            // user started the application his/her self
            if (userHasActiveHousehold()) {

                // user has an active household
                view.goToMainActivity()
            } else {

                // user is not invited and has no household (new user)
                householdRepository.createNewHousehold(
                    onComplete = { householdID ->

                        // update local household id
                        sharedPref.setActiveHouseholdId(householdID)

                        // update household id for user remote
                        userRepository.updateUser(
                            householdId = householdID,
                            role = AuthenticationResult.Role.ADMIN.name
                        ) {
                            view.goToMainActivity()
                        }
                    },
                    onFailure = {
                        view.presentToastError(R.string.generic_error)
                    })
            }
        }
    }

    private fun userHasActiveHousehold(): Boolean {
        if (sharedPref.getActiveHouseholdId().isNotBlank()) {
            return true
        }

        var isHouseholdActive = false
        userRepository.getUser { user ->
            if (user?.householdId?.isNotBlank() == true) {
                sharedPref.setActiveHouseholdId(user.householdId)
                isHouseholdActive = true
            }
        }
        return isHouseholdActive
    }

    fun changeUserHousehold(referredHouseholdId: String) {
        userRepository.updateUser(
            householdId = referredHouseholdId,
            role = AuthenticationResult.Role.NORMAL.name,
            onComplete = {

                // user is updated
                userRepository.getUsersForHouseholdId(sharedPref.getActiveHouseholdId()) { userList ->

                    if (userList.isEmpty()) {
                        // remove old household if there are no more users in the household
                        householdRepository.removeHousehold(sharedPref.getActiveHouseholdId())
                    }

                    // safe to update local household id
                    sharedPref.setActiveHouseholdId(referredHouseholdId)

                    view.goToMainActivity()
                }
            })
    }
}
