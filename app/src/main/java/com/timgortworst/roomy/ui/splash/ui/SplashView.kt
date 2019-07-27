package com.timgortworst.roomy.ui.splash.ui

interface SplashView {
    fun goToGoogleSignInActivity()
    fun goToSetupActivityReferred(referredHouseholdId: String)
    fun userInvalid()
    fun goToSetupActivity()
}