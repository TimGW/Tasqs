package com.timgortworst.roomy.presentation.base.model

import androidx.annotation.StringRes
import com.timgortworst.roomy.R

sealed class SplashAction {
    object SignInActivity : SplashAction()
    object MainActivity : SplashAction()
    object DialogAlreadyInHousehold : SplashAction()
    data class DialogOverride(val id: String) : SplashAction()
    object DialogLoading : SplashAction()
    data class DialogError(@StringRes val errorMsg: Int = R.string.error_generic) : SplashAction()
}
