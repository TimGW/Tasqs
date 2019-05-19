package com.timgortworst.roomy.ui.profile.presenter

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import com.timgortworst.roomy.local.HuishoudGenootSharedPref
import com.timgortworst.roomy.repository.HouseholdRepository
import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.ui.profile.view.ProfileView
import com.timgortworst.roomy.utils.CoroutineLifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfilePresenter(
    private val view: ProfileView,
    private val userRepository: UserRepository,
    private val householdRepository: HouseholdRepository,
    private val sharedPref: HuishoudGenootSharedPref
) : DefaultLifecycleObserver {

    private val scope = CoroutineLifecycleScope(Dispatchers.Main)

    init {
        if (view is LifecycleOwner) {
            view.lifecycle.addObserver(scope)
        }
    }

    fun getCurrentUser() = scope.launch {
        userRepository.getOrCreateUser()?.let { view.presentUser(it) }
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
