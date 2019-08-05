package com.timgortworst.roomy.ui.setup.view

interface SetupView {
    fun goToMainActivity()
    fun presentToastError(error: Int)
    fun presentHouseholdOverwriteDialog()
    fun presentAlreadyInHouseholdDialog()
    fun presentUserIsBannedDialog()
    fun presentHouseholdFullDialog()
}
