package com.timgortworst.tasqs.presentation.base.view

import com.google.firebase.firestore.FirebaseFirestoreException

interface AdapterStateListener {
    fun onDataChanged(itemCount: Int)
    fun onError(e: FirebaseFirestoreException)
}