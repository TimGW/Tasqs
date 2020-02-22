package com.timgortworst.roomy.data.repository

import android.os.Handler
import android.util.Log
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.QuerySnapshot
import com.timgortworst.roomy.domain.model.Event
import com.timgortworst.roomy.domain.model.firestore.EventJson
import com.timgortworst.roomy.domain.model.firestore.EventJson.Companion.EVENT_COLLECTION_REF
import com.timgortworst.roomy.domain.model.firestore.EventJson.Companion.EVENT_HOUSEHOLD_ID_REF
import com.timgortworst.roomy.domain.model.NetworkResponse
import com.timgortworst.roomy.domain.model.UIState
import kotlinx.coroutines.tasks.await

class EventRepository {
    private val eventCollectionRef = FirebaseFirestore.getInstance().collection(EVENT_COLLECTION_REF)
    private var registration: ListenerRegistration? = null

    suspend fun createEvent(event: Event): String? {
        val document = eventCollectionRef.document()

        return try {
            document.set(CustomMapper.convertToMap(event.apply { eventId = document.id })).await()
            document.id
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, e.localizedMessage.orEmpty())
            null
        }
    }

    suspend fun updateEvent(event: Event) {
        val document = eventCollectionRef.document(event.eventId)
        try {
            document.update(CustomMapper.convertToMap(event)).await()
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, e.localizedMessage.orEmpty())
        }
    }

    suspend fun getEventsForUser(userId: String): List<Event> {
        if (userId.isBlank()) return emptyList()

        return try {
            eventCollectionRef
                    .whereEqualTo("user.id", userId)
                    .get()
                    .await()
                    .toObjects(EventJson::class.java)
                    .mapNotNull { CustomMapper.toEvent(it) }
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, e.localizedMessage.orEmpty())
            emptyList()
        }
    }

    fun listenToEventsForHousehold(householdId: String, remoteApi: UIState<Event>) {
        val handler = Handler()
        val runnable = Runnable { remoteApi.setState(NetworkResponse.Loading) }
        handler.postDelayed(runnable, android.R.integer.config_shortAnimTime.toLong())

        registration = eventCollectionRef
                .whereEqualTo(EVENT_HOUSEHOLD_ID_REF, householdId)
                .addSnapshotListener(MetadataChanges.INCLUDE, EventListener<QuerySnapshot> { snapshots, e ->
                    handler.removeCallbacks(runnable)
                    Log.d(TAG, "isFromCache: ${snapshots?.metadata?.isFromCache}")
                    val result = when {
                        e != null && snapshots == null -> {
                            Log.e(TAG, "listen:error", e)
                            NetworkResponse.Error
                        }
                        else -> {
                            val changeList = snapshots?.documentChanges ?: return@EventListener
                            val result = mutableListOf<Pair<Event, DocumentChange.Type>>()
                            changeList.forEach {
                                result.add(Pair(CustomMapper.toEvent(it.document.toObject(EventJson::class.java))!!, it.type))
                            }

                            NetworkResponse.HasData(result, snapshots.documents.size, snapshots.metadata.hasPendingWrites())
                        }
                    }
                    remoteApi.setState(result)
                })
    }

    suspend fun updateEvents(events: List<Event>) {
        try {
            val batch = FirebaseFirestore.getInstance().batch()
            events.forEach {
                batch.update(eventCollectionRef.document(it.eventId), CustomMapper.convertToMap(it))
            }
            batch.commit().await()
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, e.localizedMessage.orEmpty())
        }
    }

    suspend fun deleteEvent(eventId: String) {
        try {
            eventCollectionRef
                    .document(eventId)
                    .delete()
                    .await()
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, e.localizedMessage.orEmpty())
        }
    }

    suspend fun deleteEvents(events: List<Event>) {
        try {
            // Get a new write batch and commit all write operations
            val batch = FirebaseFirestore.getInstance().batch()
            events.forEach { batch.delete(eventCollectionRef.document(it.eventId)) }
            batch.commit().await()
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, e.localizedMessage.orEmpty())
        }
    }

    fun detachEventListener() {
        registration?.remove()
    }

    companion object {
        private const val TAG = "EventRepository"
    }
}
