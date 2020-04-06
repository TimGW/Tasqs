package com.timgortworst.roomy.presentation.features.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.timgortworst.roomy.databinding.RowUserListBinding
import com.timgortworst.roomy.domain.entity.User

class UserAdapter(
    private val users: MutableList<User>,
    private val onLongClickListener: OnLongClickListener
) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutBinding = RowUserListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(layoutBinding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(users[position])
    }

    override fun getItemCount(): Int {
        return users.size
    }

    fun addAll(users: List<User>) {
        this.users.clear()
        this.users.addAll(users)
        notifyDataSetChanged()
    }

    fun getUser(userId: String): User? {
       return users.find { it.userId == userId }
    }

    fun remove(userId: String) {
        val index= users.indexOfFirst { it.userId == userId }
        users.removeAt(index)
        notifyItemRemoved(index)
    }

    inner class ViewHolder(private val binding: RowUserListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            binding.userClickListener = onLongClickListener
            binding.user = user
        }
    }
}