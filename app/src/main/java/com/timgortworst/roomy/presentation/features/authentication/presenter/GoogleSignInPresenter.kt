package com.timgortworst.roomy.presentation.features.authentication.presenter

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.timgortworst.roomy.presentation.base.CoroutineLifecycleScope
import com.timgortworst.roomy.presentation.features.authentication.view.GoogleSignInView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.koin.core.KoinComponent

class GoogleSignInPresenter (
        private val view: GoogleSignInView
) : DefaultLifecycleObserver, KoinComponent {

    private val scope = CoroutineLifecycleScope(Dispatchers.Main)

    init {
        if (view is LifecycleOwner) {
            view.lifecycle.addObserver(scope)
        }
    }

    fun signInWithCredential(credential: AuthCredential) = scope.launch {
        try {
            FirebaseAuth.getInstance().signInWithCredential(credential).await()
            view.loginSuccessful()
        } catch (e: Exception) {
            view.loginFailed()
        }
    }
}
