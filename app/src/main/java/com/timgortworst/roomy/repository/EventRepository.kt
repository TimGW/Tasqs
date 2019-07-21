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
import com.timgortworst.roomy.utils.Constants.EVENT_INTERVAL_REF
import com.timgortworst.roomy.utils.Constants.EVENT_IS_DONE_REF
import com.timgortworst.roomy.utils.Constants.EVENT_META_DATA_REF
import com.timgortworst.roomy.utils.Constants.EVENT_START_DATE_REF
import com.timgortworst.roomy.utils.Constants.EVENT_USER_REF
import com.timgortworst.roomy.utils.Constants.HOUSEHOLD_COLLECTION_REF
import kotlinx.coroutines.tasks.await

class EventRepository(val userRepository: UserRepository) {
    private val householdCollectionRef = FirebaseFirestore.getInstance().collection(HOUSEHOLD_COLLECTION_REF)
    private var registration: ListenerRegistration? = null

    suspend fun insertEvent(
        category: Category,
        user: User,
        eventMetaData: EventMetaData,
        isEventDone: Boolean
    ) {
        val document = householdCollectionRef.document(userRepository.getHouseholdIdForCurrentUser())
            .collection(EVENT_COLLECTION_REF)
            .document()

        try {
            document.set(Event(document.id, category, user, eventMetaData, isEventDone)).await()
        } catch (e: FirebaseFirestoreException) {
            Log.e("TIMTIM", e.localizedMessage!!)
        }
    }

    suspend fun updateEvent(
        eventId: String,
        category: Category? = null,
        user: User? = null,
        eventMetaData: EventMetaData? = null,
        isEventDone: Boolean? = null
    ) {
        val document = householdCollectionRef.document(userRepository.getHouseholdIdForCurrentUser())
            .collection(EVENT_COLLECTION_REF).document(eventId)

        val eventMetaDataMap = mutableMapOf<String, Any>()
        if (eventMetaData != null) eventMetaDataMap[EVENT_START_DATE_REF] = eventMetaData.repeatStartDate
        if (eventMetaData != null) eventMetaDataMap[EVENT_INTERVAL_REF] = eventMetaData.repeatInterval.name

        val eventFieldMap = mutableMapOf<String, Any>()
        if (category != null) eventFieldMap[EVENT_CATEGORY_REF] = category
        if (user != null) eventFieldMap[EVENT_USER_REF] = user
        if (eventMetaData != null) eventFieldMap[EVENT_META_DATA_REF] = eventMetaDataMap
        if (isEventDone != null) eventFieldMap[EVENT_IS_DONE_REF] = isEventDone

        try {
            document.update(eventFieldMap).await()
        } catch (e: FirebaseFirestoreException) {
            Log.e("TIMTIM", e.localizedMessage!!)
        }
    }

    suspend fun removeEvent(eventId: String) {
        try {
            householdCollectionRef
                .document(userRepository.getHouseholdIdForCurrentUser())
                .collection(EVENT_COLLECTION_REF)
                .document(eventId)
                .delete()
                .await()
        } catch (e: FirebaseFirestoreException) {
            Log.e("TIMTIM", e.localizedMessage!!)
        }
    }

    suspend fun listenToEvents(eventListener: EventListener) {
        eventListener.setLoading(true)

        val query = householdCollectionRef
            .document(userRepository.getHouseholdIdForCurrentUser())
            .collection(EVENT_COLLECTION_REF)

        query.orderBy("eventMetaData.repeatStartDate", Query.Direction.ASCENDING)

        registration = query.addSnapshotListener(EventListener<QuerySnapshot> { snapshots, e ->
            if (e != null && snapshots == null) {
                eventListener.setLoading(false)
                Log.w(TAG, "listen:error", e)
                return@EventListener
            }

            for (dc in snapshots!!.documentChanges) {
                val agendaEvent = dc.document.toObject(Event::class.java)
                when (dc.type) {
                    ADDED -> eventListener.eventAdded(agendaEvent)
                    MODIFIED -> eventListener.eventModified(agendaEvent)
                    REMOVED -> eventListener.eventDeleted(agendaEvent)
                }
            }
            eventListener.setLoading(false)
        })
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
