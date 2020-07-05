package com.timgortworst.tasqs.presentation.features.task.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

abstract class BaseArrayAdapter<T>(private val dataSource: MutableList<T>) : BaseAdapter() {

    abstract fun getAdapterView(position: Int, convertView: View?, parent: ViewGroup?, t: T): View?

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup?
    ): View? {
        return getAdapterView(position, convertView, parent, dataSource[position])
    }

    fun clear() {
        dataSource.clear()
    }

    fun addAll(dataSource: MutableList<T>) {
        this.dataSource.addAll(dataSource)
    }

    override fun getItem(position: Int): T? {
        return dataSource[position]
    }

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItemId(position: Int): Long {
        return dataSource[position].hashCode().toLong()
    }
}