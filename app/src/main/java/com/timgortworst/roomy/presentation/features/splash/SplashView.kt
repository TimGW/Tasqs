package com.timgortworst.roomy.presentation.features.splash

interface SplashView {
    fun goToOnboardingActivity()
    fun goToMainActivity()
    fun presentHouseholdOverwriteDialog()
    fun presentAlreadyInHouseholdDialog()
//    fun presentUserIsBannedDialog()
//    fun presentHouseholdFullDialog()
}