package com.timgortworst.tasqs.domain.model.response

import com.google.firebase.firestore.FirebaseFirestoreException

interface ErrorHandler {
    fun getError(firestoreException: FirebaseFirestoreException): ErrorEntity
}