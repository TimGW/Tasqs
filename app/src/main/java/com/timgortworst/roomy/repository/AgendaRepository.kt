package com.timgortworst.roomy.repository

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentChange.Type.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
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


class AgendaRepository(
        db: FirebaseFirestore,
        val sharedPref: HuishoudGenootSharedPref
) {
    val householdCollectionRef = db.collection(HOUSEHOLD_COLLECTION_REF)
    private var eventListener: ListenerRegistration? = null
    private lateinit var categoryListener: ListenerRegistration

    fun getCategories(onComplete: (MutableList<EventCategory>) -> Unit) {
        val document = householdCollectionRef.document(sharedPref.getActiveHouseholdId())
                .collection(AGENDA_EVENT_CATEGORIES_COLLECTION_REF)

        document.get().addOnSuccessListener {
            onComplete(it.toObjects(EventCategory::class.java))
        }
    }

    fun updateCategory(
            categoryId: String,
            name: String = "",
            description: String = "",
            points: Int = 0) {

        val document = householdCollectionRef.document(sharedPref.getActiveHouseholdId())
                .collection(AGENDA_EVENT_CATEGORIES_COLLECTION_REF).document(categoryId)

        val categoryFieldMap = mutableMapOf<String, Any>()
        categoryFieldMap[EVENT_CATEGORY_ID_REF] = document.id
        if (name.isNotBlank()) categoryFieldMap[EVENT_CATEGORY_NAME_REF] = name
        if (description.isNotBlank()) categoryFieldMap[EVENT_CATEGORY_DESC_REF] = description
        if (points != 0) categoryFieldMap[EVENT_CATEGORY_POINTS_REF] = points

        document.update(categoryFieldMap)
    }

    fun insertCategory(
            name: String = "",
            description: String = "",
            points: Int = 0) {

        val document = householdCollectionRef.document(sharedPref.getActiveHouseholdId())
                .collection(AGENDA_EVENT_CATEGORIES_COLLECTION_REF).document()

        val categoryFieldMap = mutableMapOf<String, Any>()
        categoryFieldMap[EVENT_CATEGORY_ID_REF] = document.id
        if (name.isNotBlank()) categoryFieldMap[EVENT_CATEGORY_NAME_REF] = name
        if (description.isNotBlank()) categoryFieldMap[EVENT_CATEGORY_DESC_REF] = description
        if (points != 0) categoryFieldMap[EVENT_CATEGORY_POINTS_REF] = points

        document.set(categoryFieldMap)
    }

    fun deleteEventCategoryForHousehold(agendaEventCategory: EventCategory): Task<Void> {
        return householdCollectionRef.document(sharedPref.getActiveHouseholdId())
                .collection(AGENDA_EVENT_CATEGORIES_COLLECTION_REF).document(agendaEventCategory.categoryId).delete()
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

    fun getAgendaEvents(onComplete: (MutableList<Event>) -> Unit) {
        if(sharedPref.getActiveHouseholdId().isNotBlank()){
            householdCollectionRef.document(sharedPref.getActiveHouseholdId())
                .collection(AGENDA_EVENTS_COLLECTION_REF).get().addOnSuccessListener {
                    onComplete(it.toObjects(Event::class.java))
                }
        }
    }


    fun insertAgendaEvent(category: EventCategory, user: User, eventMetaData: EventMetaData, isEventDone : Boolean) {
        val document = householdCollectionRef.document(sharedPref.getActiveHouseholdId())
                .collection(AGENDA_EVENTS_COLLECTION_REF).document()

        document.set(Event(document.id, category, user, eventMetaData, isEventDone))
    }

    fun updateAgendaEvent(eventId: String, category: EventCategory? = null, user: User? = null, eventMetaData: EventMetaData? = null, isEventDone : Boolean? = null) {
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

        document.update(eventFieldMap)
    }

    fun removeAgendaEvent(eventId: String) {
        householdCollectionRef
                .document(sharedPref.getActiveHouseholdId())
                .collection(AGENDA_EVENTS_COLLECTION_REF)
                .document(eventId)
                .delete()
    }

    fun listenToEvents(agendaListener: AgendaEventListener) {
        eventListener = householdCollectionRef.document(sharedPref.getActiveHouseholdId())
                .collection(AGENDA_EVENTS_COLLECTION_REF)
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
