package com.timgortworst.roomy.ui.user.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.timgortworst.roomy.R
import com.timgortworst.roomy.model.Role
import com.timgortworst.roomy.model.User

/**
 * Recyclerview adapter for handling the list items in the task overview
 *
 * Handles clicks by expanding items to show a more detailed description of the HouseholdTask
 */
class UserListAdapter(
        private val onUserLongClickListener: OnUserLongClickListener
) : RecyclerView.Adapter<UserListAdapter.ViewHolder>() {
    private var users: MutableList<User> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_user_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val user = users[position]

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

    override fun getItemCount(): Int {
        return users.size
    }

    fun removeUser(user: User) {
        val index = users.indexOf(user)
        users.removeAt(index)
        notifyItemRemoved(index)
    }

    fun addUser(user: User) {
        val index = if (user.role == Role.ADMIN.name) {
            users.add(0, user)
            0
        } else {
            users.add(user)
            users.lastIndex
        }
        notifyItemInserted(index)
    }

    fun updateUser(user: User) {
        val fromPosition = users.indexOf(user)
        notifyItemChanged(fromPosition)

        val toPosition = users.indexOfFirst { user.role != Role.ADMIN.name }

        // update data array if item is not on first or last position
        if (toPosition != RecyclerView.NO_POSITION &&
                users.lastIndex != fromPosition &&
                toPosition > fromPosition
        ) {
            val item = users[fromPosition]
            users.removeAt(fromPosition)
            users.add(toPosition, item)

            // notify adapter
            notifyItemMoved(fromPosition, toPosition)
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userTitle: TextView
        val adminLabel: TextView

        init {
            this.userTitle = view.findViewById(R.id.user_title)
            this.adminLabel = view.findViewById(R.id.admin_label)
        }
    }

    interface OnUserLongClickListener {
        fun onUserClick(user: User)
    }
}