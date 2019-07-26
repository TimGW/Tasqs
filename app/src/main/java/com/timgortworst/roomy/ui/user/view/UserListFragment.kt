package com.timgortworst.roomy.ui.user.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.timgortworst.roomy.R
import com.timgortworst.roomy.customview.BottomSheetMenu
import com.timgortworst.roomy.model.BottomMenuItem
import com.timgortworst.roomy.model.User
import com.timgortworst.roomy.ui.main.view.MainActivity
import com.timgortworst.roomy.ui.user.adapter.UserListAdapter
import com.timgortworst.roomy.ui.user.presenter.UserListPresenter
import com.timgortworst.roomy.utils.Constants.QUERY_PARAM_HOUSEHOLD
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_recycler_view.view.*
import javax.inject.Inject


class UserListFragment : Fragment(), UserListView {
    @Inject
    lateinit var presenter: UserListPresenter
    private lateinit var userListAdapter: UserListAdapter
    private lateinit var activityContext: MainActivity
    private var recyclerView: RecyclerView? = null

    companion object {
        fun newInstance(): UserListFragment {
            return UserListFragment()
        }
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
        activityContext = (activity as MainActivity)
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

        presenter.listenToUsers()

//        view.showOrHideFab(userList.size < 8 && currentUser?.role == Role.ADMIN.name)

        recyclerView?.apply {
            val linearLayoutManager = LinearLayoutManager(activityContext)
            val dividerItemDecoration = DividerItemDecoration(activityContext, linearLayoutManager.orientation)

            layoutManager = linearLayoutManager
            adapter = userListAdapter

            addItemDecoration(dividerItemDecoration)
        }
    }

    override fun onResume() {
        super.onResume()
        activityContext.supportActionBar?.title = getString(R.string.roommates)
        activityContext.fab.setOnClickListener {
            activityContext.showProgressDialog()
            presenter.inviteUser()
        }
    }

    override fun onPause() {
        super.onPause()
        activityContext.fab.setOnClickListener(null)
        activityContext.fab.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachUserListener()
    }

    override fun share(householdId: String) {
        val myUri = createShareUri(householdId)
        val dynamicLinkUri = createDynamicUri(myUri)
        shortenLink(dynamicLinkUri)
    }

    private fun createShareUri(householdId: String): Uri {
        val builder = Uri.Builder()
        builder.scheme("https")
                .authority("roomy.xyz")
                .appendPath("households")
                .appendQueryParameter(QUERY_PARAM_HOUSEHOLD, householdId)
        return builder.build()
    }

    private fun shortenLink(linkUri: Uri) {
        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(linkUri)
                .buildShortDynamicLink()
                .addOnCompleteListener(activityContext) { task ->
                    if (task.isSuccessful) {
                        val shortLink = task.result?.shortLink
                        val msg = "$shortLink"
                        val sendIntent = Intent()
                        sendIntent.action = Intent.ACTION_SEND
                        sendIntent.putExtra(Intent.EXTRA_TEXT, msg)
                        sendIntent.type = "text/plain"

                        if (sendIntent.resolveActivity(activityContext.packageManager) != null)
                            startActivity(Intent.createChooser(sendIntent, getString(R.string.invite_title)))
                        else
                            startActivity(sendIntent)
                    } else {
                        Log.e("TIMTIM", task.exception?.message)
                    }
                    activityContext.hideProgressDialog()
                }
    }

    private fun createDynamicUri(myUri: Uri): Uri {
        val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(myUri)
                .setDomainUriPrefix("https://roomyf3eb1.page.link")
                .setAndroidParameters(
                        DynamicLink.AndroidParameters.Builder()
                                .build()
                )
                .buildDynamicLink()
        return dynamicLink.uri
    }

    override fun showOrHideFab(condition: Boolean) =
            if (condition) activityContext.fab.show() else activityContext.fab.hide()

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
}
