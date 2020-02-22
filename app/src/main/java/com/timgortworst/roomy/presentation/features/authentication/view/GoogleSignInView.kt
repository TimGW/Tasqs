package com.timgortworst.roomy.presentation.features.authentication.view

interface GoogleSignInView {
    fun loginSuccessful()
    fun loginFailed()
    fun failedInitUser()
}