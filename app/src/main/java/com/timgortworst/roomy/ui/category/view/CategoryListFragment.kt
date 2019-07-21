package com.timgortworst.roomy.ui.category.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration
import com.timgortworst.roomy.R
import com.timgortworst.roomy.customview.BottomSheetMenu
import com.timgortworst.roomy.model.BottomMenuItem
import com.timgortworst.roomy.model.Category
import com.timgortworst.roomy.ui.category.adapter.CategoryListAdapter
import com.timgortworst.roomy.ui.category.presenter.CategoryListPresenter
import com.timgortworst.roomy.ui.main.view.MainActivity
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_category_list.*
import javax.inject.Inject


class CategoryListFragment : Fragment(), CategoryListView {
    private lateinit var activityContext: MainActivity
    private lateinit var adapter: CategoryListAdapter

    @Inject
    lateinit var presenter: CategoryListPresenter

    companion object {
        private val TAG = "CategoryListFragment"

        fun newInstance(): CategoryListFragment {
            return CategoryListFragment()
        }
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
        activityContext = (activity as MainActivity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_category_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.listenToCategories()

        adapter = CategoryListAdapter(object : CategoryListAdapter.OnOptionsClickListener {
                override fun onOptionsClick(householdTask: Category) {
                    showContextMenuFor(householdTask)
                }
            })
        val layoutManager = LinearLayoutManager(activityContext)
        household_task_list.layoutManager = layoutManager
        household_task_list.addItemDecoration(StickyRecyclerHeadersDecoration(adapter))
        household_task_list.addItemDecoration(DividerItemDecoration(activityContext, layoutManager.orientation))
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

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachCategoryListener()
    }

    fun showContextMenuFor(householdTask: Category) {
        var bottomSheetMenu: BottomSheetMenu? = null

        val items = arrayListOf(
            BottomMenuItem(R.drawable.ic_edit, "Edit") {
                CategoryEditActivity.start(activityContext, householdTask)
                bottomSheetMenu?.dismiss()
            },
            BottomMenuItem(R.drawable.ic_delete, "Delete") {
                presenter.deleteCategory(householdTask)
                bottomSheetMenu?.dismiss()
            }
        )
        bottomSheetMenu = BottomSheetMenu(activityContext, householdTask.name, items)
        bottomSheetMenu.show()
    }

    override fun presentNewCategory(householdTask: Category) {
        adapter.insertItem(householdTask)
    }

    override fun presentEditedCategory(householdTask: Category) {
        adapter.editItem(householdTask)
    }

    override fun presentDeletedCategory(householdTask: Category) {
        adapter.removeItem(householdTask)
    }
}