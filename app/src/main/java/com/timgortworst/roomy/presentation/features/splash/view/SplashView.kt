package com.timgortworst.roomy.presentation.features.splash.view

interface SplashView {
    fun goToGoogleSignInActivity()
    fun goToSetupActivityReferred(referredHouseholdId: String)
    fun userInvalid()
    fun goToSetupActivity()
    fun goToMainActivity()
}