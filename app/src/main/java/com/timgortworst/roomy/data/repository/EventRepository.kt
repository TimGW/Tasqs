package com.timgortworst.roomy.data.repository

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.timgortworst.roomy.domain.model.Event
import com.timgortworst.roomy.domain.model.Household
import com.timgortworst.roomy.domain.model.User.Companion.USER_ID_REF
import com.timgortworst.roomy.domain.model.firestore.EventJson
import com.timgortworst.roomy.domain.model.firestore.EventJson.Companion.EVENT_COLLECTION_REF
import com.timgortworst.roomy.domain.model.firestore.EventJson.Companion.EVENT_HOUSEHOLD_ID_REF
import com.timgortworst.roomy.domain.model.firestore.EventJson.Companion.EVENT_META_DATA_REF
import com.timgortworst.roomy.domain.model.firestore.EventJson.Companion.EVENT_USER_REF
import com.timgortworst.roomy.domain.model.firestore.EventMetaDataJson.Companion.EVENT_DATE_TIME_REF
import com.timgortworst.roomy.presentation.RoomyApp.Companion.TAG
import kotlinx.coroutines.tasks.await

class EventRepository(private val idProvider: IdProvider) {

    private suspend fun collectionRef(): CollectionReference {
        return FirebaseFirestore
        .getInstance()
        .collection(Household.HOUSEHOLD_COLLECTION_REF)
        .document(idProvider.getHouseholdId())
        .collection(EVENT_COLLECTION_REF)
    }

    suspend fun createEvent(event: Event): String? {
        val document = collectionRef().document()

        return try {
            document.set(CustomMapper.convertToMap(event.apply { eventId = document.id })).await()
            document.id
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, e.localizedMessage.orEmpty())
            null
        }
    }

    suspend fun updateEvent(event: Event) {
        val document = collectionRef().document(event.eventId)
        try {
            document.update(CustomMapper.convertToMap(event)).await()
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, e.localizedMessage.orEmpty())
        }
    }

    suspend fun getEventsForUser(userId: String): List<Event> {
        if (userId.isBlank()) return emptyList()

        return try {
            collectionRef()
                .whereEqualTo("$EVENT_USER_REF.$USER_ID_REF", userId)
                .get()
                .await()
                .toObjects(EventJson::class.java)
                .mapNotNull { CustomMapper.toEvent(it) }
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, e.localizedMessage.orEmpty())
            emptyList()
        }
    }

    suspend fun getAllEventsQuery(): Query {
        return collectionRef()
            .whereEqualTo(EVENT_HOUSEHOLD_ID_REF, idProvider.getHouseholdId())
            .orderBy("$EVENT_META_DATA_REF.$EVENT_DATE_TIME_REF", Query.Direction.ASCENDING)
    }

    suspend fun updateEvents(events: List<Event>) {
        try {
            val batch = FirebaseFirestore.getInstance().batch()
            events.forEach {
                batch.update(collectionRef().document(it.eventId), CustomMapper.convertToMap(it))
            }
            batch.commit().await()
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, e.localizedMessage.orEmpty())
        }
    }

    suspend fun deleteEvent(eventId: String) {
        try {
            collectionRef()
                .document(eventId)
                .delete()
                .await()
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, e.localizedMessage.orEmpty())
        }
    }

    suspend fun deleteEvents(events: List<Event>) {
        try {
            val batch = FirebaseFirestore.getInstance().batch()
            events.forEach { batch.delete(collectionRef().document(it.eventId)) }
            batch.commit().await()
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, e.localizedMessage.orEmpty())
        }
    }
}
