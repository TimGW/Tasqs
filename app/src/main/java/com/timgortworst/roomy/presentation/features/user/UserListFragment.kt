package com.timgortworst.roomy.presentation.features.user

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.timgortworst.roomy.R
import com.timgortworst.roomy.databinding.FragmentUserListBinding
import com.timgortworst.roomy.presentation.base.model.BottomMenuItem
import com.timgortworst.roomy.domain.entity.User
import com.timgortworst.roomy.domain.entity.response.Response
import com.timgortworst.roomy.domain.utils.snackbar
import com.timgortworst.roomy.presentation.base.model.EventObserver
import com.timgortworst.roomy.presentation.base.customview.BottomSheetMenu
import com.timgortworst.roomy.presentation.features.main.MainActivity
import org.koin.android.viewmodel.ext.android.viewModel

// todo refactor logic to viewmodel / usecase
class UserListFragment : Fragment(), OnLongClickListener {
    private lateinit var userAdapter: UserAdapter
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel.userOptions.observe(viewLifecycleOwner,
            EventObserver {
                showBottomMenuFor(it)
            })

        userViewModel.removedUser.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Response.Success -> { response.data?.let { user ->
                        userAdapter.remove(user) // update local list

                        parentActivity.binding.bottomNavigationContainer.snackbar(
                            message = getString(R.string.removed, user.name),
                            anchorView = parentActivity.binding.fab
                        )
                    }

                }
                is Response.Error -> {
                    parentActivity.binding.bottomNavigationContainer.snackbar(
                        message = getString(R.string.users_removing_error),
                        anchorView = parentActivity.binding.fab
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
            val linearLayoutManager = LinearLayoutManager(parentActivity)
            val dividerItemDecoration =
                DividerItemDecoration(parentActivity, linearLayoutManager.orientation)
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
