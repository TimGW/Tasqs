package com.timgortworst.roomy.presentation.features.main

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.material.transition.MaterialContainerTransformSharedElementCallback
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.timgortworst.roomy.R
import com.timgortworst.roomy.databinding.ActivityMainBinding
import com.timgortworst.roomy.presentation.base.view.BaseActivity
import com.timgortworst.roomy.presentation.features.task.view.TaskEditActivity
import com.timgortworst.roomy.presentation.features.task.view.TaskListFragment
import com.timgortworst.roomy.presentation.features.settings.SettingsActivity
import com.timgortworst.roomy.presentation.features.splash.SplashActivity
import com.timgortworst.roomy.presentation.features.user.UserListFragment
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class MainActivity : BaseActivity(), MainView {
    private lateinit var binding: ActivityMainBinding
    private val presenter: MainPresenter by inject {
        parametersOf(this)
    }

    private var adRequest: AdRequest? = null
    private val taskListFragment: Fragment by lazy { TaskListFragment.newInstance() }
    private val userListFragment: Fragment by lazy { UserListFragment.newInstance() }
//    private val googleAuthFragment: Fragment by lazy { AuthFragment.newInstance() }

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
        initAnimation()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            openFragment(taskListFragment, taskListFragment::class.java.toString())
        } else {
            activeFragment = supportFragmentManager.getFragment(savedInstanceState, ACTIVE_FRAG_KEY)
            activeFragment?.let { openFragment(it, it::class.java.toString()) }
        }

        setupBottomAppBar()
        updateFabAndTitle(activeFragment)

//        presenter.listenToHousehold()

        setupAds()
        setupBroadcastReceivers()
    }

    private fun initAnimation() {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        setExitSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementsUseOverlay = false
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
//        presenter.detachHouseholdListener()
        binding.adView.removeAllViews()
        binding.adView.destroy()
        super.onDestroy()
    }

    private fun setupBottomAppBar() {
        binding.bottomAppbar.replaceMenu(R.menu.bottom_appbar_menu)
        binding.bottomAppbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.appbar_tasks_id -> openFragment(taskListFragment, taskListFragment::class.java.toString())
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

//            if (activeFragment.tag == googleAuthFragment::class.java.toString()) {
//                binding.fab.hide()
//            } else {
//                binding.fab.show()
//            }
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
            taskListFragment::class.java.toString() -> getString(R.string.toolbar_title_tasks)
            userListFragment::class.java.toString() -> getString(R.string.toolbar_title_users)
            else -> getString(R.string.app_name)
        }
    }

    private fun setFabClickListenerFor(fragment: Fragment?) {
        val clickTask: (View) -> Unit = when (fragment?.tag) {
            taskListFragment::class.java.toString() -> { _ ->
                openTaskEditActivity()
            }
            userListFragment::class.java.toString() -> { _ ->
                showProgressDialog()
                presenter.inviteUser()
            }
            else -> { _ -> }
        }

        binding.fab.setOnClickListener(clickTask)
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
        binding.adView.adListener = object : AdListener() {
            override fun onAdLoaded() { presenter.showOrHideAd() }

            override fun onAdFailedToLoad(errorCode: Int) { hideAd() }
        }
        loadAd()
    }

    override fun loadAd() { binding.adView.loadAd(adRequest) }

    override fun showAd() { binding.adViewContainer.visibility = View.VISIBLE }

    override fun hideAd() { binding.adViewContainer.visibility = View.GONE }

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

    override fun openTaskEditActivity() {
//        TaskEditActivity.start(this)

        val intent = Intent(this, TaskEditActivity::class.java)
        val options =  ActivityOptions.makeSceneTransitionAnimation(
            this,
            binding.fab,
            "shared_element_container"  // The transition name to be matched in Activity B.
        )
//        task?.let { intent.putExtra(TaskEditActivity.INTENT_EXTRA_EDIT_TASK, it) }
        startActivity(intent, options.toBundle())

    }

//    override fun presentGoogleAuthFragment() {
////        openFragment(googleAuthFragment, googleAuthFragment::class.java.toString())
//    }
//
//    override fun presentUsersFragment() {
////        openFragment(userListFragment, userListFragment::class.java.toString())
//    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data) // delegate to fragment
    }
}
