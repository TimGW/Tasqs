package com.timgortworst.roomy.presentation.features.event.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.timgortworst.roomy.data.repository.CustomMapper
import com.timgortworst.roomy.domain.model.Event
import com.timgortworst.roomy.domain.model.NetworkResponse
import com.timgortworst.roomy.domain.model.firestore.EventJson
import com.timgortworst.roomy.domain.usecase.EventUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EventViewModel(private val eventUseCase: EventUseCase) : ViewModel() {
    private var _events : MutableLiveData<NetworkResponse> = MutableLiveData()
    private var events : LiveData<NetworkResponse> = _events

//
//    val events: LiveData<NetworkResponse> = liveData {
//        emitSource(eventUseCase.eventsForHouseholdQuery().asSnapshotLiveData())
//    }


    // get realtime updates from firebase regarding saved addresses
    suspend fun eventListener(): LiveData<NetworkResponse> {
        _events.value = NetworkResponse.Loading

        eventUseCase.eventsForHouseholdQuery().addSnapshotListener { value, e ->
            if (e != null) {
                _events.value = NetworkResponse.Error(e)
                return@addSnapshotListener
            }

            val savedAddressList : MutableList<Event> = mutableListOf()
            for (doc in value!!) {
                val event = CustomMapper.toEvent(doc.toObject(EventJson::class.java))
                savedAddressList.add(event!!)
            }
            _events.value = NetworkResponse.Success(value)
        }

        return events
    }

    suspend fun eventsCompleted(events: List<Event>) = withContext(Dispatchers.IO) {
        eventUseCase.eventsCompleted(events)
    }

    suspend fun deleteEvents(events: List<Event>) = withContext(Dispatchers.IO) {
        eventUseCase.deleteEvents(events)
    }
}