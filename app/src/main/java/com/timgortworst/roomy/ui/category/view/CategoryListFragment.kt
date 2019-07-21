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
import com.timgortworst.roomy.model.UIState
import com.timgortworst.roomy.ui.category.adapter.CategoryListAdapter
import com.timgortworst.roomy.ui.category.presenter.CategoryListPresenter
import com.timgortworst.roomy.ui.main.view.MainActivity
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_recycler_view.*
import javax.inject.Inject


class CategoryListFragment : Fragment(), CategoryListView {
    private lateinit var activityContext: MainActivity
    private lateinit var categoryListAdapter: CategoryListAdapter

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
        return inflater.inflate(R.layout.fragment_recycler_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipe_container.isEnabled = false

        setupCategoryList()

        presenter.listenToCategories()
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

    private fun setupCategoryList() {
        categoryListAdapter = CategoryListAdapter(object : CategoryListAdapter.OnOptionsClickListener {
            override fun onOptionsClick(householdTask: Category) {
                showContextMenuFor(householdTask)
            }
        })
        val recyclerView = recycler_view
        recyclerView.apply {
            val linearLayoutManager = LinearLayoutManager(activityContext)

            layoutManager = linearLayoutManager
            adapter = categoryListAdapter

            addItemDecoration(StickyRecyclerHeadersDecoration(categoryListAdapter))
            addItemDecoration(DividerItemDecoration(context, linearLayoutManager.orientation))
        }
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
        categoryListAdapter.insertItem(householdTask)
    }

    override fun presentEditedCategory(householdTask: Category) {
        categoryListAdapter.editItem(householdTask)
    }

    override fun presentDeletedCategory(householdTask: Category) {
        categoryListAdapter.removeItem(householdTask)
    }

    override fun setUIState(uiState: UIState) {
        when(uiState) {
            UIState.LOADING -> {
                swipe_container.isRefreshing = true
            }
            UIState.ERROR -> {
                swipe_container.isRefreshing = false
            }
            UIState.SUCCESS -> {
                swipe_container.isRefreshing = false
            }
        }
    }
}