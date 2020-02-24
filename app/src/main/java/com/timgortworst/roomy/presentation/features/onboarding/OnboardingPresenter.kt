package com.timgortworst.roomy.presentation.features.onboarding

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.timgortworst.roomy.domain.usecase.EventUseCase
import com.timgortworst.roomy.domain.usecase.SetupUseCase
import com.timgortworst.roomy.domain.usecase.UserUseCase
import com.timgortworst.roomy.presentation.base.CoroutineLifecycleScope
import com.timgortworst.roomy.presentation.features.auth.AuthCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class OnboardingPresenter(
        private val view: AuthCallback,
        private val setupUseCase: SetupUseCase,
        private val userUseCase: UserUseCase,
        private val eventUseCase: EventUseCase
) : DefaultLifecycleObserver {
    private val auth = FirebaseAuth.getInstance()
    private val scope = CoroutineLifecycleScope(Dispatchers.Main)

    init {
        if (view is LifecycleOwner) {
            view.lifecycle.addObserver(scope)
        }
    }

    fun signInAnonymously() = scope.launch {
        trySignIn { auth.signInAnonymously() }
    }

    fun signInOrLinkCredential(credential: AuthCredential, displayName: String?) = scope.launch {
        val prevUser = auth.currentUser
        if (prevUser?.isAnonymous == true) {
            val newUser = prevUser.linkWithCredential(credential).await()?.user
            userUseCase.updateUser(displayName.orEmpty(), newUser?.email.orEmpty())
            eventUseCase.updateEventsForUser(newUser?.uid, displayName.orEmpty(), newUser?.email.orEmpty())
            view.setupSuccessful()
        } else {
            trySignIn { auth.signInWithCredential(credential) }
        }
    }

    private suspend fun trySignIn(action: () -> Task<AuthResult>) {
        try {
            val fireBaseUser = action.invoke().await().user
            if (fireBaseUser != null) {
                setupHousehold(fireBaseUser)
            } else {
                view.setupFailed()
            }
        } catch (e: Exception) {
            view.setupFailed()
        }
    }

    private fun setupHousehold(fireBaseUser: FirebaseUser) = scope.launch {
        setupUseCase.initializeHousehold(fireBaseUser)?.let {
            view.setupSuccessful()
            return@launch
        }
        view.setupFailed()
    }
}
