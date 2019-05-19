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
                changeCurrentUserHousehold(referredHouseholdId)
            }
        } else {
            // user is not invited
            if (userHasActiveHousehold()) {
                // user has an active household
                view.goToMainActivity()
            } else {
                // user is not invited and has no household (new user)
                householdRepository.createNewHousehold(
                    onComplete = { householdID ->

                        // update household id for user remote
                        userRepository.setOrUpdateUser(
                            householdId = householdID,
                            role = AuthenticationResult.Role.ADMIN.name,
                            onComplete = { view.goToMainActivity() },
                            onFailure = { view.presentToastError(R.string.generic_error) })
                    },
                    onFailure = {
                        view.presentToastError(R.string.generic_error)
                    })
            }
        }
    }

    private fun userHasActiveHousehold(): Boolean {
        var result = true

        // check locally
        if (sharedPref.getActiveHouseholdId().isNotBlank()) {
            return true
        }

        // check remote fallback when local is blank
        userRepository.getOrCreateUser(
            onComplete = { user ->
                result = if (user.householdId.isNotBlank()) {
                    sharedPref.setActiveHouseholdId(user.householdId)
                    true
                } else {
                    false
                }
            },
            onFailure = {
                view.presentToastError(R.string.generic_error)
            })
    }

    fun changeCurrentUserHousehold(newHouseholdId: String) {
        userRepository.setOrUpdateUser(
            householdId = newHouseholdId,
            role = AuthenticationResult.Role.NORMAL.name,

            onComplete = {
                userRepository.getUsersForHouseholdId(sharedPref.getActiveHouseholdId(),
                    onComplete = {
                        if (it.isEmpty()) {
                            // remove old household if there are no more users in the household
                            householdRepository.removeHousehold(sharedPref.getActiveHouseholdId())
                        }

                        // safe to update local household id
                        sharedPref.setActiveHouseholdId(newHouseholdId)

                        view.goToMainActivity()
                    }, onFailure = {
                        view.presentToastError(R.string.generic_error)
                    })
            }, onFailure = {
                view.presentToastError(R.string.generic_error)
            })
    }
}
