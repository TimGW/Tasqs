package com.timgortworst.roomy.domain

import com.google.firebase.firestore.DocumentChange

interface UIState<T> {
    fun renderLoadingState(isLoading: Boolean)
    fun renderSuccessfulState(changeSet: List<Pair<T, DocumentChange.Type>>, totalDataSetSize: Int, hasPendingWrites: Boolean)
    fun renderErrorState(hasError: Boolean)

    fun setState(response: Response<T>) {
        when (response) {
            is Response.Loading -> {
                renderErrorState(false)
                renderLoadingState(true)
            }
            is Response.HasData -> {
                renderLoadingState(false)
                renderErrorState(false)
                renderSuccessfulState(response.dc, response.totalDataSetSize, response.hasPendingWrites)
            }
            is Response.Error -> {
                renderLoadingState(false)
                renderErrorState(true)
            }
        }
    }
}

sealed class Response<out T> {
    object Loading : Response<Nothing>()
    data class HasData<T>(val dc: List<Pair<T, DocumentChange.Type>>, val totalDataSetSize: Int, val hasPendingWrites: Boolean) : Response<T>()
    object Error : Response<Nothing>()
}
