package com.timgortworst.roomy.presentation.features.googlesignin.view

interface GoogleSignInView {
    fun loginSuccessful()
    fun loginFailed()
    fun failedInitUser()
}