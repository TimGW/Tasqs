package com.timgortworst.roomy.ui.category.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter
import com.timgortworst.roomy.R
import com.timgortworst.roomy.model.Category

/**
 * Recyclerview adapter for handling the list items in the task overview
 *
 * Handles clicks by expanding items to show a more detailed description of the HouseholdTask
 */
class CategoryListAdapter(
        private var optionsClickListener: OnOptionsClickListener
) : StickyRecyclerHeadersAdapter<CategoryListAdapter.HeaderViewHolder>,
        RecyclerView.Adapter<CategoryListAdapter.ViewHolder>() {
    private val houseHoldTasks: MutableList<Category> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_category_list, parent, false)
        return ViewHolder(view)
    }

    override fun onCreateHeaderViewHolder(parent: ViewGroup): HeaderViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_category_list_header, parent, false)
        return HeaderViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val householdTask = houseHoldTasks[position]

        viewHolder.taskTitle.text = householdTask.name

        if (householdTask.description.isNotBlank()) {
            viewHolder.taskDescription.text = householdTask.description
            viewHolder.taskDescription.visibility = View.VISIBLE
        } else {
            viewHolder.taskDescription.visibility = View.GONE
        }

        viewHolder.itemView.setOnLongClickListener {
            optionsClickListener.onOptionsClick(householdTask)
            true
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

    fun removeItem(householdTask: Category) {
        val indexToRemove = houseHoldTasks.indexOf(householdTask)
        houseHoldTasks.removeAt(indexToRemove)
        houseHoldTasks.sortBy { it.name }
        notifyItemRemoved(indexToRemove)
    }

    fun insertItem(task: Category) {
        houseHoldTasks.add(task)
        houseHoldTasks.sortBy { it.name }
        notifyDataSetChanged()
    }

    fun editItem(householdTask: Category) {
        val indexToUpdate = houseHoldTasks.indexOfFirst { it.categoryId == householdTask.categoryId }
        houseHoldTasks[indexToUpdate] = householdTask
        houseHoldTasks.sortBy { it.name }
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val taskTitle: TextView
        val taskDescription: TextView

        init {
            this.taskTitle = view.findViewById(R.id.task_title)
            this.taskDescription = view.findViewById(R.id.task_description)
        }
    }

    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val firstLetter: TextView

        init {
            this.firstLetter = view.findViewById(R.id.list_first_letter)
        }
    }

    interface OnOptionsClickListener {
        fun onOptionsClick(householdTask: Category)
    }
}