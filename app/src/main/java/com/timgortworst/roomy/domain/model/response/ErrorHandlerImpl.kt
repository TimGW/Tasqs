package com.timgortworst.roomy.domain.model.response

import androidx.annotation.Keep
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.FirebaseFirestoreException.Code.*

@Keep
class ErrorHandlerImpl :
    ErrorHandler {

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