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
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.roomy.R
import com.timgortworst.roomy.databinding.FragmentUserListBinding
import com.timgortworst.roomy.domain.model.BottomMenuItem
import com.timgortworst.roomy.domain.model.User
import com.timgortworst.roomy.domain.model.firestore.EventJson
import com.timgortworst.roomy.presentation.base.customview.BottomSheetMenu
import com.timgortworst.roomy.presentation.features.event.recyclerview.AdapterStateListener
import com.timgortworst.roomy.presentation.features.main.MainActivity
import kotlinx.android.synthetic.main.fragment_event_list.*
import kotlinx.android.synthetic.main.layout_list_state.view.*
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

class UserListFragment : Fragment(), AdapterStateListener,
    FirestoreUserAdapter.OnUserLongClickListener {
    private lateinit var userListAdapter: FirestoreUserAdapter
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
        createFireStoreRvAdapter()

        return binding.root
    }

    fun setupRecyclerView() {
        // todo remove this placeholder options
        val query = FirebaseFirestore.getInstance().collection(EventJson.EVENT_COLLECTION_REF).whereEqualTo(
            EventJson.EVENT_HOUSEHOLD_ID_REF, "")
        val defaultOptions = FirestoreRecyclerOptions
            .Builder<User>()
            .setQuery(query, User::class.java)
            .build()

        userListAdapter = FirestoreUserAdapter(this, this, defaultOptions)

        binding.recyclerView.apply {
            val linearLayoutManager = LinearLayoutManager(parentActivity)
            val dividerItemDecoration = DividerItemDecoration(parentActivity, linearLayoutManager.orientation)
            layoutManager = linearLayoutManager
            adapter = userListAdapter
            addItemDecoration(dividerItemDecoration)
        }

    }
    private fun createFireStoreRvAdapter() = userViewModel.fetchFireStoreRecyclerOptionsBuilder()
        .observe(viewLifecycleOwner, Observer { networkResponse ->
            networkResponse?.let {
                val options = it.setLifecycleOwner(this).build()
                userListAdapter.updateOptions(options)
            }
        })

    override fun onEmptyState(isVisible: Int) {
        setMsgView(
            isVisible,
            R.string.empty_list_state_title_users,
            R.string.empty_list_state_text_users
        )
    }

    override fun hideLoadingState() {
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

    override fun onErrorState(isVisible: Int, e: FirebaseFirestoreException?) {
        // todo handle specific errors
        setMsgView(
            isVisible,
            R.string.error_list_state_title,
            R.string.error_list_state_text
        )
    }

    private fun setMsgView(isVisible: Int, title: Int?, text: Int?) {
        layout_list_state?.apply {
            title?.let { this.state_title.text = parentActivity.getString(it) }
            text?.let { this.state_message.text = parentActivity.getString(it) }
            visibility = isVisible
        }
    }

    override fun onUserClick(user: User) {
       userViewModel.userAdminObservable(user).observe(viewLifecycleOwner, Observer {
           it?.let {
               showContextMenuFor(user)
           }
       })
    }

    private fun showContextMenuFor(user: User) {
        var bottomSheetMenu: BottomSheetMenu? = null

        val items = arrayListOf(
            BottomMenuItem(R.drawable.ic_delete, "Delete") {
                userViewModel.viewModelScope.launch {
                    userViewModel.deleteUser(user)
                }
                bottomSheetMenu?.dismiss()
            }
        )
        bottomSheetMenu = BottomSheetMenu(parentActivity, user.name, items)
        bottomSheetMenu.show()
    }
}
