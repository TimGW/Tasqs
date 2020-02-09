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
import com.timgortworst.roomy.data.model.Event
import com.timgortworst.roomy.data.model.EventMetaData
import com.timgortworst.roomy.data.model.User
import com.timgortworst.roomy.data.model.firestore.EventJson
import com.timgortworst.roomy.data.utils.Constants
import com.timgortworst.roomy.data.utils.Constants.EVENT_COLLECTION_REF
import com.timgortworst.roomy.data.utils.Constants.EVENT_DATE_TIME_REF
import com.timgortworst.roomy.data.utils.Constants.EVENT_DESCRIPTION_REF
import com.timgortworst.roomy.data.utils.Constants.EVENT_HOUSEHOLD_ID_REF
import com.timgortworst.roomy.data.utils.Constants.EVENT_INTERVAL_REF
import com.timgortworst.roomy.data.utils.Constants.EVENT_META_DATA_REF
import com.timgortworst.roomy.data.utils.Constants.EVENT_TIME_ZONE_REF
import com.timgortworst.roomy.data.utils.Constants.EVENT_USER_REF
import com.timgortworst.roomy.data.utils.Constants.LOADING_SPINNER_DELAY
import com.timgortworst.roomy.domain.UIState
import com.timgortworst.roomy.domain.Response
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor() {
    private val eventCollectionRef = FirebaseFirestore.getInstance().collection(EVENT_COLLECTION_REF)
    private var registration: ListenerRegistration? = null

    suspend fun createEvent(
            eventDescription: String,
            eventMetaData: EventMetaData,
            user: User,
            householdId: String
    ): String? {
        val document = eventCollectionRef.document()
        val eventFieldMap = mutableMapOf<String, Any>()
        val eventMetaDataMap = mutableMapOf<String, Any>()

        eventMetaDataMap[EVENT_DATE_TIME_REF] = eventMetaData.eventTimestamp.toInstant().toEpochMilli()
        eventMetaDataMap[EVENT_TIME_ZONE_REF] = eventMetaData.eventTimestamp.zone.id
//        eventMetaDataMap[EVENT_INTERVAL_REF] = eventMetaData.eventInterval.name todo

        eventFieldMap[Constants.EVENT_ID_REF] = document.id
        eventFieldMap[EVENT_DESCRIPTION_REF] = eventDescription
        eventFieldMap[EVENT_META_DATA_REF] = eventMetaDataMap
        eventFieldMap[EVENT_USER_REF] = user
        eventFieldMap[EVENT_HOUSEHOLD_ID_REF] = householdId

        return try {
            document.set(eventFieldMap).await()
            document.id
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, e.localizedMessage.orEmpty())
            null
        }
    }

    suspend fun getEventsForUser(userId: String): List<Event> {
        if (userId.isBlank()) return emptyList()

        return try {
            eventCollectionRef
                    .whereEqualTo("user.userId", userId)
                    .get()
                    .await()
                    .toObjects(EventJson::class.java)
                    .map { it.toEvent() }
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, e.localizedMessage.orEmpty())
            emptyList()
        }
    }

    fun listenToEventsForHousehold(householdId: String, remoteApi: UIState<Event>) {
        val handler = Handler()
        val runnable = Runnable { remoteApi.setState(Response.Loading) }
        handler.postDelayed(runnable, LOADING_SPINNER_DELAY)

        registration = eventCollectionRef
                .whereEqualTo(EVENT_HOUSEHOLD_ID_REF, householdId)
                .addSnapshotListener(MetadataChanges.INCLUDE, EventListener<QuerySnapshot> { snapshots, e ->
                    handler.removeCallbacks(runnable)
                    Log.d(TAG, "isFromCache: ${snapshots?.metadata?.isFromCache}")
                    val result = when {
                        e != null && snapshots == null -> {
                            Log.e(TAG, "listen:error", e)
                            Response.Error
                        }
                        else -> {
                            val changeList = snapshots?.documentChanges ?: return@EventListener
                            val result = mutableListOf<Pair<Event, DocumentChange.Type>>()
                            changeList.forEach {
                                result.add(Pair(it.document.toObject(EventJson::class.java).toEvent(), it.type))
                            }

                            Response.HasData(result, snapshots.documents.size, snapshots.metadata.hasPendingWrites())
                        }
                    }
                    remoteApi.setState(result)
                })
    }

    suspend fun updateEvent(
            eventId: String,
            eventDescription: String? = null,
            eventMetaData: EventMetaData? = null,
            user: User? = null,
            householdId: String? = null
    ) {
        if (eventId.isBlank()) return
        val document = eventCollectionRef.document(eventId)

        val eventMetaDataMap = mutableMapOf<String, Any>()
        eventMetaData?.let {
            eventMetaDataMap[EVENT_DATE_TIME_REF] = it.eventTimestamp.toInstant().toEpochMilli()
            eventMetaDataMap[EVENT_TIME_ZONE_REF] = it.eventTimestamp.zone.id
            eventMetaDataMap[EVENT_INTERVAL_REF] = it.eventInterval.toString()
        }

        val eventFieldMap = mutableMapOf<String, Any>()
        eventDescription?.let { eventFieldMap[EVENT_DESCRIPTION_REF] = it }
        user?.let { eventFieldMap[EVENT_USER_REF] = it }
        eventMetaData?.let { eventFieldMap[EVENT_META_DATA_REF] = eventMetaDataMap }
        householdId?.let { eventFieldMap[EVENT_HOUSEHOLD_ID_REF] = it }

        try {
            document.update(eventFieldMap).await()
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, e.localizedMessage.orEmpty())
        }
    }

    suspend fun updateEvents(events: List<Event>) {
        try {
            val batch = FirebaseFirestore.getInstance().batch()
            events.forEach {
                val eventMetaDataMap = mutableMapOf<String, Any>()
                it.eventMetaData.let { metaData ->
                    eventMetaDataMap[EVENT_DATE_TIME_REF] = metaData.eventTimestamp.toInstant().toEpochMilli()
                    eventMetaDataMap[EVENT_TIME_ZONE_REF] = metaData.eventTimestamp.zone.id
//                    eventMetaDataMap[EVENT_INTERVAL_REF] = metaData.eventInterval.name todo
                }
                val eventFieldMap = mutableMapOf<String, Any>()
                eventFieldMap[EVENT_META_DATA_REF] = eventMetaDataMap
                eventFieldMap[EVENT_DESCRIPTION_REF] = it.description
                eventFieldMap[EVENT_USER_REF] = it.user
                eventFieldMap[EVENT_HOUSEHOLD_ID_REF] = it.householdId
                batch.update(eventCollectionRef.document(it.eventId), eventFieldMap)
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
