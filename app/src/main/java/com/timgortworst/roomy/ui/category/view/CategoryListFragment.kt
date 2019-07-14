package com.timgortworst.roomy.ui.category.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration
import com.timgortworst.roomy.R
import com.timgortworst.roomy.model.BottomMenuItem
import com.timgortworst.roomy.model.EventCategory
import com.timgortworst.roomy.ui.category.adapter.CategoryListAdapter
import com.timgortworst.roomy.ui.category.presenter.CategoryListPresenter
import com.timgortworst.roomy.ui.customview.BottomSheetMenu
import com.timgortworst.roomy.ui.main.view.MainActivity
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_household_tasks.*
import javax.inject.Inject


class CategoryListFragment : androidx.fragment.app.Fragment(), CategoryListView {
    private lateinit var activityContext: MainActivity
    private lateinit var adapter: CategoryListAdapter

    @Inject
    lateinit var presenter: CategoryListPresenter

    companion object {
        private val TAG = CategoryListFragment::class.java.simpleName

        fun newInstance(): CategoryListFragment {
            return CategoryListFragment()
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

        adapter = CategoryListAdapter(activityContext, mutableListOf(),
            object : CategoryListAdapter.OnOptionsClickListener {
                override fun onOptionsClick(householdTask: EventCategory) {
                    showContextMenuFor(householdTask)
                }
            })
        val layoutManager = LinearLayoutManager(activityContext)
        household_task_list.layoutManager = layoutManager
        household_task_list.addItemDecoration(StickyRecyclerHeadersDecoration(adapter))
        household_task_list.addItemDecoration(
            DividerItemDecoration(activityContext, layoutManager.orientation)
        )
        household_task_list.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        activityContext.supportActionBar?.title = getString(R.string.householdtasks_toolbar_title)
        activityContext.fab.setOnClickListener {
            CategoryEditActivity.start(activityContext)
        }
    }

    override fun onPause() {
        super.onPause()
        activityContext.fab.setOnClickListener(null)
    }

    override fun onDetach() {
        super.onDetach()
        presenter.detachTaskListener()
    }

    fun showContextMenuFor(householdTask: EventCategory) {
        var bottomSheetMenu: BottomSheetMenu? = null

        val items = arrayListOf(
            BottomMenuItem(R.drawable.ic_edit, "Edit") {
                CategoryEditActivity.start(activityContext, householdTask)
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