package com.timgortworst.roomy.domain.model

import com.google.firebase.firestore.FirebaseFirestoreException

sealed class Response<out T> {
    object Loading : Response<Nothing>()
    data class Success<T>(val data: T) : Response<T>()
    data class Error(
        val firestoreException: FirebaseFirestoreException? = null,
        val message: String? = null
    ) : Response<Nothing>()
}