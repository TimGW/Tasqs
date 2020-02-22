package com.timgortworst.roomy.presentation.features.user.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.timgortworst.roomy.R
import com.timgortworst.roomy.domain.model.BottomMenuItem
import com.timgortworst.roomy.domain.model.User
import com.timgortworst.roomy.presentation.base.customview.BottomSheetMenu
import com.timgortworst.roomy.presentation.features.main.view.MainActivity
import com.timgortworst.roomy.presentation.features.user.adapter.UserListAdapter
import com.timgortworst.roomy.presentation.features.user.presenter.UserListPresenter
import kotlinx.android.synthetic.main.fragment_recycler_view.*
import kotlinx.android.synthetic.main.fragment_recycler_view.view.*
import kotlinx.android.synthetic.main.layout_list_state.view.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class UserListFragment : Fragment(), UserListView {
    private lateinit var userListAdapter: UserListAdapter
    private lateinit var activityContext: MainActivity
    private var recyclerView: RecyclerView? = null
    private val presenter: UserListPresenter by inject {
        parametersOf(this)
    }

    companion object {
        fun newInstance(): UserListFragment {
            return UserListFragment()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityContext = (activity as? MainActivity) ?: return
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_recycler_view, container, false)
        userListAdapter = UserListAdapter(object : UserListAdapter.OnUserLongClickListener {
            override fun onUserClick(user: User) {
                presenter.showContextMenuIfUserHasPermission(user)
            }
        })
        recyclerView = view.recycler_view
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipe_container?.isEnabled = false

        presenter.listenToUsers()

        recyclerView?.apply {
            val linearLayoutManager = LinearLayoutManager(activityContext)
            val dividerItemDecoration = DividerItemDecoration(activityContext, linearLayoutManager.orientation)
            layoutManager = linearLayoutManager
            adapter = userListAdapter
            addItemDecoration(dividerItemDecoration)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachUserListener()
    }

    override fun showContextMenuFor(user: User) {
        var bottomSheetMenu: BottomSheetMenu? = null

        val items = arrayListOf(
                BottomMenuItem(R.drawable.ic_delete, "Delete") {
                    presenter.deleteUser(user)
                    bottomSheetMenu?.dismiss()
                }
        )
        bottomSheetMenu = BottomSheetMenu(activityContext, user.name, items)
        bottomSheetMenu.show()
    }

    override fun presentEditedUser(user: User) {
        userListAdapter.updateUser(user)
    }

    override fun presentDeletedUser(user: User) {
        userListAdapter.removeUser(user)
    }

    override fun presentAddedUser(user: User) {
        userListAdapter.addUser(user)
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
}
