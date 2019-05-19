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
            if (isHouseholdActive()) {

                // caution user that household will be overwritten
                view.presentHouseholdOverwriteDialog()
            } else {

                // no active household, so update
                updateHousehold(referredHouseholdId)
            }
        } else {

            // user started the application his/her self
            if (isHouseholdActive()) {

                // user has an active household
                view.goToMainActivity()
            } else {

                // user is not invited and has no household (new user)
                householdRepository.createNewHousehold(
                    onComplete = { householdID ->

                        // update local household id
                        sharedPref.setHouseholdId(householdID)

                        // update household id for user remote
                        userRepository.updateUser(householdId = householdID) {
                            view.goToMainActivity()
                        }
                    },
                    onFailure = {
                        view.presentToastError(R.string.generic_error)
                    })
            }
        }
    }

    private fun isHouseholdActive(): Boolean {
        if (sharedPref.getHouseholdId().isNotBlank()) {
            return true
        }

        var isHouseholdActive = false
        userRepository.getUser { user ->
            if (user?.householdId?.isNotBlank() == true) {
                sharedPref.setHouseholdId(user.householdId)
                isHouseholdActive = true
            }
        }
        return isHouseholdActive
    }

    fun updateHousehold(referredHouseholdId: String) {
        // update local household id
        sharedPref.setHouseholdId(referredHouseholdId)

        userRepository.updateUser(householdId = referredHouseholdId, role = AuthenticationResult.Role.NORMAL.name) {
            view.goToMainActivity()
        }
    }
}
