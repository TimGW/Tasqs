package com.timgortworst.roomy.presentation.features.settings.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.timgortworst.roomy.R
import com.timgortworst.roomy.presentation.base.view.BaseActivity

class SettingsActivity : BaseActivity() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, SettingsActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
    }
}