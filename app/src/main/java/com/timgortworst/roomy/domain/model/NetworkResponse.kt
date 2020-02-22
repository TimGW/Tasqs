package com.timgortworst.roomy.domain.model

import com.google.firebase.firestore.DocumentChange

sealed class NetworkResponse<out T> {
    object Loading : NetworkResponse<Nothing>()
    data class HasData<T>(val dc: List<Pair<T, DocumentChange.Type>>, val totalDataSetSize: Int, val hasPendingWrites: Boolean) : NetworkResponse<T>()
    object Error : NetworkResponse<Nothing>()
}

interface UIState<T> {
    fun renderLoadingState(isLoading: Boolean)
    fun renderSuccessfulState(changeSet: List<Pair<T, DocumentChange.Type>>, totalDataSetSize: Int, hasPendingWrites: Boolean)
    fun renderErrorState(hasError: Boolean)

    fun setState(response: NetworkResponse<T>) {
        when (response) {
            is NetworkResponse.Loading -> {
                renderErrorState(false)
                renderLoadingState(true)
            }
            is NetworkResponse.HasData -> {
                renderLoadingState(false)
                renderErrorState(false)
                renderSuccessfulState(response.dc, response.totalDataSetSize, response.hasPendingWrites)
            }
            is NetworkResponse.Error -> {
                renderLoadingState(false)
                renderErrorState(true)
            }
        }
    }
}
