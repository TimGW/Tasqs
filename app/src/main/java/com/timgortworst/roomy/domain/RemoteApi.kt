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

//sealed class Response<T>(
//        val data: T? = null,
//        val message: String? = null
//) {
//    class Success<T>(data: T) : Response<T>(data)
//    class Loading<T>(data: T? = null, var refreshing: Boolean = false) : Response<T>(data)
//    class Error<T>(data: T? = null, message: String) : Response<T>(data, message)
//}
//
//data class ResponseData<T>(val data: T?, val totalDataSetSize: Int, val hasPendingWrites: Boolean)