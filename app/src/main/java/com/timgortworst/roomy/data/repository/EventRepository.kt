package com.timgortworst.roomy.data.repository

import android.util.Log
import androidx.lifecycle.liveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.timgortworst.roomy.domain.model.Event
import com.timgortworst.roomy.domain.model.NetworkResponse
import com.timgortworst.roomy.domain.model.firestore.EventJson
import com.timgortworst.roomy.domain.model.firestore.EventJson.Companion.EVENT_COLLECTION_REF
import com.timgortworst.roomy.domain.model.firestore.EventJson.Companion.EVENT_HOUSEHOLD_ID_REF
import com.timgortworst.roomy.domain.model.firestore.EventMetaDataJson.Companion.EVENT_DATE_TIME_REF
import com.timgortworst.roomy.presentation.RoomyApp.Companion.TAG
import kotlinx.coroutines.tasks.await


class EventRepository {
    private val eventCollectionRef = FirebaseFirestore.getInstance().collection(EVENT_COLLECTION_REF)
//    val chatMessagesLiveData = MutableLiveData<NetworkResponse>()

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

    fun getEventsForHousehold(householdId: String): Query {
        return eventCollectionRef
            .whereEqualTo(EVENT_HOUSEHOLD_ID_REF, householdId)
//            .orderBy(EVENT_DATE_TIME_REF, Query.Direction.ASCENDING)
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
}
