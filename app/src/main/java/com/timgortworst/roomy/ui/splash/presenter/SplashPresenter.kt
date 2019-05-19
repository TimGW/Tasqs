package com.timgortworst.roomy.ui.splash.presenter

import com.google.firebase.auth.FirebaseAuth
import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.ui.splash.ui.SplashView

class SplashPresenter(
    private val view: SplashView,
    private val userRepository: UserRepository,
    private val auth: FirebaseAuth
) {

    fun userLogin(referredHouseholdId: String) {
        if (auth.currentUser == null) {
            view.goToGoogleSignInActivity()
        } else {
            // get the user if he exists or create a new one in firebase
            userRepository.getOrCreateUser(
                onComplete = {

                    // user is created or retrieved
                    if (referredHouseholdId.isNotBlank()) {

                        // user accepted invite link
                        view.goToSetupActivity(referredHouseholdId)
                    } else {
                        view.goToSetupActivity()
                    }
                },
                onFailure = {
                    view.userInvalid()
                })
        }
    }
}
