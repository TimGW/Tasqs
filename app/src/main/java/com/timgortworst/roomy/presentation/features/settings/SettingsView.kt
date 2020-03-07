package com.timgortworst.roomy.presentation.features.settings

import androidx.annotation.StringRes

interface SettingsView {
    fun easterEggMsg(@StringRes stringRes: Int, argument: Int? = null)
}