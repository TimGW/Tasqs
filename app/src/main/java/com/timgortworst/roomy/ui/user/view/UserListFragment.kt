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
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.timgortworst.roomy.R
import com.timgortworst.roomy.customview.BottomSheetMenu
import com.timgortworst.roomy.local.HuishoudGenootSharedPref
import com.timgortworst.roomy.model.BottomMenuItem
import com.timgortworst.roomy.model.User
import com.timgortworst.roomy.ui.main.view.MainActivity
import com.timgortworst.roomy.ui.user.adapter.UserListAdapter
import com.timgortworst.roomy.ui.user.presenter.UserListPresenter
import com.timgortworst.roomy.utils.Constants.QUERY_PARAM_HOUSEHOLD
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_user_list.*
import javax.inject.Inject


class UserListFragment : Fragment(), UserListView {
    @Inject
    lateinit var presenter: UserListPresenter
    private lateinit var adapter: UserListAdapter
    private lateinit var activityContext: MainActivity

    @Inject
    lateinit var sharedPref: HuishoudGenootSharedPref

    companion object {

        fun newInstance(): UserListFragment {
            return UserListFragment()
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
        return inflater.inflate(R.layout.fragment_user_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = UserListAdapter(object : UserListAdapter.OnUserLongClickListener {
            override fun onUserClick(user: User) {
                presenter.showContextMenuIfUserHasPermission(user)
            }
        })
        val layoutManager = LinearLayoutManager(activityContext)
        user_list.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(activityContext, layoutManager.orientation)
        user_list.addItemDecoration(dividerItemDecoration)
        user_list.adapter = adapter

        presenter.fetchUsers()
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

    override fun presentUserList(users: MutableList<User>) {
        adapter.setUsers(users)
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
                presenter.deleteUser(user) // todo
                bottomSheetMenu?.dismiss()
            }
        )
        bottomSheetMenu = BottomSheetMenu(activityContext, user.name, items)
        bottomSheetMenu.show()
    }

    override fun refreshView(user: User) {
        adapter.remove(user)
    }
}
