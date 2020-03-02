package com.timgortworst.roomy.presentation.features.event.recyclerview

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
import com.timgortworst.roomy.domain.model.Event
import com.timgortworst.roomy.domain.model.EventRecurrence
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
class FirestoreAdapter(
        private val eventDoneClickListener: EventDoneClickListener,
        private val adapterStateListener: AdapterStateListener,
        options: FirestoreRecyclerOptions<Event>
) : FirestoreRecyclerAdapter<Event, FirestoreAdapter.ViewHolder>(options) {
    var tracker: SelectionTracker<String>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.row_event_list, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int, event: Event) {
        tracker?.let {
            viewHolder.bind(event, it.isSelected(event.eventId))
        }
    }

    override fun getItemCount(): Int  = snapshots.size

    override fun onDataChanged() {
        adapterStateListener.onLoadingState(View.GONE)
        adapterStateListener.onErrorState(View.GONE)
        val visibility = if (itemCount == 0) View.VISIBLE else View.GONE
        adapterStateListener.onEmptyState(visibility)
    }

    override fun onError(e: FirebaseFirestoreException) {
        adapterStateListener.onErrorState(View.VISIBLE, e)
    }

    fun getPosition(eventId: String): Int {
        return snapshots.indexOfFirst { it.eventId == eventId }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val user: TextView = view.findViewById(R.id.event_user)
        private val dateTime: TextView = view.findViewById(R.id.event_date_time)
        private val description: TextView = view.findViewById(R.id.event_name)
        private val repeatIcon: RepeatIcon = view.findViewById(R.id.event_repeat_label)
        private val eventDone: MaterialButton = view.findViewById(R.id.event_done)

        fun bind(event: Event, isActivated: Boolean) {
            val dateTimeEvent = event.metaData.startDateTime
            description.text = event.description

            repeatIcon.setRepeatLabelText(event.metaData.recurrence)
            if (event.metaData.recurrence !is EventRecurrence.SingleEvent) {
                repeatIcon.visibility = View.VISIBLE
            } else {
                repeatIcon.visibility = View.GONE
            }

            dateTime.text = formatDate(dateTimeEvent)
            if (dateTimeEvent.isBefore(ZonedDateTime.now())) {
                dateTime.setTextColor(ContextCompat.getColor(itemView.context, R.color.color_error))
            } else {
                dateTime.setTextColor(description.currentTextColor)
            }

            user.visibility = if (event.user.name.isNotBlank()) {
                View.VISIBLE
            } else {
                View.GONE
            }

            user.text = event.user.name.capitalize()

            eventDone.setOnClickListener {
                eventDoneClickListener.onEventDoneClicked(event, adapterPosition) }

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
                    override fun getSelectionKey(): String? = snapshots[position].eventId
                }
    }
}