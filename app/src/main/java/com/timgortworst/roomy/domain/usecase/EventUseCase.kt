package com.timgortworst.roomy.domain.usecase

import com.timgortworst.roomy.data.model.Event
import com.timgortworst.roomy.data.model.EventMetaData
import com.timgortworst.roomy.data.model.EventRecurrence
import com.timgortworst.roomy.data.model.User
import com.timgortworst.roomy.data.repository.EventRepository
import com.timgortworst.roomy.data.repository.UserRepository
import com.timgortworst.roomy.domain.utils.isDateInPast
import com.timgortworst.roomy.domain.utils.plusInterval
import com.timgortworst.roomy.presentation.features.event.presenter.EventListPresenter
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
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

    suspend fun deleteEvents(events: List<Event>) {
        eventRepository.deleteEvents(events)
    }

    suspend fun eventsCompleted(events: List<Event>) {
        events.filter {
            it.metaData.recurrence is EventRecurrence.SingleEvent
        }.run {
            deleteEvents(this)
        }

        events.filterNot {
            it.metaData.recurrence is EventRecurrence.SingleEvent
        }.run {
            updateNextEventDate(this)
        }
    }

    private suspend fun updateNextEventDate(events: List<Event>) {
        events.forEach {
            it.metaData.startDateTime = calcNextEventDate(it.metaData)
        }
        eventRepository.updateEvents(events)
    }

    suspend fun getHouseholdIdForUser(): String {
        return userRepository.getHouseholdIdForUser()
    }

    suspend fun getUserListForCurrentHousehold(): List<User>? {
        return userRepository.getUserListForHousehold(userRepository.getHouseholdIdForUser())
    }

    private fun calcNextEventDate(eventMetaData: EventMetaData): ZonedDateTime {
        return if (eventMetaData.startDateTime.isDateInPast()) {
            LocalDate.now().toZonedNoonDateTime().plusInterval(eventMetaData.recurrence)
        } else {
            eventMetaData.startDateTime.plusInterval(eventMetaData.recurrence)
        }
    }

    suspend fun createOrUpdateEvent(event: Event) {
        if (event.eventId.isNotEmpty()) {
            eventRepository.updateEvent(event.apply { householdId = getHouseholdIdForUser() })
        } else {
            eventRepository.createEvent(event.apply { householdId = getHouseholdIdForUser() })
        }
    }

    private fun LocalDate.toZonedNoonDateTime() = LocalDateTime.of(this, LocalTime.NOON).atZone(ZoneId.systemDefault())
}
