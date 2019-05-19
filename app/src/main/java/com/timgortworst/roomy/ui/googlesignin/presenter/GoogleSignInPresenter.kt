package com.timgortworst.roomy.ui.googlesignin.presenter

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.util.Log
import com.google.firebase.auth.AuthCredential
import com.timgortworst.roomy.repository.AuthRepository
import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.ui.googlesignin.view.GoogleSignInView
import com.timgortworst.roomy.utils.await
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


class GoogleSignInPresenter(
    private val view: GoogleSignInView,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : CoroutineScope, DefaultLifecycleObserver {

    lateinit var job: Job // todo improve coroutine setup

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    init {
        if (view is LifecycleOwner) {
            (view as LifecycleOwner).lifecycle.addObserver(this)
        }
    }

    companion object {
        private const val TAG = "TIMTIM"
    }

    override fun onCreate(owner: LifecycleOwner) {
        job = Job()
        if (authRepository.getFirebaseUser() != null) {
            loginSuccessful()
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        job.cancel()
    }

    fun signInWithCredential(credential: AuthCredential) = launch {
        try {
            authRepository.signIn(credential).await()
            Log.d(TAG, "signInTask:success")
            loginSuccessful()
        } catch (e: Exception) {
            // todo show error message
            Log.d(TAG, "signInWithCredential:failure ${e.message}")
        }
    }

    private fun loginSuccessful() {
        userRepository.getOrCreateUser(
            onComplete = { user ->
                view.loginSuccessful()
            },
            onFailure = { /*todo */ })
    }
}
