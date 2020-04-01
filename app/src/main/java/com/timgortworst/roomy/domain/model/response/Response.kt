package com.timgortworst.roomy.domain.model.response

sealed class Response<out T> {
    object Loading : Response<Nothing>()
    data class Success<T>(val data: T? = null) : Response<T>()
    data class Error(val error: ErrorEntity? = null) : Response<Nothing>()
}