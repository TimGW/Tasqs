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

    private var eventListFragment: Fragment = EventListFragment.newInstance()
    private var categoryListFragment: Fragment = CategoryListFragment.newInstance()
    private var userListFragment: Fragment = UserListFragment.newInstance()
    private var activeFragment: Fragment? = null

    companion object {
        private const val TAG = "MainActivity"

        fun start(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        supportFragmentManager.putFragment(outState, FragmentTag.USER_LIST_FRAGMENT.name, userListFragment)
        supportFragmentManager.putFragment(outState, FragmentTag.CATEGORY_LIST_FRAGMENT.name, categoryListFragment)
        supportFragmentManager.putFragment(outState, FragmentTag.EVENT_LIST_FRAGMENT.name, eventListFragment)
        activeFragment?.let { supportFragmentManager.putFragment(outState, FragmentTag.ACTIVE_FRAGMENT.name, it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            openFragment(userListFragment, FragmentTag.USER_LIST_FRAGMENT.name)
            openFragment(categoryListFragment, FragmentTag.CATEGORY_LIST_FRAGMENT.name)
            openFragment(eventListFragment, FragmentTag.EVENT_LIST_FRAGMENT.name)
        } else {
            userListFragment = supportFragmentManager.getFragment(savedInstanceState, FragmentTag.USER_LIST_FRAGMENT.name) as Fragment
            categoryListFragment = supportFragmentManager.getFragment(savedInstanceState, FragmentTag.CATEGORY_LIST_FRAGMENT.name) as Fragment
            eventListFragment = supportFragmentManager.getFragment(savedInstanceState, FragmentTag.EVENT_LIST_FRAGMENT.name) as Fragment
            activeFragment = supportFragmentManager.getFragment(savedInstanceState, FragmentTag.ACTIVE_FRAGMENT.name) as Fragment
        }

        setupClickListeners(activeFragment)

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

    private fun setupClickListeners(activeFragment: Fragment?) {
        main_agenda.setOnClickListener { openFragment(eventListFragment, FragmentTag.EVENT_LIST_FRAGMENT.name) }
        main_categories.setOnClickListener { openFragment(categoryListFragment, FragmentTag.CATEGORY_LIST_FRAGMENT.name) }
        main_housemates.setOnClickListener { openFragment(userListFragment, FragmentTag.USER_LIST_FRAGMENT.name) }
        main_settings.setOnClickListener { SettingsActivity.start(this) }
        activeFragment?.let { fragment ->
            setFabClickListenerFor(fragment)
            setToolbarTitleFor(fragment.tag.orEmpty())
        }
    }

    private fun openFragment(fragment: Fragment, tag: String) {
        val fragmentTransaction = supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
        activeFragment?.let { fragmentTransaction.hide(it) }

        if (doesFragmentExist(tag)) {
            fragmentTransaction.show(fragment)
        } else {
            fragmentTransaction.add(R.id.content_frame, fragment, tag)
        }

        fragmentTransaction.commit()
        activeFragment = fragment

        setFabClickListenerFor(fragment)
        setToolbarTitleFor(fragment.tag.orEmpty())
    }

    private fun doesFragmentExist(tag: String) = supportFragmentManager.findFragmentByTag(tag) != null

    private fun setToolbarTitleFor(tag: String) {
        supportActionBar?.title = when (tag) {
            FragmentTag.EVENT_LIST_FRAGMENT.name -> getString(R.string.schema_toolbar_title)
            FragmentTag.CATEGORY_LIST_FRAGMENT.name -> getString(R.string.householdtasks_toolbar_title)
            FragmentTag.USER_LIST_FRAGMENT.name -> getString(R.string.roommates)
            else -> getString(R.string.app_name)
        }
    }

    private fun setFabClickListenerFor(fragment: Fragment) {
        val clickEvent: (View) -> Unit = when (fragment.tag) {
            FragmentTag.EVENT_LIST_FRAGMENT.name -> { _ ->
                EventEditActivity.start(this)
            }
            FragmentTag.CATEGORY_LIST_FRAGMENT.name -> { _ ->
                CategoryEditActivity.start(this)
            }
            FragmentTag.USER_LIST_FRAGMENT.name -> { _ ->
                showProgressDialog()
                presenter.inviteUser()
            }
            else -> { _ -> null }
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

    enum class FragmentTag {
        ACTIVE_FRAGMENT,
        EVENT_LIST_FRAGMENT,
        CATEGORY_LIST_FRAGMENT,
        USER_LIST_FRAGMENT;
    }
}
