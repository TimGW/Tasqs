package com.timgortworst.roomy.presentation.features.onboarding.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.timgortworst.roomy.R
import kotlinx.android.synthetic.main.fragment_onboarding.view.*

class OnboardingFragment : Fragment() {
    private var position: Int = INVALID_POSITION
    private var bgs = intArrayOf(R.drawable.onboarding_agenda, R.drawable.onboarding_connect)
    private var titles = intArrayOf(R.string.onboarding_title_agenda, R.string.onboarding_title_connect)
    private var subtitles = intArrayOf(R.string.onboarding_subtitle_agenda, R.string.onboarding_subtitle_connect)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_onboarding, container, false)
        position = arguments?.getInt(ARG_SECTION_NUMBER) ?: INVALID_POSITION
        if (position != INVALID_POSITION) {
            view.onboarding_image.setImageResource(bgs[position])
            view.onboarding_headline.setText(titles[position])
            view.onboarding_subtitle.setText(subtitles[position])
        }
        return view
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