package com.timgortworst.roomy.ui.features.splash.ui

interface SplashView {
    fun goToGoogleSignInActivity()
    fun goToSetupActivityReferred(referredHouseholdId: String)
    fun userInvalid()
    fun goToSetupActivity()
    fun goToMainActivity()
}