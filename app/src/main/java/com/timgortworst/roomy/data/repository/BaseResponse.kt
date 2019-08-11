package com.timgortworst.roomy.data.repository

import com.google.firebase.firestore.DocumentChange

abstract class BaseResponse {

    abstract fun renderSuccessfulState(dc: List<DocumentChange>, totalDataSetSize: Int, hasPendingWrites: Boolean)
    abstract fun renderLoadingState()
    abstract fun renderUnsuccessfulState(throwable: Throwable)

    fun setResponse(response: DataListener) {
        when (response) {
            is DataListener.Loading -> renderLoadingState()
            is DataListener.Error -> renderUnsuccessfulState(response.throwable)
            is DataListener.Success -> renderSuccessfulState(response.dc, response.totalDataSetSize, response.hasPendingWrites)
        }
    }
}
