package com.timgortworst.roomy.ui.googlesignin.presenter

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.util.Log
import com.google.firebase.auth.AuthCredential
import com.timgortworst.roomy.repository.AuthRepository
import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.ui.googlesignin.view.GoogleSignInView
import com.timgortworst.roomy.utils.CoroutineLifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.CoroutineContext


class GoogleSignInPresenter(
    private val view: GoogleSignInView,
    private val authRepository: AuthRepository,
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
        if (authRepository.getFirebaseUser() != null) {
            loginSuccessful()
        }
    }

    fun signInWithCredential(credential: AuthCredential) = scope.launch {
        try {
            authRepository.signIn(credential)
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
