package com.timgortworst.roomy.presentation.features.event.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.timgortworst.roomy.data.model.EventRecurrence


class SpinnerRecurrenceAdapter(
        ctx: Context,
        textViewResourceId: Int,
        private val list: List<EventRecurrence>
) : ArrayAdapter<EventRecurrence>(ctx, textViewResourceId, list) {

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): EventRecurrence? {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val label = super.getView(position, convertView, parent) as TextView
        label.text = getLabelForRecurrence(list[position])
        return label
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val label = super.getDropDownView(position, convertView, parent) as TextView
        label.text = getLabelForRecurrence(list[position])
        return label
    }

    private fun getLabelForRecurrence(eventRecurrence: EventRecurrence): String {
        eventRecurrence.apply {
            return when (this) {
                EventRecurrence.SingleEvent -> "days"
                is EventRecurrence.Daily -> "days"
                is EventRecurrence.Weekly -> "weeks"
                is EventRecurrence.Monthly -> "Months"
                is EventRecurrence.Annually -> "years"
            }
        }
    }
}