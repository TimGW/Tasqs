package com.timgortworst.roomy.domain.usecase

import com.timgortworst.roomy.R
import com.timgortworst.roomy.data.model.Event
import com.timgortworst.roomy.data.model.EventInterval
import com.timgortworst.roomy.data.model.EventMetaData
import com.timgortworst.roomy.data.model.User
import com.timgortworst.roomy.data.repository.EventRepository
import com.timgortworst.roomy.data.repository.UserRepository
import com.timgortworst.roomy.domain.utils.isDateInPast
import com.timgortworst.roomy.domain.utils.plusInterval
import com.timgortworst.roomy.domain.utils.toIntOrOne
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
            it.eventMetaData.eventInterval == EventInterval.SingleEvent
        }.run {
            deleteEvents(this)
        }

        events.filterNot {
            it.eventMetaData.eventInterval == EventInterval.SingleEvent
        }.run {
            updateNextEventDate(this)
        }
    }

    private suspend fun updateNextEventDate(events: List<Event>) {
        events.forEach {
            it.eventMetaData.eventTimestamp = calcNextEventDate(it.eventMetaData)
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
        return if (eventMetaData.eventTimestamp.isDateInPast()) {
            LocalDate.now().toZonedNoonDateTime().plusInterval(eventMetaData.eventInterval)
        } else {
            eventMetaData.eventTimestamp.plusInterval(eventMetaData.eventInterval)
        }
    }

    suspend fun createOrUpdateEvent(eventId: String?,
                                    eventDescription: String,
                                    user: User,
                                    selectedDate: LocalDate,
                                    recurrenceFrequency: String,
                                    recurrenceTypeId: Int, selectedWeekDays: List<Int>) {

        val eventMetaData = buildEventMetaData(recurrenceFrequency,
                recurrenceTypeId, selectedWeekDays, selectedDate)

        if (!eventId.isNullOrEmpty()) {
            eventRepository.updateEvent(Event(eventId, eventDescription, eventMetaData, user))
        } else {
            eventRepository.createEvent(
                    Event(
                            description = eventDescription,
                            eventMetaData = eventMetaData,
                            user = user,
                            householdId = getHouseholdIdForUser()
                    )
            )
        }
    }

    private fun buildEventMetaData(recurrenceFrequency: String,
                                   recurrenceTypeId: Int,
                                   selectedWeekDays: List<Int>,
                                   selectedDate: LocalDate): EventMetaData {
        val frequency = recurrenceFrequency.toIntOrOne()

        val eventInterval = when (recurrenceTypeId) {
            R.id.days -> EventInterval.Daily(frequency)
            R.id.weeks -> EventInterval.Weekly(frequency, selectedWeekDays)
            R.id.months -> EventInterval.Monthly(frequency)
            R.id.year -> EventInterval.Annually(frequency)
            else -> EventInterval.SingleEvent
        }

        return EventMetaData(selectedDate.toZonedNoonDateTime(), eventInterval)
    }

    private fun LocalDate.toZonedNoonDateTime() = LocalDateTime.of(this, LocalTime.NOON).atZone(ZoneId.systemDefault())
}
