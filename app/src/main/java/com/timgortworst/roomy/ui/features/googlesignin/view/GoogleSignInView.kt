package com.timgortworst.roomy.ui.features.googlesignin.view

interface GoogleSignInView {
    fun loginSuccessful()
    fun loginFailed()
    fun failedInitUser()
}