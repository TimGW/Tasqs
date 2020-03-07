package com.timgortworst.roomy.presentation.features.user

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.roomy.R
import com.timgortworst.roomy.databinding.FragmentUserListBinding
import com.timgortworst.roomy.domain.model.User
import com.timgortworst.roomy.domain.model.firestore.TaskJson
import com.timgortworst.roomy.presentation.base.view.AdapterStateListener
import com.timgortworst.roomy.presentation.base.view.BaseFragment
import com.timgortworst.roomy.presentation.features.main.MainActivity
import org.koin.android.viewmodel.ext.android.viewModel

class UserListFragment : BaseFragment(),
    AdapterStateListener,
    UserFirestoreAdapter.OnUserLongClickListener {
    private lateinit var userListAdapter: UserFirestoreAdapter
    private lateinit var parentActivity: MainActivity
    private var _binding: FragmentUserListBinding? = null
    private val binding get() = _binding!!
    private val userViewModel by viewModel<UserViewModel>()

    companion object {
        fun newInstance(): UserListFragment {
            return UserListFragment()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        parentActivity = (activity as? MainActivity) ?: return
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentUserListBinding.inflate(inflater, container, false)

        setupRecyclerView()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel.data.observe(viewLifecycleOwner, Observer { networkResponse ->
            networkResponse?.let {
                val options = it.setLifecycleOwner(this).build()
                userListAdapter.updateOptions(options)
            }
        })

        userViewModel.showLoading.observe(viewLifecycleOwner, Observer { networkResponse ->
            networkResponse?.let {
                if (it) {
                    toggleFadeViews(binding.recyclerView, binding.progress.root)
                } else {
                    toggleFadeViews(binding.progress.root, binding.recyclerView)
                }
            }
        })
    }

    private fun setupRecyclerView() {
        // todo remove this placeholder options
        val query = FirebaseFirestore.getInstance().collection(TaskJson.TASK_COLLECTION_REF).whereEqualTo(
            TaskJson.TASK_HOUSEHOLD_ID_REF, "")
        val defaultOptions = FirestoreRecyclerOptions
            .Builder<User>()
            .setQuery(query, User::class.java)
            .build()

        userListAdapter = UserFirestoreAdapter(this, this, defaultOptions)

        binding.recyclerView.apply {
            val linearLayoutManager = LinearLayoutManager(parentActivity)
            val dividerItemDecoration = DividerItemDecoration(parentActivity, linearLayoutManager.orientation)
            layoutManager = linearLayoutManager
            adapter = userListAdapter
            addItemDecoration(dividerItemDecoration)
        }

    }

    override fun onDataChanged(itemCount: Int) {
        binding.recyclerView.visibility = View.VISIBLE
        val visibility = if (itemCount == 0) View.VISIBLE else View.GONE
        setMsgView(
            visibility,
            R.string.empty_list_state_title_users,
            R.string.empty_list_state_text_users
        )
    }

    override fun onError(e: FirebaseFirestoreException) {
        binding.recyclerView.visibility = View.GONE
        setMsgView(
            View.VISIBLE,
            R.string.error_list_state_title,
            R.string.error_list_state_text
        )
    }

    private fun hideLoadingState() {
        val animationDuration = resources.getInteger(android.R.integer.config_mediumAnimTime)

        binding.recyclerView.apply {
            alpha = 0f
            visibility = View.VISIBLE

            animate()
                .alpha(1f)
                .setDuration(animationDuration.toLong())
                .setListener(null)
        }

        binding.progress.root.animate()
            .alpha(0f)
            .setDuration(animationDuration.toLong())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    binding.progress.root.visibility = View.GONE
                }
            })
    }

    private fun setMsgView(isVisible: Int, title: Int?, text: Int?) {
        binding.layoutListState.apply {
            title?.let { this.stateTitle.text = parentActivity.getString(it) }
            text?.let { this.stateMessage.text = parentActivity.getString(it) }
            root.visibility = isVisible
        }
    }

    override fun onUserClick(user: User) {
//       userViewModel.userAdminObservable(user).observe(viewLifecycleOwner, Observer {
//           it?.let {
//               showContextMenuFor(user)
//           }
//       })
    }
//
//    private fun showContextMenuFor(user: User) {
//        var bottomSheetMenu: BottomSheetMenu? = null
//
//        val items = arrayListOf(
//            BottomMenuItem(R.drawable.ic_delete, "Delete") {
//                userViewModel.viewModelScope.launch {
////                    userViewModel.deleteUser(user) todo
//                }
//                bottomSheetMenu?.dismiss()
//            }
//        )
//        bottomSheetMenu = BottomSheetMenu(parentActivity, user.name, items)
//        bottomSheetMenu.show()
//    }
}
