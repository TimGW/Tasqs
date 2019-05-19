package com.timgortworst.roomy.ui.profile.presenter

import com.timgortworst.roomy.local.HuishoudGenootSharedPref
import com.timgortworst.roomy.model.AuthenticationResult
import com.timgortworst.roomy.repository.HouseholdRepository
import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.ui.profile.view.ProfileView

class ProfilePresenter(
    private val view: ProfileView,
    private val userRepository: UserRepository,
    private val householdRepository: HouseholdRepository,
    private val sharedPref: HuishoudGenootSharedPref
) {

    fun getCurrentUser() {
        userRepository.getUser { user ->
            user?.let { view.presentUser(it) }
        }
    }

    fun removeUserFromActiveHousehold() {

//        // update household id for user remote
//        userRepository.updateUser(householdId = "") {
//            // user is updated, find all users with active householdId
//            userRepository.getUsersForHouseholdId(sharedPref.getActiveHouseholdId()) { userList ->
//
//                if (userList.isEmpty()) {
//                    // remove old household if since there are no more users in the household
//                    householdRepository.removeHousehold(sharedPref.getActiveHouseholdId())
//
//                    // safe to update local household id
//                    sharedPref.setActiveHouseholdId("")
//
//                    view.restartApplication()
//                } else {
//                    userRepository.getUser { user ->
//                        user?.let {
//                            if(it.role == AuthenticationResult.Role.ADMIN.name){
//                                userRepository.updateUser(userId = userList.first().userId, role = AuthenticationResult.Role.ADMIN.name){
//                                    userRepository.updateUser(role = AuthenticationResult.Role.ADMIN.name) {
//                                        // safe to update local household id
//                                        sharedPref.setActiveHouseholdId("")
//
//                                        view.restartApplication()
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
    }
}
