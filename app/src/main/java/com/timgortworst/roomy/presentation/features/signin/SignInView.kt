package com.timgortworst.roomy.presentation.features.signin

import androidx.annotation.StringRes

interface SignInView {
    fun loginSuccessful()
    fun welcomeBack(displayName: String?)
    fun loginFailed(@StringRes errorMessage: Int)
}