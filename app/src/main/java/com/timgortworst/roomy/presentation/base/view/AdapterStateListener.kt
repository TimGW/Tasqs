package com.timgortworst.roomy.presentation.base.view

import com.firebase.ui.common.ChangeEventType
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException

interface AdapterStateListener {
    fun onDataChanged(itemCount: Int)
    fun onError(e: FirebaseFirestoreException)
    fun onChildChanged(
        type: ChangeEventType,
        snapshot: DocumentSnapshot,
        newIndex: Int,
        oldIndex: Int
    )
}