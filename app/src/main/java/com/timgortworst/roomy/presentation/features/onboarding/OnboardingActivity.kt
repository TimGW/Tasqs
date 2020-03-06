package com.timgortworst.roomy.presentation.features.onboarding

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.timgortworst.roomy.R
import com.timgortworst.roomy.data.SharedPrefs
import com.timgortworst.roomy.databinding.ActivityOnboardingBinding
import com.timgortworst.roomy.presentation.base.view.BaseActivity
import com.timgortworst.roomy.presentation.features.auth.AuthFragment
import com.timgortworst.roomy.presentation.features.main.MainActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class OnboardingActivity : BaseActivity() {
    private lateinit var binding: ActivityOnboardingBinding
    private val sharedPrefs: SharedPrefs by inject { parametersOf(this) }

    private val onboardingFragments = listOf(
            OnboardingFragment.newInstance(0),
            OnboardingFragment.newInstance(1),
            AuthFragment.newInstance())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = OnboardingAdapter(this, onboardingFragments)
        binding.viewPager.adapter = adapter
        binding.indicator.setViewPager(binding.viewPager)

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                // todo migrate to presenter
                if (position == adapter.itemCount - 1) {
                    binding.nextButton.setText(R.string.skip)
                    binding.nextButton.setOnClickListener {
                        (onboardingFragments[position] as? AuthFragment)?.signInAnonymously()
                    }
                } else {
                    binding.nextButton.setText(R.string.next)
                    binding.nextButton.setOnClickListener {
                        binding.viewPager.currentItem =  binding.viewPager.currentItem + 1
                    }
                }
            }
        })
    }

    override fun onBackPressed() {
        if ( binding.viewPager.currentItem == 0) {
            super.onBackPressed()
        } else {
            binding.viewPager.currentItem =  binding.viewPager.currentItem - 1
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