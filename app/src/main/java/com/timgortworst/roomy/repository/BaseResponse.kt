package com.timgortworst.roomy.repository

import com.google.firebase.firestore.DocumentChange
import com.timgortworst.roomy.R
import java.net.ConnectException
import java.net.UnknownHostException


abstract class BaseResponse {

    abstract fun renderSuccessfulState(dc: List<DocumentChange>?)
    abstract fun renderLoadingState()
    abstract fun renderUnsuccessfulState(throwable: Throwable)

    fun determineErrorMessage(data: Throwable?): Int {
        return when (data) {
            null -> R.string.generic_error
            is ConnectException, is UnknownHostException -> R.string.connection_error
            else -> R.string.server_error
        }
    }

    fun setResponse(response: DataListener) {
        when (response) {
            is DataListener.Loading -> renderLoadingState()
            is DataListener.Error -> renderUnsuccessfulState(response.throwable)
            is DataListener.Success -> renderSuccessfulState(response.dc)
        }
    }
}
