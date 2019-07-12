package com.timgortworst.roomy.ui.agenda.presenter

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.timgortworst.roomy.model.Event
import com.timgortworst.roomy.repository.AgendaRepository
import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.ui.agenda.view.AgendaView
import com.timgortworst.roomy.utils.CoroutineLifecycleScope
import kotlinx.coroutines.Dispatchers
import android.widget.Filter

class AgendaPresenter(
    val view: AgendaView,
    val agendaRepository: AgendaRepository,
    val userRepository: UserRepository
) : AgendaRepository.AgendaEventListener, DefaultLifecycleObserver {

    private val scope = CoroutineLifecycleScope(Dispatchers.Main)

    init {
        if (view is LifecycleOwner) {
            view.lifecycle.addObserver(scope)
        }
    }

    override fun eventAdded(agendaEvent: Event) {
        view.presentAddedEvent(agendaEvent)
    }

    override fun eventModified(agendaEvent: Event) {
        view.presentEditedEvent(agendaEvent)
    }

    override fun eventDeleted(agendaEvent: Event) {
        view.presentDeletedEvent(agendaEvent)
    }

    fun detachEventListener() {
        agendaRepository.detachEventListener()
    }

    fun listenToEvents() {
        agendaRepository.listenToEvents(this)
    }

    fun filterMe(filter: Filter) {
        filter.filter(userRepository.getCurrentUserId())
    }
}
