package com.timgortworst.roomy.domain.model.response

import androidx.annotation.Keep

@Keep
sealed class ErrorEntity {
    abstract val originalException: Throwable
    data class Network(override val originalException: Throwable) : ErrorEntity()
    data class NotFound(override val originalException: Throwable) : ErrorEntity()
    data class AccessDenied(override val originalException: Throwable) : ErrorEntity()
    data class ServiceUnavailable(override val originalException: Throwable) : ErrorEntity()
    data class Unknown(override val originalException: Throwable) : ErrorEntity()
}