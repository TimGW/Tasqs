package com.timgortworst.roomy.ui.eventcategory.adapter

import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter
import com.timgortworst.roomy.R
import com.timgortworst.roomy.model.EventCategory

/**
 * Recyclerview adapter for handling the list items in the task overview
 *
 * Handles clicks by expanding items to show a more detailed description of the HouseholdTask
 */
class EventCategoryAdapter(
    private var activity: AppCompatActivity,
    private var houseHoldTasks: MutableList<EventCategory>,
    private var optionsClickListener: EventCategoryAdapter.OnOptionsClickListener
) : StickyRecyclerHeadersAdapter<EventCategoryAdapter.HeaderViewHolder>,
    RecyclerView.Adapter<EventCategoryAdapter.ViewHolder>() {
    private var mExpandedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.household_tasks_list_row, parent, false)
        return ViewHolder(view)
    }

    override fun onCreateHeaderViewHolder(parent: ViewGroup): HeaderViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.household_tasks_list_header, parent, false)
        return HeaderViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val isExpanded = position == mExpandedPosition
        val householdTask = houseHoldTasks[position]

        viewHolder.taskTitle.text = householdTask.name
        viewHolder.taskPoints.text = activity.getString(R.string.householdtask_points, householdTask.points)
        viewHolder.taskDescription.text = householdTask.description

        /** logic for handling the clicks to expand an item  */
        viewHolder.itemView.setOnClickListener {
            mExpandedPosition = if (isExpanded) -1 else viewHolder.adapterPosition
            if (mExpandedPosition >= 0) {
                notifyItemChanged(mExpandedPosition)
            }
            notifyItemChanged(viewHolder.adapterPosition)
        }

        viewHolder.itemView.setOnLongClickListener {
            optionsClickListener.onOptionsClick(householdTask)
            true
        }

        if (isExpanded) {
//            viewHolder.taskIcon.setImageResource(R.drawable.ic_minus_white_24dp)
            viewHolder.taskDescription.visibility = View.VISIBLE
        } else {
//            viewHolder.taskIcon.setImageResource(R.drawable.ic_plus_white_24dp)
            viewHolder.taskDescription.visibility = View.GONE
        }
    }

    override fun onBindHeaderViewHolder(holder: HeaderViewHolder, position: Int) {
        val headerFirstLetter = houseHoldTasks[position].name[0].toString()
        holder.firstLetter.text = headerFirstLetter.toUpperCase()
    }

    override fun getItemCount(): Int {
        return houseHoldTasks.size
    }

    override fun getHeaderId(position: Int): Long {
        return houseHoldTasks[position].name[0].toLong()
    }

    fun removeItem(householdTask: EventCategory) {
        val indexToRemove = houseHoldTasks.indexOf(householdTask)
        houseHoldTasks.removeAt(indexToRemove)
        houseHoldTasks.sortBy { it.name }
        notifyItemRemoved(indexToRemove)
    }

    fun insertItem(task: EventCategory) {
        houseHoldTasks.add(task)
        houseHoldTasks.sortBy { it.name }
        notifyDataSetChanged()
    }

    fun editItem(householdTask: EventCategory) {
        val indexToUpdate = houseHoldTasks.indexOfFirst { it -> it.categoryId == householdTask.categoryId }
        houseHoldTasks[indexToUpdate] = householdTask
        houseHoldTasks.sortBy { it.name }
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val taskTitle: TextView
        val taskDescription: TextView
        val taskPoints: TextView
        //val taskIcon: ImageView

        init {
            this.taskTitle = view.findViewById(R.id.task_title)
            this.taskDescription = view.findViewById(R.id.task_description)
            this.taskPoints = view.findViewById(R.id.task_points_hint)
            // this.taskIcon = view.findViewById(R.id.task_expand_icon)
        }
    }

    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val firstLetter: TextView

        init {
            this.firstLetter = view.findViewById(R.id.list_first_letter)
        }
    }

    interface OnOptionsClickListener {
        fun onOptionsClick(
            householdTask: EventCategory
        )
    }
}