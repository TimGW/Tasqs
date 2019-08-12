package com.timgortworst.roomy.presentation.features.event.adapter

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.timgortworst.roomy.BuildConfig
import com.timgortworst.roomy.R
import com.timgortworst.roomy.data.model.Event
import com.timgortworst.roomy.data.model.EventMetaData
import com.timgortworst.roomy.domain.utils.isTimeStampInPast
import com.timgortworst.roomy.presentation.base.customview.RepeatIcon
import java.text.SimpleDateFormat
import java.util.*


/**
 * Recyclerview adapter for handling the list items in the task overview
 *
 * Handles clicks by expanding items to show a more detailed description of the category
 */
class EventListAdapter(
        private var activity: AppCompatActivity
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

        val formatter = if (BuildConfig.DEBUG) {
            SimpleDateFormat("dd MMM yyyy HH:mm:ss.SSS", Locale.getDefault())
        } else {
            SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        }
        val formattedDate = formatter.format(Date(event.eventMetaData.nextEventDate))

        viewHolder.dateTime.text = if (event.eventMetaData.nextEventDate.isTimeStampInPast()) {
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

        viewHolder.repeatIcon.setRepeatLabelText(event.eventMetaData.repeatInterval)
        if (event.eventMetaData.repeatInterval != EventMetaData.RepeatingInterval.SINGLE_EVENT) {
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
            it.eventMetaData.nextEventDate <= event.eventMetaData.nextEventDate
        } + 1
        filteredEvents.add(newAddIndex, event)
        notifyItemInserted(newAddIndex)
    }

    fun updateEvent(event: Event) {
        val fromPosition = filteredEvents.indexOfFirst { event.eventId == it.eventId }
        filteredEvents[fromPosition] = event
        notifyItemChanged(fromPosition)

        val toPosition = filteredEvents.indexOfLast { event.eventMetaData.nextEventDate > it.eventMetaData.nextEventDate }

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