package com.timgortworst.roomy.ui.googlesignin.view

interface GoogleSignInView {
    fun loginSuccessful()
    fun loginFailed()
    fun failedInitUser()
}