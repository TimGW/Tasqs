package com.timgortworst.roomy.ui.profile.presenter

import com.timgortworst.roomy.local.HuishoudGenootSharedPref
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
        userRepository.getOrCreateUser(
            onComplete = { view.presentUser(it) },
            onFailure = { })
    }

    fun removeUserFromActiveHousehold() {

//        // update household id for user remote
//        userRepository.setOrUpdateUser(householdId = "") {
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
//                                userRepository.setOrUpdateUser(userId = userList.first().userId, role = AuthenticationResult.Role.ADMIN.name){
//                                    userRepository.setOrUpdateUser(role = AuthenticationResult.Role.ADMIN.name) {
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
