package com.timgortworst.roomy.presentation.features.auth

interface AuthCallback {
    fun setupSuccessful()
    fun setupFailed()
}