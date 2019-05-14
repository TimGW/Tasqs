package com.timgortworst.roomy.ui.splash.presenter

import com.google.firebase.auth.FirebaseAuth
import com.timgortworst.roomy.local.HuishoudGenootSharedPref
import com.timgortworst.roomy.repository.HouseholdRepository
import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.ui.splash.ui.SplashView
import kotlinx.coroutines.InternalCoroutinesApi

class SplashPresenter(
    private val view: SplashView,
    private val householdRepository: HouseholdRepository,
    private val userRepository: UserRepository,
    private val auth: FirebaseAuth,
    private val sharedPref: HuishoudGenootSharedPref) {

    fun initializeApplication(householdId: String) {
        // check if user is logged in
        if (auth.currentUser == null) {
            view.userNotLoggedIn()
            return
        }

        if(householdId.isNotBlank()){
            view.userAcceptedInvite(householdId)
            return
        }

        // pre check to speed up the splash screen
        if(sharedPref.getHouseholdId().isNotBlank()){
            view.userSetupValid()
        } else {
            //todo handle when user has no internet
            // check if user is correctly setup his account
            userRepository.getUser { user ->
                val houseId = user?.householdId.orEmpty()
                householdRepository.isHouseholdInDb(houseId) { isHouseholdInDb ->
                    if (isHouseholdInDb) {
                        view.userSetupValid()
                    } else {
                        view.userSetupInvalid()
                    }
                }
            }
        }
    }
}
