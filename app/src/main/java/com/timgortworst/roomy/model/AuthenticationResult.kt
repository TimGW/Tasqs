package com.timgortworst.roomy.model

sealed class AuthenticationResult {
    data class Success(val role: Role) : AuthenticationResult()
    data class Failure(val reason: FailureReason) : AuthenticationResult()

    enum class Role {
        READONLY,
        NORMAL,
        ADMIN
    }
    enum class FailureReason {
        BLANK_USER_OR_PW,
        INVALID_USER_OR_PW,
        USER_IS_NOT_IN_GROUP,
        CONNECTION_ISSUES,
        FAILED_GET_USER,
        FAILED_SET_USER
    }
}
