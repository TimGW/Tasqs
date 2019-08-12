package com.timgortworst.roomy.presentation.features.category.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter
import com.timgortworst.roomy.R
import com.timgortworst.roomy.data.model.Category

/**
 * Recyclerview adapter for handling the list items in the task overview
 *
 * Handles clicks by expanding items to show a more detailed description of the HouseholdTask
 */
class CategoryListAdapter(
        private var optionsClickListener: OnOptionsClickListener
) : StickyRecyclerHeadersAdapter<CategoryListAdapter.HeaderViewHolder>,
        RecyclerView.Adapter<CategoryListAdapter.ViewHolder>() {
    private val categories: MutableList<Category> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_category_list, parent, false)
        return ViewHolder(view)
    }

    override fun onCreateHeaderViewHolder(parent: ViewGroup): HeaderViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_category_list_header, parent, false)
        return HeaderViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val category = categories[position]

        viewHolder.title.text = category.name

        if (category.description.isNotBlank()) {
            viewHolder.description.text = category.description
            viewHolder.description.visibility = View.VISIBLE
        } else {
            viewHolder.description.visibility = View.GONE
        }

        viewHolder.itemView.setOnLongClickListener {
            optionsClickListener.onOptionsClick(category)
            true
        }
    }

    override fun onBindHeaderViewHolder(holder: HeaderViewHolder, position: Int) {
        val headerFirstLetter = categories[position].name[0].toString()
        holder.firstLetter.text = headerFirstLetter.toUpperCase()
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun getHeaderId(position: Int): Long {
        return categories[position].name[0].toLong()
    }

    fun removeItem(category: Category) {
        val indexToRemove = categories.indexOf(category)
        categories.removeAt(indexToRemove)
        categories.sortBy { it.name }
        notifyItemRemoved(indexToRemove)
    }

    fun insertItem(task: Category) {
        categories.add(task)
        categories.sortBy { it.name }
        notifyDataSetChanged()
    }

    fun editItem(category: Category) {
        val indexToUpdate = categories.indexOfFirst { it.categoryId == category.categoryId }
        categories[indexToUpdate] = category
        categories.sortBy { it.name }
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView
        val description: TextView

        init {
            this.title = view.findViewById(R.id.title)
            this.description = view.findViewById(R.id.description)
        }
    }

    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val firstLetter: TextView

        init {
            this.firstLetter = view.findViewById(R.id.list_first_letter)
        }
    }

    interface OnOptionsClickListener {
        fun onOptionsClick(category: Category)
    }
}