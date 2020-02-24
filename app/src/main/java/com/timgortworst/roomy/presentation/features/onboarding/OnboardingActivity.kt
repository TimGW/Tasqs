package com.timgortworst.roomy.presentation.features.onboarding

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.timgortworst.roomy.R
import com.timgortworst.roomy.data.SharedPrefs
import com.timgortworst.roomy.presentation.base.view.BaseActivity
import com.timgortworst.roomy.presentation.features.auth.AuthFragment
import com.timgortworst.roomy.presentation.features.main.MainActivity
import kotlinx.android.synthetic.main.activity_onboarding.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class OnboardingActivity : BaseActivity() {
    private val sharedPrefs: SharedPrefs by inject { parametersOf(this) }

    private val onboardingFragments = listOf(
            OnboardingFragment.newInstance(0),
            OnboardingFragment.newInstance(1),
            AuthFragment.newInstance())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        val adapter = OnboardingAdapter(this, onboardingFragments)
        view_pager.adapter = adapter
        indicator.setViewPager(view_pager)

        view_pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                // todo migrate to presenter
                if (position == adapter.itemCount - 1) {
                    next_button.setText(R.string.skip)
                    next_button.setOnClickListener {
                        (onboardingFragments[position] as? AuthFragment)?.signInAnonymously()
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

    fun goToMain() {
        sharedPrefs.setFirstLaunch(false)
        MainActivity.start(this)
        finish()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data) // delegate to fragment
    }

    companion object {
        fun start(originActivity: Activity) {
            originActivity.startActivity(Intent(originActivity, OnboardingActivity::class.java))
        }
    }
}