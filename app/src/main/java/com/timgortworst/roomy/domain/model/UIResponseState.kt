package com.timgortworst.roomy.domain.model

import androidx.annotation.StringRes

sealed class UIResponseState {
    object Loading : UIResponseState()
    data class Error(@StringRes val message: Int) : UIResponseState()
    data class Success<out T>(val data: T): UIResponseState()
}