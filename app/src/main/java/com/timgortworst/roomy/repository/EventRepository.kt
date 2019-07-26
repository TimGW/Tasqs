package com.timgortworst.roomy.repository

import android.util.Log
import com.google.firebase.firestore.DocumentChange.Type.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.timgortworst.roomy.model.Category
import com.timgortworst.roomy.model.Event
import com.timgortworst.roomy.model.EventMetaData
import com.timgortworst.roomy.model.User
import com.timgortworst.roomy.utils.Constants.EVENT_CATEGORY_REF
import com.timgortworst.roomy.utils.Constants.EVENT_COLLECTION_REF
import com.timgortworst.roomy.utils.Constants.EVENT_HOUSEHOLD_ID_REF
import com.timgortworst.roomy.utils.Constants.EVENT_INTERVAL_REF
import com.timgortworst.roomy.utils.Constants.EVENT_META_DATA_REF
import com.timgortworst.roomy.utils.Constants.EVENT_START_DATE_REF
import com.timgortworst.roomy.utils.Constants.EVENT_USER_REF
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor() {
    var eventCollectionRef = FirebaseFirestore.getInstance().collection(EVENT_COLLECTION_REF)
        private set

    private var registration: ListenerRegistration? = null

    suspend fun createEvent(
            eventMetaData: EventMetaData,
            category: Category,
            user: User,
            householdId: String
    ) {
        val document = eventCollectionRef.document()
        try {
            document.set(Event(document.id, eventMetaData, category, user, householdId)).await()
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, e.localizedMessage.orEmpty())
        }
    }

    suspend fun getEventsForUser(userId: String): MutableList<Event> {
        return eventCollectionRef.whereEqualTo("user.userId", userId).get().await().toObjects(Event::class.java)
    }

    fun listenToEvents(eventListener: EventListener) {
        eventListener.setLoading(true)

        eventCollectionRef.orderBy("eventMetaData.repeatStartDate", Query.Direction.ASCENDING)

        registration = eventCollectionRef.addSnapshotListener(EventListener<QuerySnapshot> { snapshots, e ->
            if (e != null && snapshots == null) {
                eventListener.setLoading(false)
                Log.w(TAG, "listen:error", e)
                return@EventListener
            }

            for (dc in snapshots!!.documentChanges) {
                val event = dc.document.toObject(Event::class.java)
                when (dc.type) {
                    ADDED -> eventListener.eventAdded(event)
                    MODIFIED -> eventListener.eventModified(event)
                    REMOVED -> eventListener.eventDeleted(event)
                }
            }
            eventListener.setLoading(false)
        })
    }

    suspend fun updateEvent(
            eventId: String,
            eventMetaData: EventMetaData? = null,
            category: Category? = null,
            user: User? = null,
            householdId: String? = null
    ) {
        val document = eventCollectionRef.document(eventId)

        val eventMetaDataMap = mutableMapOf<String, Any>()
        if (eventMetaData != null) eventMetaDataMap[EVENT_START_DATE_REF] = eventMetaData.repeatStartDate
        if (eventMetaData != null) eventMetaDataMap[EVENT_INTERVAL_REF] = eventMetaData.repeatInterval.name

        val eventFieldMap = mutableMapOf<String, Any>()
        if (category != null) eventFieldMap[EVENT_CATEGORY_REF] = category
        if (user != null) eventFieldMap[EVENT_USER_REF] = user
        if (eventMetaData != null) eventFieldMap[EVENT_META_DATA_REF] = eventMetaDataMap
        if (householdId != null) eventFieldMap[EVENT_HOUSEHOLD_ID_REF] = householdId

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
            Log.e(TAG, e.localizedMessage!!)
        }
    }

    fun detachEventListener() {
        registration?.remove()
    }

    companion object {
        private const val TAG = "EventRepository"
    }

    interface EventListener : ObjectStateListener {
        fun eventAdded(event: Event)
        fun eventModified(event: Event)
        fun eventDeleted(event: Event)
    }
}
