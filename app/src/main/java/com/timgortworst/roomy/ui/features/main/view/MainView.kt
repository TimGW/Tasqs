package com.timgortworst.roomy.ui.features.main.view

import android.net.Uri
import androidx.annotation.StringRes

interface MainView {
    fun logout()
    fun share(householdId: String)
    fun presentShareLinkUri(linkUri: Uri)
    fun loadAd()
    fun showToast(@StringRes stringRes: Int)
}
