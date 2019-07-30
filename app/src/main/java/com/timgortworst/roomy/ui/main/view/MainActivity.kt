package com.timgortworst.roomy.ui.main.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.timgortworst.roomy.R
import com.timgortworst.roomy.ui.BaseActivity
import com.timgortworst.roomy.ui.category.view.CategoryEditActivity
import com.timgortworst.roomy.ui.category.view.CategoryListFragment
import com.timgortworst.roomy.ui.event.view.EventEditActivity
import com.timgortworst.roomy.ui.event.view.EventListFragment
import com.timgortworst.roomy.ui.main.presenter.MainPresenter
import com.timgortworst.roomy.ui.settings.view.SettingsActivity
import com.timgortworst.roomy.ui.splash.ui.SplashActivity
import com.timgortworst.roomy.ui.user.view.UserListFragment
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class MainActivity : BaseActivity(), HasSupportFragmentInjector, MainView, FabVisibilityListener {
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var presenter: MainPresenter

    private var adRequest: AdRequest? = null

    private val eventListFragment: Fragment = EventListFragment.newInstance()
    private val categoryListFragment: Fragment = CategoryListFragment.newInstance()
    private val userListFragment: Fragment = UserListFragment.newInstance()
    private var active = eventListFragment

    companion object {
        private const val TAG = "MainActivity"

        fun start(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addFragment(userListFragment, true)
        addFragment(categoryListFragment, true)
        addFragment(eventListFragment, false)

        setupClickListeners()

        presenter.listenToHousehold()

        adRequest = AdRequest.Builder().build()
        adView?.adListener = object : AdListener() {
            override fun onAdLoaded() {
                adView_container?.visibility = View.VISIBLE
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                adView_container?.visibility = View.GONE
                Toast.makeText(this@MainActivity, getString(R.string.connection_error), Toast.LENGTH_LONG).show()
            }
        }
        adView?.loadAd(adRequest)
    }

    override fun onDestroy() {
        presenter.detachHouseholdListener()

        adView?.removeAllViews()
        adView?.destroy()

        super.onDestroy()
    }

    private fun setupClickListeners() {
        main_agenda.setOnClickListener {
            fragmentToReplace(eventListFragment)
            fab.setOnClickListener {
                EventEditActivity.start(this)
            }
        }
        main_categories.setOnClickListener {
            fragmentToReplace(categoryListFragment)
            fab.setOnClickListener {
                CategoryEditActivity.start(this)
            }
        }
        main_housemates.setOnClickListener {
            fragmentToReplace(userListFragment)
            fab.setOnClickListener {
                showProgressDialog()
                presenter.inviteUser()
            }
        }
        main_settings.setOnClickListener { SettingsActivity.start(this) }
    }

    private fun addFragment(fragment: Fragment, hide: Boolean) {
        val transaction = supportFragmentManager
                .beginTransaction()
                .add(R.id.content_frame, fragment, fragment::class.java.toString())
        if (hide) {
            transaction.hide(fragment)
        }
        transaction.commit()
    }

    private fun fragmentToReplace(newFragment: Fragment) {
        if (newFragment::class.java.toString() != active.tag) {
            supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .hide(active)
                    .show(newFragment)
                    .commit()
            active = newFragment
//            supportActionBar?.title = getString(R.string.roommates) todo
//            activityContext.supportActionBar?.title = getString(R.string.schema_toolbar_title)
//            activityContext.supportActionBar?.title = getString(R.string.householdtasks_toolbar_title)
        }
    }

    override fun logout() {
        finishAffinity()
        SplashActivity.start(this)
    }

    override fun share(householdId: String) {
        val linkUri = InviteLink.Builder()
                .householdId(householdId)
                .build()

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
                            startActivity(Intent.createChooser(sendIntent, getString(R.string.invite_title)))
                        else
                            startActivity(sendIntent)
                    } else {
                        Log.e(TAG, task.exception?.message!!)
                    }
                    hideProgressDialog()
                }
    }

    override fun setFabVisible(isVisible: Boolean) {
        if (isVisible) fab.show() else fab.hide()
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return dispatchingAndroidInjector
    }
}
