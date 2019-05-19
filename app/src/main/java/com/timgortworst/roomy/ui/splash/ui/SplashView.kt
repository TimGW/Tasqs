package com.timgortworst.roomy.ui.splash.ui

interface SplashView {
    fun goToGoogleSignInActivity()
    fun goToSetupActivity(referredHouseholdId: String = "")
    fun userInvalid()
}