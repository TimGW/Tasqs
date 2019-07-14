package com.timgortworst.roomy.ui.settings.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.timgortworst.roomy.R
import com.timgortworst.roomy.local.HuishoudGenootSharedPref
import com.timgortworst.roomy.ui.base.view.BaseAuthActivity
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

class SettingsActivity : BaseAuthActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var sharedPref: HuishoudGenootSharedPref

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, SettingsActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return dispatchingAndroidInjector
    }
}