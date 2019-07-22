package com.timgortworst.roomy.ui.splash.presenter

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.auth.FirebaseAuth
import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.ui.splash.ui.SplashView
import com.timgortworst.roomy.utils.CoroutineLifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class SplashPresenter @Inject constructor(
    private val view: SplashView,
    private val userRepository: UserRepository
) : DefaultLifecycleObserver {

    private val scope = CoroutineLifecycleScope(Dispatchers.Main)

    init {
        if (view is LifecycleOwner) {
            view.lifecycle.addObserver(scope)
        }
    }

    fun userLogin(referredHouseholdId: String = "") = scope.launch {
        if (FirebaseAuth.getInstance().currentUser == null) {
            // user will be created in the googleSignInActivity
            view.goToGoogleSignInActivity()
        } else {
            // create a new unique user in firebase
            userRepository.createNewUser()

            // user is created or retrieved
            if (referredHouseholdId.isNotBlank()) {

                // user accepted invite link
                view.goToSetupActivity(referredHouseholdId)
            } else {
                view.goToSetupActivity()
            }
        }
    }
}
