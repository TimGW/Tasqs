package com.timgortworst.roomy.ui.housemates.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.timgortworst.roomy.R
import com.timgortworst.roomy.local.HuishoudGenootSharedPref
import com.timgortworst.roomy.model.Role
import com.timgortworst.roomy.model.User
import com.timgortworst.roomy.ui.housemates.adapter.HousematesAdapter
import com.timgortworst.roomy.ui.housemates.presenter.HousematesPresenter
import com.timgortworst.roomy.ui.main.view.MainActivity
import com.timgortworst.roomy.utils.Constants.QUERY_PARAM_HOUSEHOLD
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_housemates.*
import javax.inject.Inject


class HousematesFragment : Fragment(), HousenmatesView {
    @Inject
    lateinit var presenter: HousematesPresenter
    private lateinit var adapter: HousematesAdapter
    private lateinit var activityContext: MainActivity

    @Inject
    lateinit var sharedPref: HuishoudGenootSharedPref

    companion object {

        fun newInstance(): HousematesFragment {
            return HousematesFragment()
        }
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activityContext.supportActionBar?.title = getString(R.string.roommates)
        activityContext.fab.setOnClickListener { share(sharedPref.getActiveHouseholdId()) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityContext = (activity as MainActivity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.fragment_housemates, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = HousematesAdapter(activityContext, mutableListOf())
        val layoutManager = LinearLayoutManager(activityContext)
        user_list.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(user_list.context, layoutManager.orientation)
        user_list.addItemDecoration(dividerItemDecoration)
        user_list.adapter = adapter

        presenter.fetchCurrentUser()
        presenter.fetchUsers()
    }

    override fun onDetach() {
        super.onDetach()
        activityContext.fab.setOnClickListener { null }
    }

    override fun presentCurrentUser(currentUser: User?) {
        showOrHideFab(currentUser?.role == Role.ADMIN.name)
    }

    override fun presentUserList(users: MutableList<User>) {
        showOrHideFab(users.size < 8)
        adapter.setUsers(users)
    }

    private fun share(id: String) {
        activityContext.showProgressDialog()
        val myUri = createShareUri(id)
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
                        startActivity(Intent.createChooser(sendIntent, "Share"))
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

    fun showOrHideFab(condition: Boolean) = if(condition) activityContext.fab.show() else activityContext.fab.hide()
}
