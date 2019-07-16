package com.timgortworst.roomy.ui.main.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.timgortworst.roomy.R
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

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupClickListeners()
        presentAgendaFragment()
    }

    private fun setupClickListeners() {
        main_agenda.setOnClickListener { presentAgendaFragment() }
        main_categories.setOnClickListener { presentTasksFragment() }
        main_housemates.setOnClickListener { presentHousematesFragment() }
        main_settings.setOnClickListener { SettingsActivity.start(this) }
    }

    private fun fragmentToReplace(newFragment: Fragment) {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.content_frame)
        if (newFragment::class.java.toString() != currentFragment?.tag){
            supportFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.content_frame, newFragment, newFragment::class.java.toString())
                    .commit()
        }

    }

    fun presentAgendaFragment() {
        fragmentToReplace(EventListFragment.newInstance())
    }

    fun presentTasksFragment() {
        fragmentToReplace(CategoryListFragment.newInstance())
    }

    fun presentHousematesFragment() {
        fragmentToReplace(UserListFragment.newInstance())
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return dispatchingAndroidInjector
    }
}
