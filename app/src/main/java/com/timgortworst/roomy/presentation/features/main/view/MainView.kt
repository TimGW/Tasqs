package com.timgortworst.roomy.presentation.features.main.view

import android.net.Uri
import androidx.annotation.StringRes

interface MainView {
    fun logout()
    fun share(householdId: String)
    fun presentShareLinkUri(linkUri: Uri)
    fun showToast(@StringRes stringRes: Int)
    fun openEventEditActivity()
    fun loadAd()
    fun hideAd()
    fun showAd()
    fun presentGoogleAuthFragment()
    fun presentUsersFragment()
}
