package com.timgortworst.roomy.repository

import android.util.Log
import com.google.firebase.firestore.*
import com.google.firebase.firestore.DocumentChange.Type.*
import com.timgortworst.roomy.local.HuishoudGenootSharedPref
import com.timgortworst.roomy.model.Event
import com.timgortworst.roomy.model.EventCategory
import com.timgortworst.roomy.model.EventMetaData
import com.timgortworst.roomy.model.User
import com.timgortworst.roomy.utils.Constants.AGENDA_EVENTS_COLLECTION_REF
import com.timgortworst.roomy.utils.Constants.AGENDA_EVENT_CATEGORIES_COLLECTION_REF
import com.timgortworst.roomy.utils.Constants.EVENT_CATEGORY_DESC_REF
import com.timgortworst.roomy.utils.Constants.EVENT_CATEGORY_ID_REF
import com.timgortworst.roomy.utils.Constants.EVENT_CATEGORY_NAME_REF
import com.timgortworst.roomy.utils.Constants.EVENT_CATEGORY_POINTS_REF
import com.timgortworst.roomy.utils.Constants.EVENT_CATEGORY_REF
import com.timgortworst.roomy.utils.Constants.EVENT_INTERVAL_REF
import com.timgortworst.roomy.utils.Constants.EVENT_IS_DONE_REF
import com.timgortworst.roomy.utils.Constants.EVENT_META_DATA_REF
import com.timgortworst.roomy.utils.Constants.EVENT_START_DATE_REF
import com.timgortworst.roomy.utils.Constants.EVENT_USER_REF
import com.timgortworst.roomy.utils.Constants.HOUSEHOLD_COLLECTION_REF
import kotlinx.coroutines.tasks.await


