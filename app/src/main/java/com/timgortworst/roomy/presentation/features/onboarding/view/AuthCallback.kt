package com.timgortworst.roomy.presentation.features.onboarding.view

import com.google.firebase.auth.FirebaseUser

interface AuthCallback {
    fun loginSuccessful(user: FirebaseUser)
    fun loginFailed()
}