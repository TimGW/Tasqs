package com.timgortworst.roomy.ui.users.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.timgortworst.roomy.R
import com.timgortworst.roomy.local.HuishoudGenootSharedPref
import com.timgortworst.roomy.model.AuthenticationResult
import com.timgortworst.roomy.model.User
import com.timgortworst.roomy.ui.base.view.BaseAuthActivity
import com.timgortworst.roomy.ui.users.adapter.UserListAdapter
import com.timgortworst.roomy.ui.users.presenter.UsersPresenter
import com.timgortworst.roomy.utils.Constants.QUERY_PARAM_HOUSEHOLD
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_users.*
import javax.inject.Inject


class UsersActivity : BaseAuthActivity(), UsersView {
    @Inject
    lateinit var presenter: UsersPresenter
    private lateinit var adapter: UserListAdapter
    private lateinit var addPeopleMenuItem: MenuItem

    @Inject
    lateinit var sharedPref: HuishoudGenootSharedPref

    companion object {

        fun start(context: Context) {
            val intent = Intent(context, UsersActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)

        supportActionBar?.apply {
            title = getString(R.string.roommates)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        adapter = UserListAdapter(this, mutableListOf())
        val layoutManager = LinearLayoutManager(this)
        user_list.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(user_list.context, layoutManager.orientation)
        user_list.addItemDecoration(dividerItemDecoration)
        user_list.adapter = adapter

        presenter.fetchUsers()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.invite_menu, menu)
        addPeopleMenuItem = menu.findItem(R.id.action_invite)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.action_invite -> {
                share(sharedPref.getHouseholdId())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun presentUserList(users: MutableList<User>) {
        for (user in users) {
            addPeopleMenuItem.isVisible = user.role == AuthenticationResult.Role.ADMIN.name
        }
        adapter.setUsers(users)
    }

    private fun share(id: String) {
        showProgressDialog()
        val myUri = createShareUri(id)
        val dynamicLinkUri = createDynamicUri(myUri)
        shortenLink(dynamicLinkUri)
    }

    private fun createShareUri(householdId: String): Uri {
        val builder = Uri.Builder()
        builder.scheme("https")
            .authority("huishoudgenoot.xyz")
            .appendPath("households")
            .appendQueryParameter(QUERY_PARAM_HOUSEHOLD, householdId)
        return builder.build()
    }

    private fun shortenLink(linkUri: Uri) {
        FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLongLink(linkUri)
            .buildShortDynamicLink()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val shortLink = task.result?.shortLink
                    val msg = "$shortLink"
                    val sendIntent = Intent()
                    sendIntent.action = Intent.ACTION_SEND
                    sendIntent.putExtra(Intent.EXTRA_TEXT, msg)
                    sendIntent.type = "text/plain"

                    if (sendIntent.resolveActivity(packageManager) != null)
                        startActivity(Intent.createChooser(sendIntent, "Share"))
                    else
                        startActivity(sendIntent)
                } else {
                    Log.e("TIMTIM", task.exception?.message)
                }
                hideProgressDialog()
            }
    }

    private fun createDynamicUri(myUri: Uri): Uri {
        val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(myUri)
            .setDomainUriPrefix("https://huishoudgenoot.page.link")
            .setAndroidParameters(
                DynamicLink.AndroidParameters.Builder()
                    .build()
            )
            .buildDynamicLink()
        return dynamicLink.uri
    }
}
