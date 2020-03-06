package com.timgortworst.roomy.presentation.features.task.recyclerview

import com.google.firebase.firestore.FirebaseFirestoreException

interface AdapterStateListener {
    fun onDataChanged(itemCount: Int)
    fun onError(e: FirebaseFirestoreException)
}