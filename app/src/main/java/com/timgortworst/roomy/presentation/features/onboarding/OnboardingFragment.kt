package com.timgortworst.roomy.presentation.features.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.timgortworst.roomy.R
import com.timgortworst.roomy.databinding.FragmentOnboardingBinding

class OnboardingFragment : Fragment() {
    private var position: Int = INVALID_POSITION
    private var bgs = intArrayOf(R.drawable.onboarding_agenda, R.drawable.onboarding_connect)
    private var titles = intArrayOf(R.string.onboarding_title_agenda, R.string.onboarding_title_connect)
    private var subtitles = intArrayOf(R.string.onboarding_subtitle_agenda, R.string.onboarding_subtitle_connect)

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        position = arguments?.getInt(ARG_SECTION_NUMBER) ?: INVALID_POSITION
        if (position != INVALID_POSITION) {
            binding.onboardingImage.setImageResource(bgs[position])
            binding.onboardingHeadline.setText(titles[position])
            binding.onboardingSubtitle.setText(subtitles[position])
        }
        return binding.root
    }

    companion object {
        private const val ARG_SECTION_NUMBER = "ARG_SECTION_NUMBER"
        private const val INVALID_POSITION = -1

        fun newInstance(position: Int): OnboardingFragment {
            val fragment = OnboardingFragment()
            val args = Bundle()
            args.putInt(ARG_SECTION_NUMBER, position)
            fragment.arguments = args
            return fragment
        }
    }
}