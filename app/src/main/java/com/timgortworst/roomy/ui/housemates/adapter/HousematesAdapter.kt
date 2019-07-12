package com.timgortworst.roomy.ui.housemates.adapter

import androidx.appcompat.app.AppCompatActivity
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
class HousematesAdapter(
    private var activity: AppCompatActivity,
    private var users: MutableList<User>
) : RecyclerView.Adapter<HousematesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.household_user_list_row, parent, false)
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
    }

    fun setUsers(users: MutableList<User>) {
        this.users.clear()
        this.users = users
        notifyItemRangeInserted(0, this.users.size)
    }

    override fun getItemCount(): Int {
        return users.size
    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userTitle: TextView
        val adminLabel: TextView

        init {
            this.userTitle = view.findViewById(R.id.user_title)
            this.adminLabel = view.findViewById(R.id.admin_label)
        }
    }
}