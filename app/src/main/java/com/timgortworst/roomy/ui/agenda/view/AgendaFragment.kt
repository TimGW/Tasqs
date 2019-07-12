package com.timgortworst.roomy.ui.agenda.view

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.*
import com.timgortworst.roomy.R
import com.timgortworst.roomy.model.Event
import com.timgortworst.roomy.ui.agenda.adapter.EventListAdapter
import com.timgortworst.roomy.ui.agenda.presenter.AgendaPresenter
import com.timgortworst.roomy.ui.main.view.MainActivity
import com.timgortworst.roomy.ui.settings.view.SettingsActivity
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_agenda.*
import javax.inject.Inject


class AgendaFragment : androidx.fragment.app.Fragment(), AgendaView {
    private lateinit var activityContext: AppCompatActivity
    private var listeningToEvents: Boolean = false
    private lateinit var adapter: EventListAdapter

    @Inject
    lateinit var presenter: AgendaPresenter

    companion object {
        private val TAG = AgendaFragment::class.java.simpleName

        fun newInstance(): AgendaFragment {
            return AgendaFragment()
        }
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityContext = (activity as? MainActivity) ?: return

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_agenda, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupEventListAdapter()

        if (!listeningToEvents) {
            presenter.listenToEvents()
            listeningToEvents = true
        }
    }

    private fun setupEventListAdapter() {
        adapter = EventListAdapter(activityContext, mutableListOf())
        val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activityContext)
        events_agenda.layoutManager = layoutManager
        val dividerItemDecoration =
            androidx.recyclerview.widget.DividerItemDecoration(events_agenda.context, layoutManager.orientation)
        events_agenda.addItemDecoration(dividerItemDecoration)
        events_agenda.adapter = adapter
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
            R.id.toolbar_menu_settings -> {
                SettingsActivity.start(activityContext)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activityContext.supportActionBar?.title = getString(R.string.schema_toolbar_title)
        activityContext.fab.setOnClickListener { EditAgendaEventActivity.start(activityContext) }

    }

    override fun presentAddedEvent(agendaEvent: Event) {
        adapter.addEvent(agendaEvent)
    }

    override fun presentEditedEvent(agendaEvent: Event) {

    }

    override fun presentDeletedEvent(agendaEvent: Event) {

    }

    override fun onDetach() {
        super.onDetach()
        listeningToEvents = false
        presenter.detachEventListener()
        activityContext.fab.setOnClickListener { null }
    }
}