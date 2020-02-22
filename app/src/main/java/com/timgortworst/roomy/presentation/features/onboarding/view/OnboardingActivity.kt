package com.timgortworst.roomy.presentation.features.onboarding.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.timgortworst.roomy.R
import com.timgortworst.roomy.presentation.base.view.BaseActivity
import com.timgortworst.roomy.presentation.features.onboarding.adapter.OnboardingAdapter
import kotlinx.android.synthetic.main.activity_onboarding.*

class OnboardingActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        val adapter = OnboardingAdapter(supportFragmentManager, lifecycle)
        view_pager.adapter = adapter
        indicator.setViewPager(view_pager)

        view_pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                if (position == adapter.itemCount - 1) {
                    next_button.setText(R.string.skip)
                    next_button.setOnClickListener {
                        (supportFragmentManager.findFragmentById(
                                adapter.getItemId(position).toInt()
                        ) as OnboardingAuthFragment).signInAnonymously()
                    }
                } else {
                    next_button.setText(R.string.next)
                    next_button.setOnClickListener {
                        view_pager.currentItem = view_pager.currentItem + 1
                    }
                }
            }
        })
    }

    override fun onBackPressed() {
        if (view_pager.currentItem == 0) {
            super.onBackPressed()
        } else {
            view_pager.currentItem = view_pager.currentItem - 1
        }
    }

    companion object {
        fun startForResult(originActivity: Activity, resultCode: Int) {
            val intent = Intent(originActivity, OnboardingActivity::class.java)
            originActivity.startActivityForResult(intent, resultCode)
        }
    }
}