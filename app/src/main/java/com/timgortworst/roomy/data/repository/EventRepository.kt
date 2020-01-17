package com.timgortworst.roomy.data.repository

import android.os.Handler
import android.util.Log
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.QuerySnapshot
import com.timgortworst.roomy.data.model.Category
import com.timgortworst.roomy.data.model.Event
import com.timgortworst.roomy.data.model.EventMetaData
import com.timgortworst.roomy.data.model.User
import com.timgortworst.roomy.data.utils.Constants.EVENT_CATEGORY_REF
import com.timgortworst.roomy.data.utils.Constants.EVENT_COLLECTION_REF
import com.timgortworst.roomy.data.utils.Constants.EVENT_HOUSEHOLD_ID_REF
import com.timgortworst.roomy.data.utils.Constants.EVENT_INTERVAL_REF
import com.timgortworst.roomy.data.utils.Constants.EVENT_META_DATA_REF
import com.timgortworst.roomy.data.utils.Constants.EVENT_START_DATE_REF
import com.timgortworst.roomy.data.utils.Constants.EVENT_USER_REF
import com.timgortworst.roomy.data.utils.Constants.LOADING_SPINNER_DELAY
import com.timgortworst.roomy.domain.ApiStatus
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class EventRepository @Inject constructor() {
    private val eventCollectionRef = FirebaseFirestore.getInstance().collection(EVENT_COLLECTION_REF)
    private var registration: ListenerRegistration? = null

    suspend fun createEvent(
            eventMetaData: EventMetaData,
            category: Category,
            user: User,
            householdId: String
    ) : String {
        val document = eventCollectionRef.document()
        return try {
            document.set(Event(document.id, eventMetaData, category, user, householdId)).await()
            document.id
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, e.localizedMessage.orEmpty())
            ""
        }
    }

    suspend fun getEventsForUser(userId: String): MutableList<Event> {
        if (userId.isBlank()) return mutableListOf()

        return try {
            eventCollectionRef.whereEqualTo("user.userId", userId).get().await().toObjects(Event::class.java)
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, e.localizedMessage.orEmpty())
            mutableListOf()
        }
    }

    fun listenToEventsForHousehold(householdId: String, apiStatus: ApiStatus) {
        val handler = Handler()
        val runnable = Runnable { apiStatus.setState(ApiStatus.Response.Loading) }
        handler.postDelayed(runnable, LOADING_SPINNER_DELAY)

        registration = eventCollectionRef
                .whereEqualTo(EVENT_HOUSEHOLD_ID_REF, householdId)
                .addSnapshotListener(MetadataChanges.INCLUDE, EventListener<QuerySnapshot> { snapshots, e ->
                    handler.removeCallbacks(runnable)
                    Log.d(TAG, "isFromCache: ${snapshots?.metadata?.isFromCache}")
                    when {
                        e != null && snapshots == null -> {
                            apiStatus.setState(ApiStatus.Response.Error(e))
                            Log.w(TAG, "listen:error", e)
                        }
                        else -> {
                            val changeList = snapshots?.documentChanges?.toList() ?: return@EventListener
                            val totalDataSetSize = snapshots.documents.toList().size
                            // todo parse objects here
                            apiStatus.setState(ApiStatus.Response.Success(changeList, totalDataSetSize, snapshots.metadata.hasPendingWrites()))
                        }
                    }
                })
    }

    suspend fun updateEvent(
            eventId: String,
            eventMetaData: EventMetaData? = null,
            category: Category? = null,
            user: User? = null,
            householdId: String? = null
    ) {
        if (eventId.isBlank()) return
        val document = eventCollectionRef.document(eventId)

        val eventMetaDataMap = mutableMapOf<String, Any>()
        eventMetaData?.let {
            eventMetaDataMap[EVENT_START_DATE_REF] = it.eventTimestamp
            eventMetaDataMap[EVENT_INTERVAL_REF] = it.eventInterval.name
        }

        val eventFieldMap = mutableMapOf<String, Any>()
        category?.let { eventFieldMap[EVENT_CATEGORY_REF] = it }
        user?.let { eventFieldMap[EVENT_USER_REF] = it }
        eventMetaData?.let { eventFieldMap[EVENT_META_DATA_REF] = it }
        householdId?.let { eventFieldMap[EVENT_HOUSEHOLD_ID_REF] = it }

        try {
            document.update(eventFieldMap).await()
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

    fun detachEventListener() {
        registration?.remove()
    }

//    fun Timestamp.toZonedDateTime(zone: ZoneId = ZoneId.systemDefault()): ZonedDateTime {
//        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(seconds * 1000 + nanoseconds / 1000000), zone)
//    }
//
//    fun ZonedDateTime.toTimestamp() = Timestamp(second.toLong(), nano)

    companion object {
        private const val TAG = "EventRepository"
    }
}
