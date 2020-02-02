package com.timgortworst.roomy.domain

import com.google.firebase.firestore.DocumentChange

interface RemoteApi<T> {
    fun renderSuccessfulState(changeSet: List<Pair<T, DocumentChange.Type>>, totalDataSetSize: Int, hasPendingWrites: Boolean)
    fun renderLoadingState()
    fun renderUnsuccessfulState()

    fun setState(response: Response<T>) {
        when (response) {
            is Response.Loading -> renderLoadingState()
            is Response.Error -> renderUnsuccessfulState()
            is Response.Success -> renderSuccessfulState(response.dc, response.totalDataSetSize, response.hasPendingWrites)
        }
    }
}

sealed class Response<out T> {
    object Loading : Response<Nothing>()
    object Error : Response<Nothing>()
    data class Success<T>(val dc: List<Pair<T, DocumentChange.Type>>, val totalDataSetSize: Int, val hasPendingWrites: Boolean) : Response<T>()
}
