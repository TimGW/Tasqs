package com.timgortworst.roomy.presentation.features.splash

interface SplashView {
    fun goToSignInActivity()
    fun goToMainActivity()
    fun presentHouseholdOverwriteDialog(referredHouseholdId: String)
    fun presentAlreadyInHouseholdDialog()
}