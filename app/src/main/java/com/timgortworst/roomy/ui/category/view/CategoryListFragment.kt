package com.timgortworst.roomy.ui.category.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration
import com.timgortworst.roomy.R
import com.timgortworst.roomy.customview.BottomSheetMenu
import com.timgortworst.roomy.model.BottomMenuItem
import com.timgortworst.roomy.model.Category
import com.timgortworst.roomy.ui.category.adapter.CategoryListAdapter
import com.timgortworst.roomy.ui.category.presenter.CategoryListPresenter
import com.timgortworst.roomy.ui.main.view.MainActivity
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_recycler_view.*
import kotlinx.android.synthetic.main.fragment_recycler_view.view.*
import kotlinx.android.synthetic.main.layout_list_state_empty.view.state_message
import kotlinx.android.synthetic.main.layout_list_state_empty.view.state_title
import kotlinx.android.synthetic.main.layout_list_state_error.view.*
import javax.inject.Inject


class CategoryListFragment : Fragment(), CategoryListView, CategoryListAdapter.OnOptionsClickListener {
    private var recyclerView: RecyclerView? = null
    private lateinit var activityContext: MainActivity
    private lateinit var categoryListAdapter: CategoryListAdapter

    @Inject
    lateinit var presenter: CategoryListPresenter

    companion object {
        fun newInstance(): CategoryListFragment {
            return CategoryListFragment()
        }
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
        activityContext = (activity as? MainActivity) ?: return
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_recycler_view, container, false)
        categoryListAdapter = CategoryListAdapter(this)
        recyclerView = view.recycler_view
        view.swipe_container?.isEnabled = false

        recyclerView?.apply {
            val linearLayoutManager = LinearLayoutManager(activityContext)
            layoutManager = linearLayoutManager
            adapter = categoryListAdapter
            addItemDecoration(StickyRecyclerHeadersDecoration(categoryListAdapter))
            addItemDecoration(DividerItemDecoration(context, linearLayoutManager.orientation))
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.listenToCategories()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachCategoryListener()
    }

    override fun onOptionsClick(category: Category) {
        showContextMenuFor(category)
    }

    private fun showContextMenuFor(householdTask: Category) {
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

    override fun presentAddedCategory(category: Category) {
        categoryListAdapter.insertItem(category)
    }

    override fun presentEditedCategory(category: Category) {
        categoryListAdapter.editItem(category)
    }

    override fun presentDeletedCategory(category: Category) {
        categoryListAdapter.removeItem(category)
    }

    override fun setLoadingView(isLoading: Boolean) {
        swipe_container?.isRefreshing = isLoading
    }

    override fun presentEmptyView(isVisible: Boolean) {
        layout_list_state_empty?.apply {
            state_title.text = activity?.getString(R.string.empty_list_state_title_categories)
            state_message.text = activity?.getString(R.string.empty_list_state_text_categories)
            visibility = if (isVisible) View.VISIBLE else View.GONE
        }
    }

    override fun setErrorView(isVisible: Boolean, title: Int, message: Int) {
        layout_list_state_error?.apply {
            state_title.text = activity?.getString(title)
            state_message.text = activity?.getString(message)
            state_action_button.setOnClickListener { presenter.listenToCategories() }
            visibility = if (isVisible) View.VISIBLE else View.GONE
        }
    }

    fun presentAirplaneModeView() {
//        swipe_container?.visibility = View.GONE
    }
}