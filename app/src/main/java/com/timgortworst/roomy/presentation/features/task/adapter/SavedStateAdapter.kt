package com.timgortworst.roomy.presentation.features.task.adapter

import android.os.Bundle
import android.os.Parcelable
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

/**
 * A [FirestoreRecyclerAdapter] whose state can be saved regardless of database connection changes.
 *
 * This adapter will save its state across basic stop/start listening lifecycles, config changes,
 * and even full blown process death. Extenders _must_ call [SavedStateAdapter.onSaveInstanceState]
 * in the Activity/Fragment holding the adapter.
 *
 * fixme
 */
abstract class SavedStateAdapter<T, VH : RecyclerView.ViewHolder>(
    options: FirestoreRecyclerOptions<T>,
    savedInstanceState: Bundle?,
    private val recyclerView: RecyclerView
) : FirestoreRecyclerAdapter<T, VH>(options) {
    private val RecyclerView.state get() = layoutManager?.onSaveInstanceState()
    private var savedState: Parcelable? = savedInstanceState?.getParcelable(SAVED_STATE_KEY)

    fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(SAVED_STATE_KEY, recyclerView.state)
    }

    override fun stopListening() {
        // Save RV state before adapter cleanup occurs
        savedState = recyclerView.state
        super.stopListening()
    }

    override fun onDataChanged() {
        savedState?.let { recyclerView.layoutManager?.onRestoreInstanceState(it) }
        savedState = null
    }

    private companion object {
        const val SAVED_STATE_KEY = "layout_manager_saved_state"
    }
}