package com.timgortworst.roomy.domain.model

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.*
import com.timgortworst.roomy.presentation.RoomyApp.Companion.TAG

class QuerySnapshotLiveData(
    private val query: Query
) : LiveData<NetworkResponse>(), EventListener<QuerySnapshot> {
    private var registration: ListenerRegistration? = null

    override fun onEvent(snapshot: QuerySnapshot?, e: FirebaseFirestoreException?) {
        value = if (e != null && snapshot == null) {
            Log.i(TAG, "NetworkResponse.Error: ${e.message}")
            NetworkResponse.Error(e)
        } else {
//            Log.i(TAG, "NetworkResponse.Success")
            Log.d(TAG, "FromCache: ${snapshot?.metadata?.isFromCache}")
            Log.d(TAG, "documentChanges: ${snapshot?.documentChanges?.size}")
            Log.d(TAG, "documents: ${snapshot?.documents?.size}")
            NetworkResponse.Success(snapshot)
        }
    }

    override fun onActive() {
        super.onActive()
        Log.d(TAG, "addSnapshotListener")
        registration = query.addSnapshotListener(this)
    }

    override fun onInactive() {
        super.onInactive()
        registration?.remove()
        registration = null
        Log.d(TAG, "removedSnapshotListener")
    }
}