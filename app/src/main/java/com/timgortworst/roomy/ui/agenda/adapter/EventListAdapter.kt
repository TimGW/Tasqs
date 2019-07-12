package com.timgortworst.roomy.ui.agenda.adapter

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.timgortworst.roomy.R
import com.timgortworst.roomy.model.Event
import com.timgortworst.roomy.model.EventMetaData
import java.util.*

/**
 * Recyclerview adapter for handling the list items in the task overview
 *
 * Handles clicks by expanding items to show a more detailed description of the HouseholdTask
 */
class EventListAdapter(
    private var activity: AppCompatActivity,
    private var events: MutableList<Event>
) : RecyclerView.Adapter<EventListAdapter.ViewHolder>(), Filterable {
    private var filteredEvents: MutableList<Event>

    init {
        filteredEvents = events
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.household_event_list_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val event = filteredEvents[position]

        val date = Date(event.eventMetaData.repeatStartDate)
        val oldColors = viewHolder.dateTime.textColors

        viewHolder.dateTime.text = if (date < Date()) {
            viewHolder.dateTime.setTextColor(ContextCompat.getColor(viewHolder.itemView.context, R.color.error))
            viewHolder.dateTime.setTypeface(null, Typeface.BOLD)
            activity.getString(R.string.overdue_occurance, date.toString())
        } else {
            viewHolder.dateTime.setTextColor(oldColors)
            viewHolder.dateTime.setTypeface(null, Typeface.NORMAL)
            activity.getString(R.string.next_occurance, date.toString())
        }

        viewHolder.user.text = event.user.name
        viewHolder.description.text = event.eventCategory.name

        if (event.eventMetaData.repeatInterval != EventMetaData.RepeatingInterval.SINGLE_EVENT) {
            viewHolder.repeatLabel.visibility = View.VISIBLE
        } else {
            viewHolder.repeatLabel.visibility = View.GONE
        }
    }

    fun setEventList(events: MutableList<Event>) {
        this.filteredEvents.clear()
        this.filteredEvents.addAll(events)
        notifyDataSetChanged()
    }

    fun removeEvent(position: Int){
        this.filteredEvents.removeAt(position)
        notifyItemRemoved(position)
    }

    fun removeEvent(event: Event) {
        val pos = this.filteredEvents.indexOf(event)
        removeEvent(pos)
    }

    fun getEvent(position: Int) = this.filteredEvents[position]

    fun addEvent(event: Event) {
        this.filteredEvents.add(event)
        notifyDataSetChanged()
    }

    fun updateEvent(event: Event) {
        val pos = this.filteredEvents.indexOf(event)
        notifyItemChanged(pos)
    }

    fun getEventList() = this.filteredEvents

    override fun getItemCount(): Int {
        return filteredEvents.size
    }

    fun clearFilter() {
        this.filteredEvents = events
        notifyDataSetChanged()
    }

    override fun getFilter() = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val pattern = constraint.toString()
            filteredEvents = if (pattern.isEmpty()) {
                events
            } else {
                val filteredList = mutableListOf<Event>()
                for (event in events) {
                    if (event.user.userId.contains(pattern)) {
                        filteredList.add(event)
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
        val repeatLabel: ImageView

        init {
            this.user = view.findViewById(R.id.event_user)
            this.dateTime = view.findViewById(R.id.event_date_time)
            this.description = view.findViewById(R.id.event_name)
            this.repeatLabel = view.findViewById(R.id.event_repeat_label)
        }
    }
}