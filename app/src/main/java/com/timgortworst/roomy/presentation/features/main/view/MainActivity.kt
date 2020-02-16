package com.timgortworst.roomy.presentation.features.main.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.timgortworst.roomy.R
import com.timgortworst.roomy.presentation.base.view.BaseActivity
import com.timgortworst.roomy.presentation.features.event.view.EventEditActivity
import com.timgortworst.roomy.presentation.features.event.view.EventListFragment
import com.timgortworst.roomy.presentation.features.main.NetworkChangeReceiver
import com.timgortworst.roomy.presentation.features.main.presenter.MainPresenter
import com.timgortworst.roomy.presentation.features.settings.view.SettingsActivity
import com.timgortworst.roomy.presentation.features.splash.view.SplashActivity
import com.timgortworst.roomy.presentation.features.user.view.UserListFragment
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.timgortworst.roomy.data.model.Event


class MainActivity : BaseActivity(), HasSupportFragmentInjector, MainView {
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var presenter: MainPresenter

    private var adRequest: AdRequest? = null
    private val eventListFragment: Fragment by lazy { EventListFragment.newInstance() }
    private val userListFragment: Fragment by lazy { UserListFragment.newInstance() }
    private var activeFragment: Fragment? = null
    private lateinit var networkChangeReceiver: NetworkChangeReceiver

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

        setupBottomAppBar()
        updateFabAndTitle(activeFragment)

        presenter.listenToHousehold()

        setupAds()
        setupBroadcastReceivers()
    }

    override fun onResume() {
        super.onResume()
        networkChangeReceiver.register()
        presenter.showOrHideAd()
    }

    override fun onPause() {
        super.onPause()
        networkChangeReceiver.unregister()
    }

    override fun onDestroy() {
        presenter.detachHouseholdListener()
        adView?.removeAllViews()
        adView?.destroy()
        super.onDestroy()
    }

    private fun setupBottomAppBar() {
        bottom_appbar.replaceMenu(R.menu.bottom_appbar_menu)
        bottom_appbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.appbar_events_id -> openFragment(eventListFragment, eventListFragment::class.java.toString())
                R.id.appbar_users_id -> openFragment(userListFragment, userListFragment::class.java.toString())
                R.id.appbar_settings_id -> SettingsActivity.start(this)
            }
            true
        }
    }

    private fun updateFabAndTitle(activeFragment: Fragment?) {
        activeFragment?.let {
            setFabClickListenerFor(it)
            setToolbarTitleFor(it.tag.orEmpty())

            fab.hide(object : FloatingActionButton.OnVisibilityChangedListener() {
                override fun onShown(floatingActionButton: FloatingActionButton?) {
                    super.onShown(fab)
                }
                override fun onHidden(floatingActionButton: FloatingActionButton?) {
                    super.onHidden(fab)
                    fab.show()
                }
            })
        }
    }

    private fun openFragment(fragment: Fragment, tag: String) {
        if(tag == activeFragment?.tag) return
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

        updateFabAndTitle(activeFragment)
    }

    private fun setToolbarTitleFor(tag: String) {
        supportActionBar?.title = when (tag) {
            eventListFragment::class.java.toString() -> getString(R.string.toolbar_title_schema)
            userListFragment::class.java.toString() -> getString(R.string.toolbar_title_users)
            else -> getString(R.string.app_name)
        }
    }

    private fun setFabClickListenerFor(fragment: Fragment?) {
        val clickEvent: (View) -> Unit = when (fragment?.tag) {
            eventListFragment::class.java.toString() -> { _ ->
                openEventEditActivity()
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
        presenter.buildInviteLink(householdId)
    }

    private fun setupBroadcastReceivers() {
        networkChangeReceiver = object : NetworkChangeReceiver(this) {
            override fun networkStatusChanged(isEnabled: Boolean) {
                presenter.networkStatusChanged(isEnabled)
            }
        }
    }

    private fun setupAds() {
        val builder = AdRequest.Builder()
        adRequest = builder.build()
        adView?.adListener = object : AdListener() {
            override fun onAdLoaded() { presenter.showOrHideAd() }

            override fun onAdFailedToLoad(errorCode: Int) { hideAd() }
        }
        loadAd()
    }

    override fun loadAd() { adView?.loadAd(adRequest) }

    override fun showAd() { adView_container?.visibility = View.VISIBLE }

    override fun hideAd() { adView_container?.visibility = View.GONE }

    override fun presentShareLinkUri(linkUri: Uri) {
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

    override fun showToast(stringRes: Int) {
        Toast.makeText(this@MainActivity, getString(stringRes), Toast.LENGTH_LONG).show()
    }

    override fun openEventEditActivity() {
        EventEditActivity.start(this)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return dispatchingAndroidInjector
    }
}
