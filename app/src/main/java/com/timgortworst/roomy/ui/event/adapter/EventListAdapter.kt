package com.timgortworst.roomy.ui.event.adapter

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
import com.timgortworst.roomy.model.Event
import com.timgortworst.roomy.model.EventMetaData
import com.timgortworst.roomy.utils.isTimeStampInPast
import java.text.SimpleDateFormat
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
        val view = LayoutInflater.from(parent.context).inflate(com.timgortworst.roomy.R.layout.household_event_list_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val event = filteredEvents[position]

        val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val formattedDate = formatter.format(Date(event.eventMetaData.repeatStartDate))

        viewHolder.dateTime.text = if (event.eventMetaData.repeatStartDate.isTimeStampInPast()) {
            viewHolder.dateTime.setTextColor(ContextCompat.getColor(viewHolder.itemView.context, com.timgortworst.roomy.R.color.error))
            viewHolder.dateTime.setTypeface(null, Typeface.BOLD)
            activity.getString(com.timgortworst.roomy.R.string.overdue_occurance, formattedDate)
        } else {
            viewHolder.dateTime.setTextColor(viewHolder.description.currentTextColor)
            viewHolder.dateTime.setTypeface(null, Typeface.NORMAL)
            activity.getString(com.timgortworst.roomy.R.string.next_occurance, formattedDate)
        }

        viewHolder.user.text = event.user.name
        viewHolder.description.text = event.eventCategory.name

        if (event.eventMetaData.repeatInterval != EventMetaData.RepeatingInterval.SINGLE_EVENT) {
            viewHolder.repeatLabel.visibility = View.VISIBLE
        } else {
            viewHolder.repeatLabel.visibility = View.GONE
        }
    }

    fun removeEvent(position: Int){
        filteredEvents.removeAt(position)
        notifyItemRemoved(position)
    }

    fun removeEvent(event: Event) {
        val pos = filteredEvents.indexOf(event)
        removeEvent(pos)
    }

    fun getEvent(position: Int) = filteredEvents[position]

    fun addEvent(event: Event) {
        val newAddIndex = filteredEvents.indexOfLast {
            it.eventMetaData.repeatStartDate <= event.eventMetaData.repeatStartDate
        } + 1
        filteredEvents.add(newAddIndex, event)
        notifyItemInserted(newAddIndex)
    }

    fun updateEvent(event: Event) {
        val fromPosition = filteredEvents.indexOf(event)
        notifyItemChanged(fromPosition)

        val toPosition = filteredEvents.indexOfLast { it.eventMetaData.repeatStartDate < event.eventMetaData.repeatStartDate }

        // update data array
        if(toPosition != RecyclerView.NO_POSITION){
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
            this.user = view.findViewById(com.timgortworst.roomy.R.id.event_user)
            this.dateTime = view.findViewById(com.timgortworst.roomy.R.id.event_date_time)
            this.description = view.findViewById(com.timgortworst.roomy.R.id.event_name)
            this.repeatLabel = view.findViewById(com.timgortworst.roomy.R.id.event_repeat_label)
        }
    }
}