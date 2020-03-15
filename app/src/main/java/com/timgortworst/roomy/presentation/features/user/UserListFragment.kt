package com.timgortworst.roomy.presentation.features.user

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.timgortworst.roomy.R
import com.timgortworst.roomy.databinding.FragmentUserListBinding
import com.timgortworst.roomy.domain.model.BottomMenuItem
import com.timgortworst.roomy.domain.model.User
import com.timgortworst.roomy.presentation.base.EventObserver
import com.timgortworst.roomy.presentation.base.customview.BottomSheetMenu
import com.timgortworst.roomy.presentation.features.main.MainActivity
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

class UserListFragment : Fragment(), OnLongClickListener {
    private lateinit var parentActivity: MainActivity
    private var _binding: FragmentUserListBinding? = null
    private val binding get() = _binding!!
    private val userViewModel by viewModel<UserViewModel>()
    private var bottomSheetMenu: BottomSheetMenu? = null

    companion object {
        fun newInstance(): UserListFragment {
            return UserListFragment()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        parentActivity = (activity as? MainActivity) ?: return
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

        setupRecyclerView()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            val linearLayoutManager = LinearLayoutManager(parentActivity)
            val dividerItemDecoration =
                DividerItemDecoration(parentActivity, linearLayoutManager.orientation)
            layoutManager = linearLayoutManager
            adapter = UserAdapter(mutableListOf(), this@UserListFragment)
            addItemDecoration(dividerItemDecoration)
        }
    }

    override fun onLongClick(user: User): Boolean {
        userViewModel.displayBottomSheet(user).observe(viewLifecycleOwner, EventObserver {
            showBottomMenuFor(user)
        })
        return true
    }

    private fun showBottomMenuFor(user: User) {
        val items = arrayListOf(
            BottomMenuItem(R.drawable.ic_delete, getString(R.string.delete)) {
                userViewModel.viewModelScope.launch {
                    userViewModel.removeFromHousehold(user)
                }
                bottomSheetMenu?.dismiss()
            }
        )
        activity?.let {
            bottomSheetMenu = BottomSheetMenu(it, user.name, items)
            bottomSheetMenu?.show()
        }
    }
}