class AgendaRepository(
    db: FirebaseFirestore,
    val sharedPref: HuishoudGenootSharedPref
) {
    val householdCollectionRef = db.collection(HOUSEHOLD_COLLECTION_REF)
    private var eventListener: ListenerRegistration? = null
    private lateinit var categoryListener: ListenerRegistration

    suspend fun getCategories(): List<EventCategory> {
        val document = householdCollectionRef.document(sharedPref.getActiveHouseholdId())
            .collection(AGENDA_EVENT_CATEGORIES_COLLECTION_REF)
        return document.get().await().toObjects(EventCategory::class.java)
    }

    suspend fun updateCategory(
        categoryId: String,
        name: String = "",
        description: String = ""
        //,points: Int = 0
    ) {
        val document = householdCollectionRef.document(sharedPref.getActiveHouseholdId())
            .collection(AGENDA_EVENT_CATEGORIES_COLLECTION_REF).document(categoryId)

        val categoryFieldMap = mutableMapOf<String, Any>()
        categoryFieldMap[EVENT_CATEGORY_ID_REF] = document.id
        if (name.isNotBlank()) categoryFieldMap[EVENT_CATEGORY_NAME_REF] = name
        if (description.isNotBlank()) categoryFieldMap[EVENT_CATEGORY_DESC_REF] = description
       // if (points != 0) categoryFieldMap[EVENT_CATEGORY_POINTS_REF] = points

        try {
            document.update(categoryFieldMap).await()
        } catch (e: FirebaseFirestoreException) {
            Log.e("TIMTIM", e.localizedMessage)
        }
    }

    suspend fun insertCategory(
        name: String = "",
        description: String = ""
        //,points: Int = 0
    ) {
        val document = householdCollectionRef.document(sharedPref.getActiveHouseholdId())
            .collection(AGENDA_EVENT_CATEGORIES_COLLECTION_REF).document()

        val categoryFieldMap = mutableMapOf<String, Any>()
        categoryFieldMap[EVENT_CATEGORY_ID_REF] = document.id
        if (name.isNotBlank()) categoryFieldMap[EVENT_CATEGORY_NAME_REF] = name
        if (description.isNotBlank()) categoryFieldMap[EVENT_CATEGORY_DESC_REF] = description
        //if (points != 0) categoryFieldMap[EVENT_CATEGORY_POINTS_REF] = points

        try {
            document.set(categoryFieldMap).await()
        } catch (e: FirebaseFirestoreException) {
            Log.e("TIMTIM", e.localizedMessage)
        }
    }

    suspend fun deleteEventCategoryForHousehold(agendaEventCategory: EventCategory) {
        try {
            householdCollectionRef.document(sharedPref.getActiveHouseholdId())
                .collection(AGENDA_EVENT_CATEGORIES_COLLECTION_REF)
                .document(agendaEventCategory.categoryId)
                .delete()
                .await()
        } catch (e: FirebaseFirestoreException) {
            Log.e("TIMTIM", e.localizedMessage)
        }
    }

    suspend fun getAgendaEvents(): MutableList<Event>? {
        return try {
            householdCollectionRef.document(sharedPref.getActiveHouseholdId())
                .collection(AGENDA_EVENTS_COLLECTION_REF)
                .orderBy("eventMetaData.repeatStartDate", Query.Direction.ASCENDING)
                .get()
                .await()
                .toObjects(Event::class.java)
        } catch (e: FirebaseFirestoreException) {
            throw e
        }
    }


    suspend fun insertAgendaEvent(
        category: EventCategory,
        user: User,
        eventMetaData: EventMetaData,
        isEventDone: Boolean
    ) {
        val document = householdCollectionRef.document(sharedPref.getActiveHouseholdId())
            .collection(AGENDA_EVENTS_COLLECTION_REF)
            .document()

        try {
            document.set(Event(document.id, category, user, eventMetaData, isEventDone)).await()
        } catch (e: FirebaseFirestoreException) {
            Log.e("TIMTIM", e.localizedMessage)
        }
    }

    suspend fun updateAgendaEvent(
        eventId: String,
        category: EventCategory? = null,
        user: User? = null,
        eventMetaData: EventMetaData? = null,
        isEventDone: Boolean? = null
    ) {
        val document = householdCollectionRef.document(sharedPref.getActiveHouseholdId())
            .collection(AGENDA_EVENTS_COLLECTION_REF).document(eventId)

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
            Log.e("TIMTIM", e.localizedMessage)
        }
    }

    suspend fun removeAgendaEvent(eventId: String) {
        try {
            householdCollectionRef
                .document(sharedPref.getActiveHouseholdId())
                .collection(AGENDA_EVENTS_COLLECTION_REF)
                .document(eventId)
                .delete()
                .await()
        } catch (e: FirebaseFirestoreException) {
            Log.e("TIMTIM", e.localizedMessage)
        }
    }

    fun listenToCategories(taskListener: EventCategoryListener) {
        categoryListener = householdCollectionRef.document(sharedPref.getActiveHouseholdId())
            .collection(AGENDA_EVENT_CATEGORIES_COLLECTION_REF)
            .addSnapshotListener(EventListener<QuerySnapshot> { snapshots, e ->
                if (e != null) {
                    Log.w(TAG, "listen:error", e)
                    return@EventListener
                }

                for (dc in snapshots!!.documentChanges) {
                    val eventCategory = dc.document.toObject(EventCategory::class.java)
                    when (dc.type) {
                        ADDED -> {
                            taskListener.categoryAdded(eventCategory)
                        }
                        MODIFIED -> {
                            taskListener.categoryModified(eventCategory)
                        }
                        REMOVED -> {
                            taskListener.categoryDeleted(eventCategory)
                        }
                    }
                }
            })
    }

    fun listenToEvents(agendaListener: AgendaEventListener) {
        eventListener = householdCollectionRef.document(sharedPref.getActiveHouseholdId())
            .collection(AGENDA_EVENTS_COLLECTION_REF)
            .orderBy("eventMetaData.repeatStartDate", Query.Direction.ASCENDING)
            .addSnapshotListener(EventListener<QuerySnapshot> { snapshots, e ->
                if (e != null) {
                    Log.w(TAG, "listen:error", e)
                    return@EventListener
                }

                for (dc in snapshots!!.documentChanges) {
                    val agendaEvent = dc.document.toObject(Event::class.java)
                    when (dc.type) {
                        ADDED -> {
                            agendaListener.eventAdded(agendaEvent)
                        }
                        MODIFIED -> {
                            agendaListener.eventModified(agendaEvent)
                        }
                        REMOVED -> {
                            agendaListener.eventDeleted(agendaEvent)
                        }
                    }
                }
            })
    }

    fun detachTaskListener() {
        categoryListener.remove()
    }

    fun detachEventListener() {
        eventListener?.remove()
    }

    companion object {
        private const val TAG = "EventCategoryRepository"
    }

    interface AgendaEventListener {
        fun eventAdded(agendaEvent: Event)
        fun eventModified(agendaEvent: Event)
        fun eventDeleted(agendaEvent: Event)
    }

    interface EventCategoryListener {
        fun categoryAdded(agendaEventCategory: EventCategory)
        fun categoryModified(agendaEventCategory: EventCategory)
        fun categoryDeleted(agendaEventCategory: EventCategory)
    }
}
