package com.timgortworst.roomy.presentation.features.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.timgortworst.roomy.databinding.FragmentUserListBinding
import org.koin.android.viewmodel.ext.android.viewModel

class UserListFragment : Fragment() {
    private var _binding: FragmentUserListBinding? = null
    private val binding get() = _binding!!
    private val userViewModel by viewModel<UserViewModel>()

    companion object {
        fun newInstance() = UserListFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserListBinding.inflate(inflater, container, false)

        with(binding) {
            viewModel = userViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }
}
