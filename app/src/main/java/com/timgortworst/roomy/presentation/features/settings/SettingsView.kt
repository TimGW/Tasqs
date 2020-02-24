package com.timgortworst.roomy.presentation.features.settings

import androidx.annotation.StringRes

interface SettingsView {
    fun toasti(@StringRes stringRes: Int, argument: Int? = null)
}