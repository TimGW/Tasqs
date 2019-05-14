package com.timgortworst.roomy.ui.setup.presenter

import com.timgortworst.roomy.R
import com.timgortworst.roomy.model.User
import com.timgortworst.roomy.repository.HouseholdRepository
import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.ui.setup.view.SetupView
import kotlinx.coroutines.InternalCoroutinesApi



class SetupPresenter(
    private val view: SetupView,
    private val householdRepository: HouseholdRepository,
    private val userRepository: UserRepository
) {

    fun setupInitialHousehold(
        isNewHousehold: Boolean,
        isExistingHousehold: Boolean,
        householdCode: String
    ) {

        // create a new household and add a new user to it
        if (isNewHousehold) {
            householdRepository.createNewHouseholdAndUser().addOnSuccessListener {
                view.goToMainActivity()
            }.addOnFailureListener {
                view.presentToastError(R.string.generic_error)
            }
        } else if (isExistingHousehold && householdCode.isBlank()) {
            view.presentTextValidationError(R.string.setup_referral_code_empty_error)
        } else {
            // get the users if he exists or create a new one and add him to the householdId
            userRepository.getOrCreateUser { user ->
                user.householdId = householdCode
                user.role = User.Role.USER.name

                userRepository.setUser(user) {
                    view.goToMainActivity()
                }
//                userRepository.updateUser(householdId = householdCode, role = User.Role.USER.name) {
//                    view.goToMainActivity()
//                }
            }
        }
    }
}
