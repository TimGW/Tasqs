package com.timgortworst.tasqs.presentation.features.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.timgortworst.tasqs.R
import com.timgortworst.tasqs.databinding.FragmentUserListBinding
import com.timgortworst.tasqs.domain.model.User
import com.timgortworst.tasqs.domain.model.response.Response
import com.timgortworst.tasqs.presentation.base.customview.BottomSheetMenu
import com.timgortworst.tasqs.presentation.base.model.BottomMenuItem
import com.timgortworst.tasqs.presentation.base.model.EventObserver
import com.timgortworst.tasqs.infrastructure.extension.snackbar
import com.timgortworst.tasqs.presentation.features.main.MainActivity
import org.koin.android.viewmodel.ext.android.viewModel

class UserListFragment : Fragment(), OnLongClickListener {
    private lateinit var userAdapter: UserAdapter
    private var bottomSheetMenu: BottomSheetMenu? = null
    private var _binding: FragmentUserListBinding? = null
    private val binding get() = _binding!!
    private val userViewModel by viewModel<UserViewModel>()

    companion object {
        fun newInstance(): UserListFragment {
            return UserListFragment()
        }
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel.userOptions.observe(viewLifecycleOwner, EventObserver {showBottomMenuFor(it)})

        userViewModel.userRemoved.observe(viewLifecycleOwner, Observer { response ->
            val activity = (activity as? MainActivity) ?: return@Observer
            when (response) {
                is Response.Success -> {
                    response.data?.let { userId ->
                        val userName = userAdapter.getUser(userId)?.name.orEmpty()
                        activity.binding.bottomNavigationContainer.snackbar(
                            message = getString(R.string.removed, userName),
                            anchorView = activity.binding.fab
                        )

                        userAdapter.remove(userId) // update local list
                    }
                }
                is Response.Error -> {
                    activity.binding.bottomNavigationContainer.snackbar(
                        message = getString(R.string.users_removing_error),
                        anchorView = activity.binding.fab
                    )
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        userAdapter = UserAdapter(mutableListOf(), this@UserListFragment)
        binding.recyclerView.apply {
            val linearLayoutManager = LinearLayoutManager(activity)
            val dividerItemDecoration = DividerItemDecoration(activity, linearLayoutManager.orientation)
            layoutManager = linearLayoutManager
            adapter = userAdapter
            addItemDecoration(dividerItemDecoration)
        }
    }

    override fun onLongClick(user: User): Boolean {
        userViewModel.shouldDisplayBottomSheetFor(user)
        return true
    }

    private fun showBottomMenuFor(user: User) {
        val items = arrayListOf(
            BottomMenuItem(
                R.drawable.ic_delete,
                getString(R.string.delete)
            ) {
                userViewModel.removeFromHousehold(user)
                bottomSheetMenu?.dismiss()
            }
        )
        activity?.let {
            bottomSheetMenu = BottomSheetMenu(it, user.name, items)
            bottomSheetMenu?.show()
        }
    }
}
