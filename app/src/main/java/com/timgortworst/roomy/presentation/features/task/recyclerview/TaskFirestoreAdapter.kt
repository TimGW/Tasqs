package com.timgortworst.roomy.presentation.features.task.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.common.ChangeEventType
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.roomy.R
import com.timgortworst.roomy.domain.model.Task
import com.timgortworst.roomy.domain.model.TaskRecurrence
import com.timgortworst.roomy.presentation.base.customview.RepeatIcon
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import java.util.*


/**
 * Recyclerview adapter for handling the list items in the task overview
 *
 * Handles clicks by expanding items to show a more detailed description of the category
 */
class TaskFirestoreAdapter(
    private val taskDoneClickListener: TaskDoneClickListener,
    private val adapterStateListener: AdapterStateListener,
    options: FirestoreRecyclerOptions<Task>
) : FirestoreRecyclerAdapter<Task, TaskFirestoreAdapter.ViewHolder>(options) {
    var tracker: SelectionTracker<String>? = null

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.row_task_list, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int, task: Task) {
        tracker?.let {
            viewHolder.bind(task, it.isSelected(task.id))
        }
    }

    override fun getItemId(position: Int): Long {
        val task: Task = snapshots[position]
        return task.id.hashCode().toLong()
    }

    override fun getItemCount(): Int  = snapshots.size

//    override fun onChildChanged(
//        type: ChangeEventType,
//        snapshot: DocumentSnapshot,
//        newIndex: Int,
//        oldIndex: Int
//    ) {
//        super.onChildChanged(type, snapshot, newIndex, oldIndex)
//        when (type) { //todo set notifications
//            ChangeEventType.ADDED -> {
////                setNotificationReminder(event, snapshot.metadata.hasPendingWrites())
//            }
//            ChangeEventType.CHANGED -> {
////                view.removePendingNotificationReminder(event.eventId)
////                setNotificationReminder(event, snapshot.metadata.hasPendingWrites())
//            }
//            ChangeEventType.REMOVED -> {
////                view.removePendingNotificationReminder(event.eventId)
//            }
//            ChangeEventType.MOVED -> { }
//        }
//    }

    override fun onDataChanged() {
//        adapterStateListener.hideLoadingState()
        adapterStateListener.onDataState(View.VISIBLE)
        adapterStateListener.onErrorState(View.GONE)
        val visibility = if (itemCount == 0) View.VISIBLE else View.GONE
        adapterStateListener.onEmptyState(visibility)
    }

    override fun onError(e: FirebaseFirestoreException) {
        adapterStateListener.onDataState(View.GONE)
        adapterStateListener.onErrorState(View.VISIBLE, e)
    }

    fun getPosition(id: String): Int {
        return snapshots.indexOfFirst { it.id == id }
    }

//    private fun setNotificationReminder(event: Event, hasPendingWrites: Boolean) {
//        if (hasPendingWrites && event.user.userId == uId) {
//            callback.enqueueNotification(
//                event.eventId,
//                event.metaData,
//                event.description,
//                event.user.name
//            )
//        }
//    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val user: TextView = view.findViewById(R.id.task_user)
        private val dateTime: TextView = view.findViewById(R.id.task_date_time)
        private val description: TextView = view.findViewById(R.id.task_name)
        private val repeatIcon: RepeatIcon = view.findViewById(R.id.task_repeat_label)
        private val taskDone: MaterialButton = view.findViewById(R.id.task_done)

        fun bind(task: Task, isActivated: Boolean) {
            val dateTimeTask = task.metaData.startDateTime
            description.text = task.description

            repeatIcon.setRepeatLabelText(task.metaData.recurrence)
            if (task.metaData.recurrence !is TaskRecurrence.SingleTask) {
                repeatIcon.visibility = View.VISIBLE
            } else {
                repeatIcon.visibility = View.GONE
            }

            dateTime.text = formatDate(dateTimeTask)
            if (dateTimeTask.isBefore(ZonedDateTime.now())) {
                dateTime.setTextColor(ContextCompat.getColor(itemView.context, R.color.color_error))
            } else {
                dateTime.setTextColor(description.currentTextColor)
            }

            user.visibility = if (task.user.name.isNotBlank()) {
                View.VISIBLE
            } else {
                View.GONE
            }

            user.text = task.user.name.capitalize()

            taskDone.setOnClickListener {
                taskDoneClickListener.onTaskDoneClicked(task, adapterPosition) }

            itemView.isActivated = isActivated
        }

        private fun formatDate(zonedDateTime: ZonedDateTime) = zonedDateTime.format(
                DateTimeFormatter
                        .ofLocalizedDate(FormatStyle.MEDIUM)
                        .withLocale(Locale.getDefault())
        )

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<String> =
                object : ItemDetailsLookup.ItemDetails<String>() {
                    override fun getPosition(): Int = adapterPosition
                    override fun getSelectionKey(): String? = snapshots[position].id
                }
    }
}