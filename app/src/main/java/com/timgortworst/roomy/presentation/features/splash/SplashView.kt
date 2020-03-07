package com.timgortworst.roomy.presentation.features.splash

interface SplashView {
    fun goToSignInActivity()
    fun goToMainActivity()
    fun presentHouseholdOverwriteDialog()
    fun presentAlreadyInHouseholdDialog()
}