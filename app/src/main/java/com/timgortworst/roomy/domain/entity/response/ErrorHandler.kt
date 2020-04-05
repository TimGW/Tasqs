package com.timgortworst.roomy.domain.entity.response

import com.google.firebase.firestore.FirebaseFirestoreException

interface ErrorHandler {
    fun getError(firestoreException: FirebaseFirestoreException): ErrorEntity
}