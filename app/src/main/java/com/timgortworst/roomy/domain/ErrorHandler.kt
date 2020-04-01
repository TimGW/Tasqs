package com.timgortworst.roomy.domain

import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.roomy.domain.model.response.ErrorEntity

interface ErrorHandler {
    fun getError(firestoreException: FirebaseFirestoreException): ErrorEntity
}