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
    private val eventListFragment: Fragment by lazy { EventListFragment.newInstance() }
    private val categoryListFragment: Fragment by lazy { CategoryListFragment.newInstance() }
    private val userListFragment: Fragment by lazy { UserListFragment.newInstance() }
    private var activeFragment: Fragment? = null
    private lateinit var airplaneModeReceiver: AirplaneModeReceiver

    companion object {
        private const val TAG = "MainActivity"
        private const val ACTIVE_FRAG_KEY = "activeFragment"

        fun start(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (activeFragment?.isAdded == true) {
            activeFragment?.let { supportFragmentManager.putFragment(outState, ACTIVE_FRAG_KEY, it) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            openFragment(eventListFragment, eventListFragment::class.java.toString())
        } else {
            activeFragment = supportFragmentManager.getFragment(savedInstanceState, ACTIVE_FRAG_KEY)
            activeFragment?.let { openFragment(it, it::class.java.toString()) }
        }

        setClickListeners(activeFragment)

        airplaneModeReceiver = object : AirplaneModeReceiver(this) {
            override fun airplaneModeChanged(isEnabled: Boolean) {
                (activeFragment as PageStateListener).setErrorView(
                        isEnabled,
                        R.string.disable_airplane_mode_title,
                        R.string.disable_airplane_mode_text)

                if (!isEnabled) {
                    eventListFragment.tag?.let { reloadFragment(it) }
                    categoryListFragment.tag?.let { reloadFragment(it) }
                    userListFragment.tag?.let { reloadFragment(it) }
                    activeFragment?.tag?.let { reloadFragment(it) }
                }
            }
        }

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

    override fun onResume() {
        super.onResume()
        airplaneModeReceiver.register()
    }

    override fun onPause() {
        super.onPause()
        airplaneModeReceiver.unregister()
    }

    override fun onDestroy() {
        presenter.detachHouseholdListener()
        adView?.removeAllViews()
        adView?.destroy()
        super.onDestroy()
    }

    private fun setClickListeners(activeFragment: Fragment?) {
        main_agenda.setOnClickListener { openFragment(eventListFragment, eventListFragment::class.java.toString()) }
        main_categories.setOnClickListener { openFragment(categoryListFragment, categoryListFragment::class.java.toString()) }
        main_housemates.setOnClickListener { openFragment(userListFragment, userListFragment::class.java.toString()) }
        main_settings.setOnClickListener { SettingsActivity.start(this) }
        activeFragment?.let { fragment ->
            setFabClickListenerFor(fragment)
            setToolbarTitleFor(fragment.tag.orEmpty())
        }
    }

    private fun reloadFragment(tag: String) {
        val frg = supportFragmentManager.findFragmentByTag(tag) ?: return
        supportFragmentManager.beginTransaction().apply {
            detach(frg)
            attach(frg)
            commit()
        }
    }

    private fun openFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction().apply {
            setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            activeFragment?.let { hide(it) }

            if (fragment.isAdded) {
                show(fragment)
            } else {
                add(R.id.content_frame, fragment, tag)
            }
            commit()
            activeFragment = fragment
        }

        setClickListeners(activeFragment)
    }

    private fun setToolbarTitleFor(tag: String) {
        supportActionBar?.title = when (tag) {
            eventListFragment::class.java.toString() -> getString(R.string.schema_toolbar_title)
            categoryListFragment::class.java.toString() -> getString(R.string.householdtasks_toolbar_title)
            userListFragment::class.java.toString() -> getString(R.string.roommates)
            else -> getString(R.string.app_name)
        }
    }

    private fun setFabClickListenerFor(fragment: Fragment?) {
        val clickEvent: (View) -> Unit = when (fragment?.tag) {
            eventListFragment::class.java.toString() -> { _ ->
                EventEditActivity.start(this)
            }
            categoryListFragment::class.java.toString() -> { _ ->
                CategoryEditActivity.start(this)
            }
            userListFragment::class.java.toString() -> { _ ->
                showProgressDialog()
                presenter.inviteUser()
            }
            else -> { _ -> }
        }

        fab.setOnClickListener(clickEvent)
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
