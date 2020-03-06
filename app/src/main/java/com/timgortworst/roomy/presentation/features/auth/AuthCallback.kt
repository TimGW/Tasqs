package com.timgortworst.roomy.presentation.features.auth

import androidx.annotation.StringRes

interface AuthCallback {
    fun loginSuccessful()
    fun welcomeBack()
    fun loginFailed(@StringRes errorMessage: Int)
}