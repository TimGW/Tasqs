package com.timgortworst.roomy.presentation.features.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.roomy.databinding.RowUserListBinding
import com.timgortworst.roomy.domain.model.User
import com.timgortworst.roomy.presentation.base.view.AdapterStateListener

class UserFirestoreAdapter(
    private val adapterStateListener: AdapterStateListener,
    options: FirestoreRecyclerOptions<User>
) : FirestoreRecyclerAdapter<User, UserFirestoreAdapter.ViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutBinding = RowUserListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(layoutBinding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int, user: User) {
        viewHolder.bind(user)
    }

    override fun getItemCount(): Int = snapshots.size

    override fun onDataChanged() {
        adapterStateListener.onDataChanged(itemCount)
    }

    override fun onError(e: FirebaseFirestoreException) {
        adapterStateListener.onError(e)
    }

    inner class ViewHolder(private val binding: RowUserListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            binding.user = user
        }
    }
}