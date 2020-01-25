package com.timgortworst.roomy.domain

import com.google.firebase.firestore.DocumentChange

abstract class ApiStatus {

    abstract fun renderSuccessfulState(dc: MutableList<DocumentChange>, totalDataSetSize: Int, hasPendingWrites: Boolean)
    abstract fun renderLoadingState()
    abstract fun renderUnsuccessfulState(throwable: Throwable)

    fun setState(response: Response) {
        when (response) {
            is Response.Loading -> renderLoadingState()
            is Response.Error -> renderUnsuccessfulState(response.throwable)
            is Response.Success -> renderSuccessfulState(response.dc, response.totalDataSetSize, response.hasPendingWrites)
        }
    }

    sealed class Response {
        object Loading : Response()
        data class Success(val dc: MutableList<DocumentChange>, val totalDataSetSize: Int, val hasPendingWrites: Boolean) : Response()
        data class Error(val throwable: Throwable) : Response()
    }
}
