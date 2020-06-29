package com.timgortworst.tasqs.presentation.base.model

import androidx.annotation.StringRes

data class EasterEgg(
    @StringRes var id: Int,
    var data: Int? = null
)
