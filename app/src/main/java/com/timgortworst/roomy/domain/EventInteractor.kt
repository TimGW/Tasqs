package com.timgortworst.roomy.domain

import com.timgortworst.roomy.data.model.Category
import com.timgortworst.roomy.data.model.EventMetaData
import com.timgortworst.roomy.data.model.User
import com.timgortworst.roomy.data.repository.CategoryRepository
import com.timgortworst.roomy.data.repository.EventRepository
import com.timgortworst.roomy.data.repository.UserRepository
import com.timgortworst.roomy.ui.features.event.presenter.EventListPresenter
import javax.inject.Inject

class EventInteractor
@Inject
constructor(private val eventRepository: EventRepository,
            private val userRepository: UserRepository,
            private val categoryRepository: CategoryRepository) {

    suspend fun listenToEvents(eventListPresenter: EventListPresenter) {
        eventRepository.listenToEventsForHousehold(
                userRepository.getHouseholdIdForUser(),
                eventListPresenter)
    }

    fun detachEventListener() {
        eventRepository.detachEventListener()
    }

    fun getCurrentUserId(): String? {
        return userRepository.getCurrentUserId()
    }

    suspend fun deleteEvent(eventId: String) {
        eventRepository.deleteEvent(eventId)
    }

    suspend fun updateEvent(eventId: String,
                            eventMetaData: EventMetaData? = null,
                            category: Category? = null,
                            user: User? = null,
                            householdId: String? = null) {
        eventRepository.updateEvent(eventId, eventMetaData = eventMetaData)
    }

    suspend fun createEvent(eventMetaData: EventMetaData,
                            category: Category,
                            user: User,
                            householdId: String): String {
        return eventRepository.createEvent(eventMetaData, category, user, householdId)
    }

    suspend fun getHouseholdIdForUser(): String {
        return userRepository.getHouseholdIdForUser()
    }

    suspend fun getUserListForCurrentHousehold(): List<User>? {
        return userRepository.getUserListForHousehold(userRepository.getHouseholdIdForUser())
    }

    suspend fun getCategories(): List<Category> {
        return categoryRepository.getCategories()
    }


}
