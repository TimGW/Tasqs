package com.timgortworst.roomy.domain.usecase

import com.timgortworst.roomy.data.model.Event
import com.timgortworst.roomy.data.model.EventMetaData
import com.timgortworst.roomy.data.model.User
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
            private val userRepository: UserRepository) {

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

    suspend fun deleteEvents(events: List<Event>) {
        eventRepository.deleteEvents(events)
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
                            user: User? = null,
                            eventDescription: String? = null) {
        eventRepository.updateEvent(
                eventId = eventId,
                eventMetaData = eventMetaData,
                user = user,
                eventDescription = eventDescription)
    }

    suspend fun createEvent(eventMetaData: EventMetaData,
                            user: User,
                            householdId: String,
                            eventDescription: String): String? {
        return eventRepository.createEvent(eventDescription, eventMetaData, user, householdId)
    }

    suspend fun getHouseholdIdForUser(): String {
        return userRepository.getHouseholdIdForUser()
    }

    suspend fun getUserListForCurrentHousehold(): List<User>? {
        return userRepository.getUserListForHousehold(userRepository.getHouseholdIdForUser())
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
