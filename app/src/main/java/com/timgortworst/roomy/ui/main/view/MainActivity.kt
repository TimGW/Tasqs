package com.timgortworst.roomy.ui.main.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.timgortworst.roomy.ui.BaseActivity
import com.timgortworst.roomy.ui.category.view.CategoryListFragment
import com.timgortworst.roomy.ui.event.view.EventListFragment
import com.timgortworst.roomy.ui.settings.view.SettingsActivity
import com.timgortworst.roomy.ui.user.view.UserListFragment
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class MainActivity : BaseActivity(), HasSupportFragmentInjector {
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    private var adRequest: AdRequest? = null

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(com.timgortworst.roomy.R.layout.activity_main)

        setupClickListeners()

        presentAgendaFragment()

        adRequest = AdRequest.Builder().build()
        adView?.adListener = object : AdListener() {
            override fun onAdLoaded() {
                adView_container?.visibility = View.VISIBLE
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                adView_container?.visibility = View.GONE
                Toast.makeText(this@MainActivity, getString(com.timgortworst.roomy.R.string.connection_error), Toast.LENGTH_LONG).show()
            }
        }
        adView?.loadAd(adRequest)
    }

    override fun onDestroy() {
        super.onDestroy()
        adView?.removeAllViews()
        adView?.destroy()
    }

    private fun setupClickListeners() {
        main_agenda.setOnClickListener { presentAgendaFragment() }
        main_categories.setOnClickListener { presentTasksFragment() }
        main_housemates.setOnClickListener { presentHousematesFragment() }
        main_settings.setOnClickListener { SettingsActivity.start(this) }
    }

    private fun fragmentToReplace(newFragment: Fragment) {
        val currentFragment = supportFragmentManager.findFragmentById(com.timgortworst.roomy.R.id.content_frame)
        if (newFragment::class.java.toString() != currentFragment?.tag) {
            supportFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(com.timgortworst.roomy.R.anim.fade_in, com.timgortworst.roomy.R.anim.fade_out)
                    .replace(com.timgortworst.roomy.R.id.content_frame, newFragment, newFragment::class.java.toString())
                    .commit()
        }
    }

    private fun presentAgendaFragment() {
        fragmentToReplace(EventListFragment.newInstance())
    }

    private fun presentTasksFragment() {
        fragmentToReplace(CategoryListFragment.newInstance())
    }

    private fun presentHousematesFragment() {
        fragmentToReplace(UserListFragment.newInstance())
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return dispatchingAndroidInjector
    }
}
