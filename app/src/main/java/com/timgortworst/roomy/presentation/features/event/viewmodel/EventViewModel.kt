package com.timgortworst.roomy.presentation.features.event.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.timgortworst.roomy.domain.model.Event
import com.timgortworst.roomy.domain.usecase.EventUseCase
import com.timgortworst.roomy.presentation.RoomyApp.Companion.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EventViewModel(private val eventUseCase: EventUseCase) : ViewModel() {

    suspend fun fetchEvents() = withContext(Dispatchers.IO) {
        Log.i(TAG, "fetchEvents")
        return@withContext eventUseCase.getLiveEventData()
    }

    suspend fun eventsCompleted(events: List<Event>) = withContext(Dispatchers.IO) {
        eventUseCase.eventsCompleted(events)
    }

    suspend fun deleteEvents(events: List<Event>) = withContext(Dispatchers.IO) {
        eventUseCase.deleteEvents(events)
    }
}