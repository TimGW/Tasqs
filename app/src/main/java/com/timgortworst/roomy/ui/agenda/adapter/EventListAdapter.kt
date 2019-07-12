package com.timgortworst.roomy.ui.agenda.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.timgortworst.roomy.R
import com.timgortworst.roomy.model.Event
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

        viewHolder.user.text = event.user.name
        viewHolder.dateTime.text = activity.getString(R.string.next_occurance, date.toString())
        viewHolder.description.text = event.eventCategory.name
    }

    fun setEventList(events: MutableList<Event>) {
        this.filteredEvents.clear()
        this.filteredEvents.addAll(events)
        notifyDataSetChanged()
    }

    fun addEvent(event: Event) {
        this.filteredEvents.add(event)
        notifyDataSetChanged()
    }

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
        val points: TextView

        init {
            this.user = view.findViewById(R.id.event_user)
            this.dateTime = view.findViewById(R.id.event_date_time)
            this.description = view.findViewById(R.id.event_name)
            this.points = view.findViewById(R.id.points_label)
        }
    }
}