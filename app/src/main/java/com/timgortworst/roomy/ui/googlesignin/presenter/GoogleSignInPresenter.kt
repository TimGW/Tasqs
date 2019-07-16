package com.timgortworst.roomy.ui.googlesignin.presenter

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.ui.googlesignin.view.GoogleSignInView
import com.timgortworst.roomy.utils.CoroutineLifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class GoogleSignInPresenter(
    private val view: GoogleSignInView,
    private val userRepository: UserRepository
) : DefaultLifecycleObserver {

    private val scope = CoroutineLifecycleScope(Dispatchers.Main)

    init {
        if (view is LifecycleOwner) {
            view.lifecycle.addObserver(scope)
        }
    }

    companion object {
        private const val TAG = "TIMTIM"
    }

    override fun onCreate(owner: LifecycleOwner) {
        if (FirebaseAuth.getInstance().currentUser != null) {
            loginSuccessful()
        }
    }

    fun signInWithCredential(credential: AuthCredential) = scope.launch {
        try {
            FirebaseAuth.getInstance().signInWithCredential(credential).await()
            Log.d(TAG, "signInTask:success")
            loginSuccessful()
        } catch (e: Exception) {
            view.loginFailed()
        }
    }

    private fun loginSuccessful() = scope.launch {
        if (userRepository.getOrCreateUser() != null) {
            view.loginSuccessful()
        } else {
            view.failedInitUser()
        }
    }
}
