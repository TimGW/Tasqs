package com.timgortworst.roomy.ui.signin.presenter

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.util.Log
import com.google.firebase.auth.AuthCredential
import com.timgortworst.roomy.repository.AuthRepository
import com.timgortworst.roomy.repository.HouseholdRepository
import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.ui.signin.view.SignInView
import com.timgortworst.roomy.utils.await
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


class SignInPresenter(
    private val view: SignInView,
    private val userRepository: UserRepository,
    private val householdRepository: HouseholdRepository,
    private val authRepository: AuthRepository
) : CoroutineScope, DefaultLifecycleObserver {

    lateinit var job: Job

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
            view.loginSuccessful()
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        job.cancel()
    }

    fun signInWithCredential(credential: AuthCredential) = launch {
        try {
            authRepository.signIn(credential).await()
            Log.d(TAG, "signInTask:success")
            view.loginSuccessful()
        } catch (e: Exception) {
            // todo show error message
            Log.d(TAG, "signInWithCredential:failure ${e.message}")
        }
    }

    fun isHouseholdInDb(onComplete: (isHouseholdInDb: Boolean) -> Unit) {
        userRepository.getUser { user ->
            val householdId = user?.householdId.orEmpty()
            householdRepository.isHouseholdInDb(householdId, onComplete)
        }
    }
}
