package com.timgortworst.roomy.domain.usecase

import com.timgortworst.roomy.data.repository.EventRepository
import com.timgortworst.roomy.data.repository.IdProvider
import com.timgortworst.roomy.data.repository.UserRepository
import com.timgortworst.roomy.domain.model.Event
import com.timgortworst.roomy.domain.model.EventMetaData
import com.timgortworst.roomy.domain.model.EventRecurrence
import com.timgortworst.roomy.domain.utils.TimeOperations
import org.threeten.bp.LocalTime
import org.threeten.bp.ZonedDateTime

class EventUseCase(
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository,
    private val timeOperations: TimeOperations,
    private val idProvider: IdProvider
) {
    suspend fun getAllEventsQuery() = eventRepository.getAllEventsQuery()

    suspend fun getAllUsers() = userRepository.getAllUsers()

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

    private fun calcNextEventDate(eventMetaData: EventMetaData): ZonedDateTime {
        return if (eventMetaData.startDateTime.isBefore(ZonedDateTime.now())) {
            val noon = ZonedDateTime.now().with(LocalTime.NOON)
            timeOperations.nextEvent(noon, eventMetaData.recurrence)
        } else {
            timeOperations.nextEvent(eventMetaData.startDateTime, eventMetaData.recurrence)
        }
    }

    suspend fun createOrUpdateEvent(event: Event) {
        if (event.eventId.isNotEmpty()) {
            eventRepository.updateEvent(event.apply {
                this.householdId = idProvider.getHouseholdId()
            })
        } else {
            eventRepository.createEvent(event.apply {
                this.householdId = idProvider.getHouseholdId()
            })
        }
    }

    suspend fun updateEventsForUser(userId: String?, name: String, email: String) {
        userId?.let { id ->
            eventRepository.getEventsForUser(id).forEach {
                it.user.name = name
                it.user.email = email
                eventRepository.updateEvent(it)
            }
        }
    }
}
