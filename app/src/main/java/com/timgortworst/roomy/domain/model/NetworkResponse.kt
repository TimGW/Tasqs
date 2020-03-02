package com.timgortworst.roomy.domain.model

import com.google.firebase.firestore.QuerySnapshot

sealed class NetworkResponse {
    object Loading : NetworkResponse()
    data class Success(val data: QuerySnapshot?) : NetworkResponse()
    data class Error(val error: Throwable?) : NetworkResponse()
}