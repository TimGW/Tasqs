package com.timgortworst.roomy.domain.model

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.*
import com.timgortworst.roomy.presentation.RoomyApp.Companion.TAG

class QuerySnapshotLiveData(
    private val query: Query
) : LiveData<NetworkResponse>(), EventListener<QuerySnapshot> {
    private var registration: ListenerRegistration? = null
    private var listenerRemovePending = false
    private val handler: Handler = Handler(Looper.getMainLooper())
    private val removeListener = Runnable {
        registration?.also {
            it.remove()
            registration = null
        }
        listenerRemovePending = false
    }

    override fun onEvent(snapshot: QuerySnapshot?, e: FirebaseFirestoreException?) {
        value = if (e != null && snapshot == null) {
            NetworkResponse.Error(e.localizedMessage!!)
        } else {
            NetworkResponse.Success(snapshot)
        }
        Log.i(TAG, "onEvent")
    }

    override fun onActive() {
        super.onActive()
        if (listenerRemovePending) {
            handler.removeCallbacks(removeListener)
        }
        else {
            registration = query.addSnapshotListener(MetadataChanges.INCLUDE,this)
            Log.i(TAG, "addedSnapShotListener")
        }
        listenerRemovePending = false
    }

    override fun onInactive() {
        super.onInactive()
        handler.postDelayed(removeListener, 2000)
        listenerRemovePending = true
        Log.i(TAG, "onInactive")
    }
}