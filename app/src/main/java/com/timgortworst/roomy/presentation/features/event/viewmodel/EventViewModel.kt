package com.timgortworst.roomy.presentation.features.event.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.timgortworst.roomy.data.repository.CustomMapper
import com.timgortworst.roomy.domain.model.Event
import com.timgortworst.roomy.domain.model.firestore.EventJson
import com.timgortworst.roomy.domain.usecase.EventUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EventViewModel(private val eventUseCase: EventUseCase) : ViewModel() {
//    val mediatorLiveData = MediatorLiveData<NetworkResponse>()
//    private var networkResponse : LiveData<NetworkResponse> = fetchEvents()
//
//    init {
//        mediatorLiveData.addSource(networkResponse) { mediatorLiveData.value = it }
//    }
//
//    private fun fetchEvents() = liveData {
//        emit(NetworkResponse.Loading)
//        emitSource(eventUseCase.eventsForHouseholdQuery().asSnapshotLiveData())
//    }
//
//    fun refresh() {
//        mediatorLiveData.removeSource(networkResponse)
//        networkResponse = fetchEvents()
//        mediatorLiveData.addSource(networkResponse) { mediatorLiveData.value = it }
//    }

    suspend fun eventsCompleted(events: List<Event>) = withContext(Dispatchers.IO) {
        eventUseCase.eventsCompleted(events)
    }

    suspend fun deleteEvents(events: List<Event>) = withContext(Dispatchers.IO) {
        eventUseCase.deleteEvents(events)
    }

    fun fetchFireStoreRecyclerOptionsBuilder() = liveData {
        val options = FirestoreRecyclerOptions.Builder<Event>()
            .setQuery(eventUseCase.eventsForHouseholdQuery()) {
                CustomMapper.toEvent(it.toObject(EventJson::class.java)!!)!!
            }
        emit(options)
    }
}