package com.timgortworst.roomy.ui.main.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.timgortworst.roomy.ui.BaseActivity
import com.timgortworst.roomy.ui.category.view.CategoryListFragment
import com.timgortworst.roomy.ui.event.view.EventListFragment
import com.timgortworst.roomy.ui.main.presenter.MainPresenter
import com.timgortworst.roomy.ui.settings.view.SettingsActivity
import com.timgortworst.roomy.ui.splash.ui.SplashActivity
import com.timgortworst.roomy.ui.user.view.UserListFragment
import com.timgortworst.roomy.utils.runBeforeDraw
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class MainActivity : BaseActivity(), HasSupportFragmentInjector, MainView {
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var presenter: MainPresenter

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

        presenter.listenToHousehold()

        adRequest = AdRequest.Builder().build()
        adView?.adListener = object : AdListener() {
            override fun onAdLoaded() {
                adView_container?.visibility = View.VISIBLE

                adView_container.runBeforeDraw {
                    val params = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT)
                    params.setMargins(
                            params.leftMargin,
                            params.topMargin,
                            params.rightMargin,
                            bottom_appbar.measuredHeight + it.measuredHeight)

                    content_frame?.layoutParams = params
                }
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
        presenter.detachHouseholdListener()

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

    override fun logout() {
        finishAffinity()
        SplashActivity.start(this)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return dispatchingAndroidInjector
    }
}
