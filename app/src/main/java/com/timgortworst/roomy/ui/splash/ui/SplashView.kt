package com.timgortworst.roomy.ui.splash.ui

interface SplashView {
    fun userNotLoggedIn()
    fun userSetupValid()
    fun userSetupInvalid()
    fun userAcceptedInvite(householdId: String)
}