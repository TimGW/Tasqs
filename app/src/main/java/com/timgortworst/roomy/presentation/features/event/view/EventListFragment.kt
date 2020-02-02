package com.timgortworst.roomy.presentation.features.event.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.fragment.app.Fragment
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.timgortworst.roomy.R
import com.timgortworst.roomy.data.model.Event
import com.timgortworst.roomy.data.model.EventMetaData
import com.timgortworst.roomy.domain.utils.NotificationWorkerBuilder
import com.timgortworst.roomy.presentation.features.event.adapter.ActionModeCallback
import com.timgortworst.roomy.presentation.features.event.adapter.EventItemDetailsLookup
import com.timgortworst.roomy.presentation.features.event.adapter.EventItemKeyProvider
import com.timgortworst.roomy.presentation.features.event.adapter.EventListAdapter
import com.timgortworst.roomy.presentation.features.event.presenter.EventListPresenter
import com.timgortworst.roomy.presentation.features.main.view.MainActivity
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_recycler_view.*
import kotlinx.android.synthetic.main.fragment_recycler_view.view.*
import kotlinx.android.synthetic.main.layout_list_state.view.*
import javax.inject.Inject

class EventListFragment : Fragment(), EventListView, ActionModeCallback.ActionItemListener {
    private lateinit var activityContext: AppCompatActivity
    private lateinit var eventListAdapter: EventListAdapter
    private lateinit var notificationWorkerBuilder: NotificationWorkerBuilder
    private lateinit var tracker: SelectionTracker<Event>
    private var actionMode: ActionMode? = null

    @Inject
    lateinit var presenter: EventListPresenter

    companion object {
        const val EVENT_SELECTION_ID = "Event-selection"
        const val IS_IN_ACTION_MODE_KEY = "ActionMode"

        fun newInstance(): EventListFragment {
            return EventListFragment()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        savedInstanceState?.let {
            tracker.onRestoreInstanceState(it)
            if (it.getBoolean(IS_IN_ACTION_MODE_KEY, false)) startActionMode(tracker)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        tracker.onSaveInstanceState(outState)
        outState.putBoolean(IS_IN_ACTION_MODE_KEY, actionMode != null)
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
        activityContext = (activity as? MainActivity) ?: return
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_recycler_view, container, false)
        notificationWorkerBuilder = NotificationWorkerBuilder(activityContext)
        eventListAdapter = EventListAdapter()
        view.swipe_container?.isEnabled = false

        view.recycler_view.apply {
            val linearLayoutManager = LinearLayoutManager(activityContext)
            layoutManager = linearLayoutManager
            adapter = eventListAdapter
            addItemDecoration(DividerItemDecoration(context, linearLayoutManager.orientation))
            setupSelectionTracker(this)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.listenToEvents()
    }

    override fun onDestroy() {
        presenter.detachEventListener()
        super.onDestroy()
    }

    private fun setupSelectionTracker(recyclerView: RecyclerView) {
        tracker = SelectionTracker.Builder<Event>(
                EVENT_SELECTION_ID,
                recyclerView,
                EventItemKeyProvider(recyclerView.adapter),
                EventItemDetailsLookup(recyclerView),
                StorageStrategy.createParcelableStorage(Event::class.java)
        ).withSelectionPredicate(
                SelectionPredicates.createSelectAnything()
        ).withOnDragInitiatedListener {
            true
        }.build()

        eventListAdapter.tracker = tracker

        tracker.addObserver(object : SelectionTracker.SelectionObserver<Event>() {
            override fun onSelectionChanged() {
                super.onSelectionChanged()
                presenter.onSelectionChanged(tracker, actionMode)
            }
        })
    }

    override fun startActionMode(tracker: SelectionTracker<Event>) {
        actionMode = activityContext.startSupportActionMode(ActionModeCallback(this@EventListFragment, tracker))
    }

    override fun stopActionMode() {
        actionMode?.finish()
        actionMode = null
    }

    override fun invalidateActionMode() {
        actionMode?.invalidate()
    }

    override fun setActionModeTitle(size: Int) {
        actionMode?.title = activityContext.getString(R.string.action_mode_title, size)
    }

    override fun onActionItemDelete(selectedEvents: List<Event>) {
        presenter.deleteEvents(selectedEvents)
    }

    override fun onActionItemEdit(selectedEvents: List<Event>) {
        presenter.checkIfUserCanEditEvent(selectedEvents.first())
    }

    override fun presentAddedEvent(agendaEvent: Event) {
        eventListAdapter.addEvent(agendaEvent)
    }

    override fun presentEditedEvent(agendaEvent: Event) {
        eventListAdapter.updateEvent(agendaEvent)
    }

    override fun presentDeletedEvent(agendaEvent: Event) {
        eventListAdapter.removeEvent(agendaEvent)
    }

    override fun setLoadingView(isLoading: Boolean) {
        swipe_container?.isRefreshing = isLoading
    }

    override fun presentEmptyView(isVisible: Boolean) {
        layout_list_state_empty?.apply {
            this.state_title.text = activity?.getString(R.string.empty_list_state_title_events)
            this.state_message.text = activity?.getString(R.string.empty_list_state_text_events)
            visibility = if (isVisible) View.VISIBLE else View.GONE
        }
    }

    override fun setErrorView(isVisible: Boolean, title: Int?, text: Int?) {
        layout_list_state_error?.apply {
            title?.let { this.state_title.text = activityContext.getString(it) }
            text?.let { this.state_message.text = activityContext.getString(it) }
            visibility = if (isVisible) View.VISIBLE else View.GONE
        }
    }

    override fun showToast(stringRes: Int) {
        Toast.makeText(activityContext, getString(stringRes), Toast.LENGTH_LONG).show()
    }

    override fun openEventEditActivity(event: Event) {
        EventEditActivity.start(activityContext, event)
    }

    override fun enqueueNotification(eventId: String,
                                     eventMetaData: EventMetaData,
                                     categoryName: String,
                                     userName: String) {
        notificationWorkerBuilder.enqueueNotification(
                eventId,
                eventMetaData,
                userName,
                categoryName)
    }

    override fun removePendingNotificationReminder(eventId: String) {
        notificationWorkerBuilder.removePendingNotificationReminder(eventId)
    }
}
