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
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.roomy.R
import com.timgortworst.roomy.databinding.FragmentRecyclerViewBinding
import com.timgortworst.roomy.domain.model.Event
import com.timgortworst.roomy.domain.model.EventMetaData
import com.timgortworst.roomy.domain.utils.NotificationWorkerBuilder
import com.timgortworst.roomy.presentation.features.event.presenter.EventListPresenter
import com.timgortworst.roomy.presentation.features.event.recyclerview.*
import com.timgortworst.roomy.presentation.features.event.viewmodel.EventViewModel
import com.timgortworst.roomy.presentation.features.main.MainActivity
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class EventListFragment : Fragment(),
    ActionModeCallback.ActionItemListener,
    EventDoneClickListener,
    EventListView,
    AdapterStateListener {

    private lateinit var parentActivity: AppCompatActivity
    private var _binding: FragmentRecyclerViewBinding? = null
    private val binding get() = _binding!!
    private lateinit var eventListAdapter: FirestoreAdapter
    private lateinit var notificationWorkerBuilder: NotificationWorkerBuilder
    private lateinit var tracker: SelectionTracker<String>
    private var actionMode: ActionMode? = null
    private val eventViewModel by viewModel<EventViewModel>()
    private val presenter: EventListPresenter by inject { parametersOf(this) }

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
        parentActivity = (activity as? MainActivity) ?: return
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecyclerViewBinding.inflate(inflater, container, false)

        createFireStoreRvAdapter()

        notificationWorkerBuilder = NotificationWorkerBuilder(parentActivity)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun createFireStoreRvAdapter() = eventViewModel.fetchFireStoreRecyclerOptionsBuilder()
        .observe(viewLifecycleOwner, Observer { networkResponse ->
        networkResponse?.let {
            val options = it.setLifecycleOwner(this).build()
            eventListAdapter = FirestoreAdapter(this,this, options)
            bindRecyclerview()
        }
    })

    private fun bindRecyclerview() {
        binding.recyclerView.apply {
            val linearLayoutManager = LinearLayoutManager(parentActivity)
            layoutManager = linearLayoutManager
            adapter = eventListAdapter
            addItemDecoration(DividerItemDecoration(context, linearLayoutManager.orientation))
            setupSelectionTracker(this)
        }
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
        actionMode = parentActivity.startSupportActionMode(
            ActionModeCallback(
                this@EventListFragment,
                tracker,
                eventListAdapter.snapshots
            )
        )
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
        EventEditActivity.start(parentActivity, selectedEvent)
    }

    override fun onActionItemInfo(selectedEvent: Event) {
        EventInfoActivity.start(parentActivity, selectedEvent)
    }

    override fun onActionItemDone(selectedEvents: List<Event>) {
        eventViewModel.viewModelScope.launch {
            eventViewModel.eventsCompleted(selectedEvents)
        }
    }

    override fun showToast(stringRes: Int) {
        Toast.makeText(parentActivity, getString(stringRes), Toast.LENGTH_LONG).show()
    }

    override fun onEventDoneClicked(
        event: Event,
        position: Int
    ) {
        eventViewModel.viewModelScope.launch {
            eventViewModel.eventsCompleted(listOf(event))
        }
    }

    override fun enqueueNotification(
        eventId: String,
        eventMetaData: EventMetaData,
        eventName: String,
        userName: String
    ) {
        notificationWorkerBuilder.enqueueNotification(
            eventId,
            eventMetaData,
            userName,
            eventName
        )
    }

    override fun removePendingNotificationReminder(eventId: String) {
        notificationWorkerBuilder.removePendingNotificationReminder(eventId)
    }

    private fun askForDeleteDialog(events: List<Event>, mode: ActionMode) =
        MaterialAlertDialogBuilder(parentActivity)
            .setTitle(R.string.delete)
            .setMessage(getString(R.string.delete_dialog_text, events.size))
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton(R.string.delete) { dialog, _ ->
                eventViewModel.viewModelScope.launch {
                    eventViewModel.deleteEvents(events)
                }
                mode.finish()
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .create()


    private fun setMsgView(isVisible: Int, title: Int?, text: Int?) {
        binding.layoutListState.apply {
            title?.let { this.stateTitle.text = parentActivity.getString(it) }
            text?.let { this.stateMessage.text = parentActivity.getString(it) }
            root.visibility = isVisible
        }
    }

    override fun onEmptyState(isVisible: Int) {
        setMsgView(
            isVisible,
            R.string.empty_list_state_title_events,
            R.string.empty_list_state_text_events
        )
    }

    override fun onLoadingState(isVisible: Int) {
        binding.progress.visibility = isVisible
    }

    override fun onErrorState(isVisible: Int, e: FirebaseFirestoreException?) {
        // todo handle specific errors
        setMsgView(
            isVisible,
            R.string.error_list_state_title,
            R.string.error_list_state_text
        )
    }
}
