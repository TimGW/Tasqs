package com.timgortworst.roomy.ui.eventcategory.view

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration
import com.timgortworst.roomy.R
import com.timgortworst.roomy.model.BottomMenuItem
import com.timgortworst.roomy.model.EventCategory
import com.timgortworst.roomy.ui.customview.BottomSheetMenu
import com.timgortworst.roomy.ui.eventcategory.adapter.EventCategoryAdapter
import com.timgortworst.roomy.ui.eventcategory.presenter.EventCategoryPresenter
import com.timgortworst.roomy.ui.main.view.MainActivity
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_household_tasks.*
import javax.inject.Inject


class EventCategoryFragment : Fragment(), EventCategoryFragmentView {
    private lateinit var activityContext: MainActivity
    private lateinit var adapter: EventCategoryAdapter

    @Inject
    lateinit var presenter: EventCategoryPresenter

    companion object {
        private val TAG = EventCategoryFragment::class.java.simpleName

        fun newInstance(): EventCategoryFragment {
            return EventCategoryFragment()
        }
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityContext = (activity as MainActivity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_household_tasks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.listenToTasks()

        adapter = EventCategoryAdapter(activityContext, mutableListOf(),
            object : EventCategoryAdapter.OnOptionsClickListener {
                override fun onOptionsClick(householdTask: EventCategory) {
                    showContextMenuFor(householdTask)
                }
            })
        val layoutManager = LinearLayoutManager(activityContext)
        household_task_list.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(household_task_list.context, layoutManager.orientation)
        household_task_list.addItemDecoration(dividerItemDecoration)
        household_task_list.addItemDecoration(StickyRecyclerHeadersDecoration(adapter))
        household_task_list.adapter = adapter
    }

    fun showContextMenuFor(householdTask: EventCategory) {
        var bottomSheetMenu: BottomSheetMenu? = null

        val items = arrayListOf(
            BottomMenuItem(R.drawable.ic_edit, "Edit") {
                EditEventCategoryActivity.start(activityContext, householdTask)
                bottomSheetMenu?.dismiss()
            },
            BottomMenuItem(R.drawable.ic_delete, "Delete") {
                presenter.deleteEventCategory(householdTask)
                bottomSheetMenu?.dismiss()
            }
        )
        bottomSheetMenu = BottomSheetMenu(activityContext, householdTask.name, items)
        bottomSheetMenu.show()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activityContext.supportActionBar?.title = getString(R.string.householdtasks_toolbar_title)
        activityContext.fab.setOnClickListener { EditEventCategoryActivity.start(activityContext) }
    }

    override fun onDetach() {
        super.onDetach()
        presenter.detachTaskListener()
        activityContext.fab.setOnClickListener { null }
    }

    override fun presentNewCategory(householdTask: EventCategory) {
        adapter.insertItem(householdTask)
    }

    override fun presentEditedCategory(householdTask: EventCategory) {
        adapter.editItem(householdTask)
    }

    override fun presentDeletedCategory(householdTask: EventCategory) {
        adapter.removeItem(householdTask)
    }
}