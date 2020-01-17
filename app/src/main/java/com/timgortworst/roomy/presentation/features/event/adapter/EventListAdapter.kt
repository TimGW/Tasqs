package com.timgortworst.roomy.presentation.features.event.adapter

import android.app.Activity
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.timgortworst.roomy.R
import com.timgortworst.roomy.data.model.Event
import com.timgortworst.roomy.data.model.EventMetaData
import com.timgortworst.roomy.domain.utils.isDateInPast
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
class EventListAdapter(
        private val activity: Activity
) : RecyclerView.Adapter<EventListAdapter.ViewHolder>(), Filterable {
    private var filteredEvents: MutableList<Event>
    private var events: MutableList<Event> = mutableListOf()

    init {
        filteredEvents = events
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_event_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val event = filteredEvents[position]
        val dateTimeEvent = event.eventMetaData.eventTimestamp
        val formattedDate = formatDate(dateTimeEvent)

        viewHolder.dateTime.text = if (dateTimeEvent.isDateInPast()) {
            viewHolder.dateTime.setTextColor(ContextCompat.getColor(viewHolder.itemView.context, R.color.error))
            viewHolder.dateTime.setTypeface(null, Typeface.BOLD)
            activity.getString(R.string.event_overdue, formattedDate)
        } else {
            viewHolder.dateTime.setTextColor(viewHolder.description.currentTextColor)
            viewHolder.dateTime.setTypeface(null, Typeface.NORMAL)
            activity.getString(R.string.event_next, formattedDate)
        }

        viewHolder.user.text = event.user.name.capitalize()
        viewHolder.description.text = event.eventCategory.name

        viewHolder.repeatIcon.setRepeatLabelText(event.eventMetaData.eventInterval)
        if (event.eventMetaData.eventInterval != EventMetaData.EventInterval.SINGLE_EVENT) {
            viewHolder.repeatIcon.visibility = View.VISIBLE
        } else {
            viewHolder.repeatIcon.visibility = View.GONE
        }
    }

    fun removeEvent(event: Event) {
        val position = filteredEvents.indexOfFirst { event.eventId == it.eventId }
        filteredEvents.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getEvent(position: Int) = filteredEvents[position]

    fun addEvent(event: Event) {
        val newAddIndex = filteredEvents.indexOfLast {
            it.eventMetaData.eventTimestamp <= event.eventMetaData.eventTimestamp
        } + 1
        filteredEvents.add(newAddIndex, event)
        notifyItemInserted(newAddIndex)
    }

    fun updateEvent(event: Event) {
        val fromPosition = filteredEvents.indexOfFirst { event.eventId == it.eventId }
        filteredEvents[fromPosition] = event
        notifyItemChanged(fromPosition)

        val toPosition = filteredEvents.indexOfLast { event.eventMetaData.eventTimestamp > it.eventMetaData.eventTimestamp }

        // update data array if item is not on first or last position
        if (toPosition != RecyclerView.NO_POSITION &&
                filteredEvents.lastIndex != fromPosition &&
                toPosition > fromPosition
        ) {
            val item = filteredEvents[fromPosition]
            filteredEvents.removeAt(fromPosition)
            filteredEvents.add(toPosition, item)

            // notify adapter
            notifyItemMoved(fromPosition, toPosition)
        }
    }

    override fun getItemCount(): Int {
        return filteredEvents.size
    }

    fun clearFilter() {
        filteredEvents = events
        notifyDataSetChanged()
    }

    override fun getFilter() = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val pattern = constraint.toString()
            filteredEvents = if (pattern.isEmpty()) {
                events
            } else {
                val filteredList = mutableListOf<Event>()

                events.forEach {
                    if (it.user.userId.contains(pattern)) {
                        filteredList.add(it)
                    }
                }
                filteredList
            }

            val filterResults = FilterResults()
            filterResults.values = filteredEvents
            return filterResults
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            filteredEvents = results.values as MutableList<Event>
            notifyDataSetChanged()
        }
    }

    fun formatDate(zonedDateTime: ZonedDateTime) : String {
        val formatter = DateTimeFormatter
                .ofLocalizedDate(FormatStyle.MEDIUM)
                .withLocale(Locale.getDefault())
        return zonedDateTime.format(formatter)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val user: TextView
        val dateTime: TextView
        val description: TextView
        val repeatIcon: RepeatIcon

        init {
            this.user = view.findViewById(R.id.event_user)
            this.dateTime = view.findViewById(R.id.event_date_time)
            this.description = view.findViewById(R.id.event_name)
            this.repeatIcon = view.findViewById(R.id.event_repeat_label)
        }
    }
}