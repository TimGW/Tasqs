package com.timgortworst.roomy.presentation.features.task.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.roomy.R
import com.timgortworst.roomy.domain.model.Task
import com.timgortworst.roomy.domain.model.TaskRecurrence
import com.timgortworst.roomy.presentation.base.customview.RepeatIcon
import com.timgortworst.roomy.presentation.base.view.AdapterStateListener
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import java.util.*

class TaskFirestoreAdapter(
    private val taskClickListener: TaskClickListener,
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

    override fun getItemCount(): Int = snapshots.size

    override fun onDataChanged() {
        adapterStateListener.onDataChanged(itemCount)
    }

    override fun onError(e: FirebaseFirestoreException) {
        adapterStateListener.onError(e)
    }

    fun getPosition(id: String): Int {
        return snapshots.indexOfFirst { it.id == id }
    }

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
                taskClickListener.onTaskDoneClicked(task, adapterPosition)
            }
            itemView.setOnClickListener {
                taskClickListener.onTaskInfoClicked(task)
            }

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