package com.timgortworst.roomy.presentation.features.event.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.timgortworst.roomy.R
import com.timgortworst.roomy.data.model.BottomMenuItem
import com.timgortworst.roomy.data.model.Event
import com.timgortworst.roomy.data.model.EventMetaData
import com.timgortworst.roomy.domain.utils.NotificationWorkerBuilder
import com.timgortworst.roomy.domain.utils.RecyclerTouchListener
import com.timgortworst.roomy.presentation.base.customview.BottomSheetMenu
import com.timgortworst.roomy.presentation.features.event.adapter.EventListAdapter
import com.timgortworst.roomy.presentation.features.event.presenter.EventListPresenter
import com.timgortworst.roomy.presentation.features.main.view.MainActivity
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_recycler_view.*
import kotlinx.android.synthetic.main.fragment_recycler_view.view.*
import kotlinx.android.synthetic.main.layout_list_state.view.*
import javax.inject.Inject


class EventListFragment : Fragment(), EventListView {
    private var recyclerView: RecyclerView? = null
    private lateinit var touchListener: RecyclerTouchListener
    private lateinit var activityContext: AppCompatActivity
    private lateinit var eventListAdapter: EventListAdapter
    private lateinit var notificationWorkerBuilder: NotificationWorkerBuilder

    @Inject
    lateinit var presenter: EventListPresenter

    companion object {
        fun newInstance(): EventListFragment {
            return EventListFragment()
        }
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
        activityContext = (activity as? MainActivity) ?: return
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_recycler_view, container, false)
        notificationWorkerBuilder = NotificationWorkerBuilder(activityContext)
        eventListAdapter = EventListAdapter(activityContext)
        recyclerView = view.recycler_view
        view.swipe_container?.isEnabled = false

        touchListener = RecyclerTouchListener(activityContext, recyclerView)
        touchListener
                .setLongClickable(false) { showContextMenuFor(eventListAdapter.getEvent(it)) }
                .setClickable(object : RecyclerTouchListener.OnRowClickListener {
                    override fun onRowClicked(position: Int) {
                        touchListener.openSwipeOptions(position)
                    }

                    override fun onIndependentViewClicked(independentViewID: Int, position: Int) {}
                })
                .setSwipeOptionViews(R.id.task_done)
                .setSwipeable(R.id.rowFG, R.id.rowBG) { viewID, position ->
                    when (viewID) {
                        R.id.task_done -> presenter.markEventAsCompleted(eventListAdapter.getEvent(position))
                    }
                }

        recyclerView?.apply {
            val linearLayoutManager = LinearLayoutManager(activityContext)
            layoutManager = linearLayoutManager
            adapter = eventListAdapter
            addItemDecoration(DividerItemDecoration(context, linearLayoutManager.orientation))
            addOnItemTouchListener(touchListener)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.filter_all -> {
                eventListAdapter.clearFilter()
                true
            }
            R.id.filter_me -> {
                presenter.filterMe(eventListAdapter.filter)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showContextMenuFor(event: Event) {
        var bottomSheetMenu: BottomSheetMenu? = null

        val items = arrayListOf(
                BottomMenuItem(R.drawable.ic_edit, "Edit") {
                    presenter.checkIfUserCanEditEvent(event)
                    bottomSheetMenu?.dismiss()
                },
                BottomMenuItem(R.drawable.ic_delete, "Delete") {
                    presenter.deleteEvent(event)
                    bottomSheetMenu?.dismiss()
                }
        )

        bottomSheetMenu = BottomSheetMenu(activityContext, event.eventCategory.name, items)
        bottomSheetMenu.show()
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

    override fun enqueueOneTimeNotification(eventId: String,
                                            eventMetaData: EventMetaData,
                                            categoryName: String,
                                            userName: String) {
        notificationWorkerBuilder.enqueueOneTimeNotification(
                eventId,
                eventMetaData,
                userName,
                categoryName)
    }

    override fun enqueuePeriodicNotification(eventId: String,
                                             eventMetaData: EventMetaData,
                                             categoryName: String,
                                             userName: String) {
        notificationWorkerBuilder.enqueuePeriodicNotification(
                eventId,
                eventMetaData,
                userName,
                categoryName)
    }

    override fun removePendingNotificationReminder(eventId: String) {
        notificationWorkerBuilder.removePendingNotificationReminder(eventId)
    }
}
