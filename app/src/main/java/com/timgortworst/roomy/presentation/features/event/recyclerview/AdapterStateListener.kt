package com.timgortworst.roomy.presentation.features.event.recyclerview

import com.google.firebase.firestore.FirebaseFirestoreException

interface AdapterStateListener {
    fun onEmptyState(isVisible: Int)
    fun onErrorState(isVisible: Int, e: FirebaseFirestoreException? = null)
    fun onLoadingState(isVisible: Int)
}