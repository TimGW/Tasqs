package com.timgortworst.roomy.presentation.features.auth

interface AuthCallback {
    fun loginSuccessful()
    fun loginFailed()
    fun welcomeBack()
}