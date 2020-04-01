package com.timgortworst.roomy.data.error

import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.FirebaseFirestoreException.Code.*
import com.timgortworst.roomy.domain.ErrorHandler
import com.timgortworst.roomy.domain.model.response.ErrorEntity

class ErrorHandlerImpl : ErrorHandler {

    override fun getError(firestoreException: FirebaseFirestoreException): ErrorEntity {
        return when (firestoreException.code) {
            CANCELLED -> ErrorEntity.Network(firestoreException)
            ABORTED -> ErrorEntity.Network(firestoreException)
            NOT_FOUND -> ErrorEntity.NotFound(firestoreException)
            PERMISSION_DENIED -> ErrorEntity.AccessDenied(firestoreException)
            UNAVAILABLE -> ErrorEntity.ServiceUnavailable(firestoreException)
            UNAUTHENTICATED -> ErrorEntity.AccessDenied(firestoreException)
            else -> ErrorEntity.Unknown(firestoreException)
        }
    }
}