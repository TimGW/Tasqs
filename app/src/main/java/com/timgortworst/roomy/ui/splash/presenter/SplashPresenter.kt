package com.timgortworst.roomy.ui.splash.presenter

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.auth.FirebaseAuth
import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.ui.splash.ui.SplashView
import com.timgortworst.roomy.utils.CoroutineLifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SplashPresenter(
    private val view: SplashView,
    private val userRepository: UserRepository
) : DefaultLifecycleObserver {

    private val scope = CoroutineLifecycleScope(Dispatchers.Main)

    init {
        if (view is LifecycleOwner) {
            view.lifecycle.addObserver(scope)
        }
    }

    fun userLogin(referredHouseholdId: String) = scope.launch {
        if (FirebaseAuth.getInstance().currentUser == null) {
            // user will be created in the googleSignInActivity
            view.goToGoogleSignInActivity()
        } else {
            // get the user if he exists or create a new one in firebase
            val user = userRepository.getOrCreateUser()

            if (user != null) {
                // user is created or retrieved
                if (referredHouseholdId.isNotBlank()) {

                    // user accepted invite link
                    view.goToSetupActivity(referredHouseholdId)
                } else {
                    view.goToSetupActivity()
                }
            } else {
                view.userInvalid()
            }
        }
    }
}
