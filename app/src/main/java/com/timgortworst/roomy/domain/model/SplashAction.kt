package com.timgortworst.roomy.domain.model

sealed class SplashAction {
    object SignInActivity : SplashAction()
    object MainActivity : SplashAction()
    object DialogAlreadyInHousehold : SplashAction()
    data class DialogOverride(val id: String) : SplashAction()
}
