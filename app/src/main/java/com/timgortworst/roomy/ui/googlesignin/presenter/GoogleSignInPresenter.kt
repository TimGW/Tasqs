package com.timgortworst.roomy.ui.googlesignin.presenter

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.util.Log
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
    private val firebaseAuth: FirebaseAuth,
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
        if (firebaseAuth.currentUser != null) {
            loginSuccessful()
        }
    }

    fun signInWithCredential(credential: AuthCredential) = scope.launch {
        try {
            firebaseAuth.signInWithCredential(credential).await()
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
