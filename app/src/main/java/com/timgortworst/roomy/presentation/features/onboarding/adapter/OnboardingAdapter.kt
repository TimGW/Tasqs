package com.timgortworst.roomy.presentation.features.onboarding.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.timgortworst.roomy.presentation.features.onboarding.view.OnboardingAuthFragment
import com.timgortworst.roomy.presentation.features.onboarding.view.OnboardingFragment

class OnboardingAdapter(fa: FragmentActivity,
                        private val fragmentList: List<Fragment>) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {

        return fragmentList[position]
    }
}