package com.timgortworst.roomy.domain.model

import com.google.firebase.firestore.QuerySnapshot

sealed class NetworkResponse {
    object Loading : NetworkResponse()
    data class Success(val data: QuerySnapshot?) : NetworkResponse()
    data class Error(val error: Throwable?) : NetworkResponse()
//    data class NetworkException(val error: String) : NetworkResponse()
//    sealed class HttpErrors : NetworkResponse() {
//        data class ResourceForbidden(val exception: String) : HttpErrors()
//        data class ResourceNotFound(val exception: String) : HttpErrors()
//        data class InternalServerError(val exception: String) : HttpErrors()
//        data class BadGateWay(val exception: String) : HttpErrors()
//        data class ResourceRemoved(val exception: String) : HttpErrors()
//        data class RemovedResourceFound(val exception: String) : HttpErrors()
//    }
}