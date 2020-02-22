package com.timgortworst.roomy.presentation.features.onboarding.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.timgortworst.roomy.presentation.features.onboarding.view.OnboardingAuthFragment
import com.timgortworst.roomy.presentation.features.onboarding.view.OnboardingFragment

class OnboardingAdapter(fa: FragmentManager,
                        lifecycle: Lifecycle,
                        private val fragmentList: List<Fragment>) : FragmentStateAdapter(fa, lifecycle) {
    override fun getItemCount(): Int {
        return NUMBER_OF_PAGES
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0, 1 -> OnboardingFragment.newInstance(position)
            else -> OnboardingAuthFragment.newInstance()
        }
    }


//
//    fun ViewPagerFragmentAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle): ??? {
//        super(fragmentManager, lifecycle)
//    }
//
//
//    fun addFragment(fragment: Fragment) {
//        arrayList.add(fragment)
//    }
//
//    override fun getItemCount(): Int {
//        return arrayList.size()
//    }
//
//    override fun createFragment(position: Int): Fragment {
//        // return your fragment that corresponds to this 'position'
//        return arrayList.get(position)
//    }

    companion object {
        const val NUMBER_OF_PAGES = 3
    }
}