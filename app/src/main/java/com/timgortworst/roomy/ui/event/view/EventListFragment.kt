package com.timgortworst.roomy.ui.event.view

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.timgortworst.roomy.R
import com.timgortworst.roomy.customview.BottomSheetMenu
import com.timgortworst.roomy.model.BottomMenuItem
import com.timgortworst.roomy.model.Event
import com.timgortworst.roomy.ui.event.adapter.EventListAdapter
import com.timgortworst.roomy.ui.event.presenter.EventListPresenter
import com.timgortworst.roomy.ui.main.view.MainActivity
import com.timgortworst.roomy.utils.RecyclerTouchListener
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_event_list.*
import javax.inject.Inject


class EventListFragment : androidx.fragment.app.Fragment(), EventListView {
    private lateinit var touchListener: RecyclerTouchListener
    private lateinit var activityContext: AppCompatActivity
    private lateinit var adapter: EventListAdapter

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
        return inflater.inflate(R.layout.fragment_event_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.listenToEvents()

        swipe_container.isEnabled = false

        setupEventListAdapter()
        setupRecyclerContextMenu()
    }

    override fun onResume() {
        super.onResume()
        activityContext.supportActionBar?.title = getString(R.string.schema_toolbar_title)
        activityContext.fab.setOnClickListener { EventEditActivity.start(activityContext) }
        events_agenda.addOnItemTouchListener(touchListener)
    }

    override fun onPause() {
        super.onPause()
        activityContext.fab.setOnClickListener(null)
        presenter.detachEventListener()
    }

    override fun onDestroy() {
        presenter.detachEventListener()
        super.onDestroy()
    }

    private fun setupEventListAdapter() {
        adapter = EventListAdapter(activityContext)
        val layoutManager = LinearLayoutManager(activityContext)
        events_agenda.layoutManager = layoutManager
        val dividerItemDecoration =
            DividerItemDecoration(events_agenda.context, layoutManager.orientation)
        events_agenda.addItemDecoration(dividerItemDecoration)
        events_agenda.adapter = adapter
    }

    private fun setupRecyclerContextMenu() {
        touchListener = RecyclerTouchListener(activityContext, events_agenda)
        touchListener
            .setLongClickable(false) {
                showContextMenuFor(adapter.getEvent(it))
            }
            .setClickable(object : RecyclerTouchListener.OnRowClickListener {
                override fun onRowClicked(position: Int) {
                    touchListener.openSwipeOptions(position)
                }

                override fun onIndependentViewClicked(independentViewID: Int, position: Int) {}
            })
            .setSwipeOptionViews(R.id.task_done)
            .setSwipeable(R.id.rowFG, R.id.rowBG) { viewID, position ->
                when (viewID) {
                    R.id.task_done -> {
                        presenter.markEventAsCompleted(adapter.getEvent(position))
                    }
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.filter_all -> {
                adapter.clearFilter()
                true
            }
            R.id.filter_me -> {
                presenter.filterMe(adapter.filter)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showContextMenuFor(event: Event) {
        var bottomSheetMenu: BottomSheetMenu? = null

        val items = arrayListOf(
            BottomMenuItem(R.drawable.ic_edit, "Edit") {
                EventEditActivity.start(activityContext, event)
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
        adapter.addEvent(agendaEvent)
    }

    override fun presentEditedEvent(agendaEvent: Event) {
        adapter.updateEvent(agendaEvent)
    }

    override fun presentDeletedEvent(agendaEvent: Event) {
        adapter.removeEvent(agendaEvent)
    }
}
