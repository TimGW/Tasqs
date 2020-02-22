package com.timgortworst.roomy.presentation.features.onboarding.presenter

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.timgortworst.roomy.presentation.base.CoroutineLifecycleScope
import com.timgortworst.roomy.presentation.features.onboarding.view.AuthCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.koin.core.KoinComponent

class AuthenticationPresenter(
        private val view: AuthCallback
) : DefaultLifecycleObserver, KoinComponent {
    private val auth = FirebaseAuth.getInstance()
    private val scope = CoroutineLifecycleScope(Dispatchers.Main)

    init {
        if (view is LifecycleOwner) {
            view.lifecycle.addObserver(scope)
        }
    }

    fun signInAnonymously() = scope.launch {
        auth.currentUser?.let {
            view.loginSuccessful(it)
            return@launch
        }

        try {
            auth.signInAnonymously().await().user?.let {
                view.loginSuccessful(it)
            }
        } catch (e: Exception) {
            view.loginFailed()
        }
    }

    fun signInWithCredential(credential: AuthCredential) = scope.launch {
        auth.currentUser?.let {
            view.loginSuccessful(it)
            return@launch
        }

        try {
            auth.signInWithCredential(credential).await().user?.let {
                view.loginSuccessful(it)
            }
        } catch (e: Exception) {
            view.loginFailed()
        }
    }
}
