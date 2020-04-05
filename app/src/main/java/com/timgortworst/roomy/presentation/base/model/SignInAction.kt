package com.timgortworst.roomy.presentation.base.model

import androidx.annotation.StringRes
import com.timgortworst.roomy.R

sealed class SignInAction {
    object LoadingDialog : SignInAction()
    object MainActivity : SignInAction()
    data class WelcomeBack(val userName: String) : SignInAction()
    data class Failed(@StringRes val errorMsg: Int = R.string.error_generic) : SignInAction()
}
