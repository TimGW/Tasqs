package com.timgortworst.roomy.domain.usecase

import com.timgortworst.roomy.data.model.Category
import com.timgortworst.roomy.data.model.Event
import com.timgortworst.roomy.data.model.EventMetaData
import com.timgortworst.roomy.data.model.User
import com.timgortworst.roomy.data.repository.CategoryRepository
import com.timgortworst.roomy.data.repository.EventRepository
import com.timgortworst.roomy.data.repository.UserRepository
import com.timgortworst.roomy.data.utils.Constants
import com.timgortworst.roomy.domain.utils.isDateInPast
import com.timgortworst.roomy.domain.utils.plusInterval
import com.timgortworst.roomy.presentation.features.event.presenter.EventListPresenter
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.temporal.ChronoUnit
import javax.inject.Inject

class EventUseCase
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

    suspend fun markEventAsComplete(event: Event) {
        eventRepository.updateEvent(
                eventId = event.eventId,
                eventMetaData = EventMetaData(
                        eventTimestamp = calcNextEventDate(event.eventMetaData),
                        eventInterval = event.eventMetaData.eventInterval))
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
                            householdId: String): String? {
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

    suspend fun isUserAbleToCreateEvent(): Boolean {
        return categoryRepository.getCategories().isNotEmpty()
    }

    private fun calcNextEventDate(eventMetaData: EventMetaData): ZonedDateTime {
        return if (eventMetaData.eventTimestamp.isDateInPast()) {
            todayAtEight().plusInterval(eventMetaData.eventInterval)
        } else {
            eventMetaData.eventTimestamp.plusInterval(eventMetaData.eventInterval)
        }
    }

    private fun todayAtEight() = ZonedDateTime.now().withHour(Constants.DEFAULT_HOUR_OF_DAY_NOTIFICATION).truncatedTo(ChronoUnit.HOURS)
}
