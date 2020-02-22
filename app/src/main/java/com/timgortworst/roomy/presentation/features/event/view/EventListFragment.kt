package com.timgortworst.roomy.presentation.features.event.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.fragment.app.Fragment
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.timgortworst.roomy.R
import com.timgortworst.roomy.domain.model.Event
import com.timgortworst.roomy.domain.model.EventMetaData
import com.timgortworst.roomy.domain.utils.NotificationWorkerBuilder
import com.timgortworst.roomy.presentation.features.event.adapter.ActionModeCallback
import com.timgortworst.roomy.presentation.features.event.adapter.EventItemDetailsLookup
import com.timgortworst.roomy.presentation.features.event.adapter.EventItemKeyProvider
import com.timgortworst.roomy.presentation.features.event.adapter.EventListAdapter
import com.timgortworst.roomy.presentation.features.event.presenter.EventListPresenter
import com.timgortworst.roomy.presentation.features.main.view.MainActivity
import kotlinx.android.synthetic.main.fragment_recycler_view.*
import kotlinx.android.synthetic.main.fragment_recycler_view.view.*
import kotlinx.android.synthetic.main.layout_list_state.view.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class EventListFragment : Fragment(), EventListView, ActionModeCallback.ActionItemListener, EventListAdapter.EventDoneClickListener {
    private lateinit var activityContext: AppCompatActivity
    private lateinit var eventListAdapter: EventListAdapter
    private lateinit var notificationWorkerBuilder: NotificationWorkerBuilder
    private lateinit var tracker: SelectionTracker<String>
    private var actionMode: ActionMode? = null
    private val presenter: EventListPresenter by inject {
        parametersOf(this)
    }

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
        super.onAttach(context)
        activityContext = (activity as? MainActivity) ?: return
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_recycler_view, container, false)
        notificationWorkerBuilder = NotificationWorkerBuilder(activityContext)
        eventListAdapter = EventListAdapter(this)
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
        tracker = SelectionTracker.Builder<String>(
                EVENT_SELECTION_ID,
                recyclerView,
                EventItemKeyProvider(recyclerView.adapter),
                EventItemDetailsLookup(recyclerView),
                StorageStrategy.createStringStorage()
        ).withSelectionPredicate(
                SelectionPredicates.createSelectAnything()
        ).withOnDragInitiatedListener {
            true
        }.build()

        eventListAdapter.tracker = tracker

        tracker.addObserver(object : SelectionTracker.SelectionObserver<String>() {
            override fun onSelectionChanged() {
                super.onSelectionChanged()
                presenter.onSelectionChanged(tracker, actionMode)
            }
        })
    }

    override fun startActionMode(tracker: SelectionTracker<String>) {
        actionMode = activityContext.startSupportActionMode(
                ActionModeCallback(
                        this@EventListFragment,
                        tracker,
                        eventListAdapter.getEvents()))
    }

    override fun stopActionMode() {
        actionMode?.finish()
        actionMode = null
    }

    override fun invalidateActionMode() {
        actionMode?.invalidate()
    }

    override fun setActionModeTitle(size: Int) {
        actionMode?.apply {
            menu?.findItem(R.id.edit)?.isVisible = size == 1
            menu?.findItem(R.id.info)?.isVisible = size == 1
            title = size.toString()
        }
    }

    override fun onActionItemDelete(selectedEvents: List<Event>, mode: ActionMode) {
        askForDeleteDialog(selectedEvents, mode).show()
    }

    override fun onActionItemEdit(selectedEvent: Event) {
        EventEditActivity.start(activityContext, selectedEvent)
    }

    override fun onActionItemInfo(selectedEvent: Event) {
        EventInfoActivity.start(activityContext, selectedEvent)
    }

    override fun onActionItemDone(selectedEvents: List<Event>) {
        presenter.eventsCompleted(selectedEvents)
    }

    override fun presentAddedEvent(event: Event) {
        eventListAdapter.addEvent(event) //.apply { description = showInfoToast(event) })
    }

    override fun presentEditedEvent(event: Event) {
        eventListAdapter.updateEvent(event)
    }

    override fun presentDeletedEvent(event: Event) {
        eventListAdapter.removeEvent(event)
    }

    override fun setLoadingView(isLoading: Boolean) {
        swipe_container?.isRefreshing = isLoading
    }

    override fun setMsgView(isVisible: Boolean, title: Int?, text: Int?) {
        layout_list_state?.apply {
            title?.let { this.state_title.text = activityContext.getString(it) }
            text?.let { this.state_message.text = activityContext.getString(it) }
            visibility = if (isVisible) View.VISIBLE else View.GONE
        }
    }

    override fun showToast(stringRes: Int) {
        Toast.makeText(activityContext, getString(stringRes), Toast.LENGTH_LONG).show()
    }

    override fun onEventDoneClicked(position: Int) {
        presenter.eventsCompleted(listOf(eventListAdapter.getEvent(position)))
    }

    override fun enqueueNotification(eventId: String,
                                     eventMetaData: EventMetaData,
                                     eventName: String,
                                     userName: String) {
        notificationWorkerBuilder.enqueueNotification(
                eventId,
                eventMetaData,
                userName,
                eventName)
    }

    override fun removePendingNotificationReminder(eventId: String) {
        notificationWorkerBuilder.removePendingNotificationReminder(eventId)
    }

    private fun askForDeleteDialog(events: List<Event>, mode: ActionMode) = MaterialAlertDialogBuilder(activityContext)
            .setTitle(R.string.delete)
            .setMessage(getString(R.string.delete_dialog_text, events.size))
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton(R.string.delete) { dialog, _ ->
                presenter.deleteEvents(events)
                mode.finish()
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .create()
}
