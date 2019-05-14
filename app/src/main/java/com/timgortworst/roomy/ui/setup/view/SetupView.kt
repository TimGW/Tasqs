package com.timgortworst.roomy.ui.setup.view

interface SetupView {
    fun presentTextValidationError(errorStringResourceId: Int)
    fun goToMainActivity()
    fun presentToastError(error: Int)
}
