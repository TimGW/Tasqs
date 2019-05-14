package com.timgortworst.roomy.ui.agenda.adapter

import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
) : RecyclerView.Adapter<EventListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.household_event_list_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val event = events[position]

        val date = Date(event.eventMetaData.repeatStartDate)

        viewHolder.user.text = event.user.name
        viewHolder.dateTime.text = activity.getString(R.string.next_occurance, date.toString())
        viewHolder.description.text = event.eventCategory.name
        viewHolder.points.text = event.eventCategory.points.toString()
    }

    fun setEventList(events: MutableList<Event>) {
        this.events.clear()
        this.events.addAll(events)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return events.size
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