package com.timgortworst.roomy.domain.model

sealed class Response {
    object Loading : Response()
    data class Error(val e: Throwable?) : Response()
    data class Success<out T>(val data: T): Response()
}