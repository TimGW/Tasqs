package com.timgortworst.roomy.domain.model

import androidx.annotation.StringRes

sealed class ResponseState {
    object Loading : ResponseState()
    data class Error(@StringRes val message: Int) : ResponseState()
    data class Success<out T>(val data: T): ResponseState()
}