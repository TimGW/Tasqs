package com.timgortworst.roomy.ui.features.main.view

import android.net.Uri

interface MainView {
    fun logout()
    fun share(householdId: String)
    fun presentShareLinkUri(linkUri: Uri)
}
