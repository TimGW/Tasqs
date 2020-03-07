package com.timgortworst.roomy.domain.model

import androidx.annotation.StringRes
import com.timgortworst.roomy.R

sealed class Response {
    data class Success<T>(val data: T? = null) : Response()
    data class Error(@StringRes val message: Int = R.string.error_generic) : Response()
}