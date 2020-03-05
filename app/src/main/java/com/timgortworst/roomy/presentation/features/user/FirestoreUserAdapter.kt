package com.timgortworst.roomy.presentation.features.user

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.roomy.R
import com.timgortworst.roomy.domain.model.Role
import com.timgortworst.roomy.domain.model.User
import com.timgortworst.roomy.presentation.features.task.recyclerview.AdapterStateListener


/**
 * Recyclerview adapter for handling the list items in the task overview
 *
 * Handles clicks by expanding items to show a more detailed description of the category
 */
class FirestoreUserAdapter(
    private val onUserLongClickListener: OnUserLongClickListener,
    private val adapterStateListener: AdapterStateListener,
    options: FirestoreRecyclerOptions<User>
) : FirestoreRecyclerAdapter<User, FirestoreUserAdapter.ViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.row_user_list, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int, user: User) {
        viewHolder.userTitle.text = user.name

        if (user.role == Role.ADMIN.name) {
            viewHolder.adminLabel.visibility = View.VISIBLE
        } else {
            viewHolder.adminLabel.visibility = View.GONE
        }

        viewHolder.itemView.setOnLongClickListener {
            onUserLongClickListener.onUserClick(user)
            true
        }
    }

    override fun getItemCount(): Int = snapshots.size

    override fun onDataChanged() {
        adapterStateListener.hideLoadingState()
        adapterStateListener.onErrorState(View.GONE)
        val visibility = if (itemCount == 0) View.VISIBLE else View.GONE
        adapterStateListener.onEmptyState(visibility)
    }

    override fun onError(e: FirebaseFirestoreException) {
        //todo hide list
        adapterStateListener.onErrorState(View.VISIBLE, e)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userTitle: TextView = view.findViewById(R.id.user_title)
        val adminLabel: TextView = view.findViewById(R.id.admin_label)
    }

    interface OnUserLongClickListener {
        fun onUserClick(user: User)
    }
}