package com.timgortworst.roomy.domain.model

import com.google.firebase.firestore.QuerySnapshot

sealed class NetworkResponse {
    object Loading: NetworkResponse()
    data class Error(val errorMessage: String): NetworkResponse()
    data class Success(val data: QuerySnapshot?): NetworkResponse() {
//        inline fun <reified T> isInstanceOf(instance: Any?): Boolean {
//            return instance is T
//        }
    }
}
//
//
//sealed class NetworkResponse<out T> {
//    object Loading : NetworkResponse<Nothing>()
//    data class Error(val throwable: Throwable) : NetworkResponse<Nothing>()
//    data class Success<T>(val data: T) : NetworkResponse<T>()
//    data class NetworkException(val error : String) : NetworkResponse<Nothing>()
//    sealed class HttpErrors : NetworkResponse<Nothing>() {
//        data class ResourceForbidden(val exception: String) : HttpErrors()
//        data class ResourceNotFound(val exception: String) : HttpErrors()
//        data class InternalServerError(val exception: String) : HttpErrors()
//        data class BadGateWay(val exception: String) : HttpErrors()
//        data class ResourceRemoved(val exception: String) : HttpErrors()
//        data class RemovedResourceFound(val exception: String) : HttpErrors()
//    }
//}