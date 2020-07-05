package com.timgortworst.tasqs.presentation.features.task.viewholderbinder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.TextView
import androidx.core.view.get
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textfield.TextInputEditText
import com.timgortworst.tasqs.R
import com.timgortworst.tasqs.domain.model.TaskRecurrence
import com.timgortworst.tasqs.infrastructure.adapter.viewholder.ViewHolderBinder
import com.timgortworst.tasqs.infrastructure.extension.getOrFirst
import com.timgortworst.tasqs.presentation.features.task.adapter.BaseArrayAdapter
import kotlinx.android.synthetic.main.layout_input_recurrence.view.*
import kotlinx.android.synthetic.main.layout_recurrence_picker.view.*
import kotlinx.android.synthetic.main.layout_week_picker.view.*
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.temporal.ChronoField

class RecurrenceViewHolderBinder :
    ViewHolderBinder<RecurrenceViewHolderBinder.ViewItem, RecurrenceViewHolderBinder.ViewHolder> {
    var callback: Callback? = null

    private var adapter: BaseArrayAdapter<TaskRecurrence>? = null
    private val recurrences = mutableListOf(
        TaskRecurrence.Daily(),
        TaskRecurrence.Weekly(),
        TaskRecurrence.Monthly(),
        TaskRecurrence.Annually()
    )

    override fun createViewHolder(parent: ViewGroup): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_input_recurrence, parent, false)
        )

    override fun bind(viewHolder: ViewHolder, item: ViewItem) {
        setupCheckBox(viewHolder, item)
        setupFrequency(viewHolder, item)
        setupSpinner(viewHolder, item)
    }

    private fun setupCheckBox(
        viewHolder: ViewHolder,
        item: ViewItem
    ) {
        val isRepeating = item.taskRecurrence !is TaskRecurrence.SingleTask

        viewHolder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            viewHolder.taskRepeatView.visibility = if (isChecked) {
                callback?.onRecurrenceSelection(recurrenceFromSelection(viewHolder))
                View.VISIBLE
            } else {
                callback?.onRecurrenceSelection(TaskRecurrence.SingleTask())
                View.GONE
            }
        }

        viewHolder.checkBox.isChecked = isRepeating
    }

    private fun setupFrequency(viewHolder: ViewHolder, item: ViewItem) {
        viewHolder.frequency.setOnFocusChangeListener { _, hasFocus ->
            viewHolder.frequency.apply { if (text?.isBlank() == true && !hasFocus) setText("1") }
        }

        viewHolder.frequency.doAfterTextChanged {
            if (it == null) return@doAfterTextChanged

            val input = it.toString() // don't allow 0 as input
            if (input.isNotEmpty() && input.first() == '0') {
                it.replace(0, 1, "1")
            }

            callback?.onRecurrenceSelection(recurrenceFromSelection(viewHolder))
            adapter?.notifyDataSetChanged()
        }

        viewHolder.frequency.setText(item.taskRecurrence.frequency.toString())
    }

    private fun setupSpinner(parentViewHolder: ViewHolder, item: ViewItem) {
        adapter = object : BaseArrayAdapter<TaskRecurrence>(recurrences) {

            override fun getAdapterView(
                position: Int,
                convertView: View?,
                parent: ViewGroup?,
                t: TaskRecurrence
            ): View? {
                return inflateSpinnerAdapter(convertView, parent, t, parentViewHolder)
            }
        }

        parentViewHolder.spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    parentViewHolder.weekPickerView.visibility =
                        if (recurrences[position] is TaskRecurrence.Weekly) View.VISIBLE else View.GONE

                    callback?.onRecurrenceSelection(recurrenceFromSelection(parentViewHolder))
                }
            }

        parentViewHolder.spinner.adapter = adapter

        val weekdayButtonGroup = parentViewHolder.toggleGroup
        val recurrenceIndex = recurrences.indexOfFirst { it.name == item.taskRecurrence.name }
        parentViewHolder.spinner.setSelection(recurrenceIndex)
        (item.taskRecurrence as? TaskRecurrence.Weekly)?.let { weekly ->
            parentViewHolder.weekPickerView.visibility = View.VISIBLE
            weekly.onDaysOfWeek.forEach { index ->
                weekdayButtonGroup.check(weekdayButtonGroup[index - 1].id)
            }
        }

        weekdayButtonGroup.addOnButtonCheckedListener { _, _, _ ->
            // clearAllFocus()
        }
    }

    private fun recurrenceFromSelection(viewHolder: ViewHolder): TaskRecurrence {
        if (!viewHolder.checkBox.isChecked) return TaskRecurrence.SingleTask(1)

        val freq = viewHolder.frequency.text.toString().toIntOrNull() ?: 1

        return when (recurrences.getOrFirst(viewHolder.spinner.selectedItemPosition)) {
            is TaskRecurrence.Daily -> TaskRecurrence.Daily(freq)
            is TaskRecurrence.Weekly -> {
                val weekdays = getSelectedWeekdays(viewHolder).ifEmpty {
                    listOf(ZonedDateTime.now().get(ChronoField.DAY_OF_WEEK))
                }
                TaskRecurrence.Weekly(freq, weekdays)
            }
            is TaskRecurrence.Monthly -> TaskRecurrence.Monthly(freq)
            is TaskRecurrence.Annually -> TaskRecurrence.Annually(freq)
            is TaskRecurrence.SingleTask -> TaskRecurrence.SingleTask(freq)
        }
    }

    private fun getSelectedWeekdays(viewHolder: ViewHolder): List<Int> {
        val buttonGroup = viewHolder.toggleGroup
        return buttonGroup
            .checkedButtonIds
            .map { buttonId ->
                val btn = buttonGroup.findViewById<MaterialButton>(buttonId)
                buttonGroup.indexOfChild(btn) + 1
            } // map checked buttons to weekday index 0..6 (mo - su)
    }


    private fun inflateSpinnerAdapter(
        convertView: View?,
        parent: ViewGroup?,
        taskRecurrence: TaskRecurrence,
        parentViewHolder: ViewHolder
    ): View? {
        val inflater = LayoutInflater.from(parent?.context)
        val view: View?
        val vh: SpinnerViewHolder
        if (convertView == null) {
            view = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false)
            vh = SpinnerViewHolder(view)
            view?.tag = vh
        } else {
            view = convertView
            vh = view.tag as SpinnerViewHolder
        }

        val frequency = parentViewHolder.frequency.text.toString()
        vh.label.text = if (frequency.toIntOrNull()?.equals(1) == true || frequency.isBlank()) {
            parent?.context?.getString(taskRecurrence.name)
        } else {
            parent?.context?.getString(taskRecurrence.pluralName)
        }

        return view
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.task_repeat_checkbox
        val taskRepeatView: View = itemView.task_repeat_view
        val frequency: TextInputEditText = taskRepeatView.recurrence_frequency
        val spinner: Spinner = taskRepeatView.spinner_recurrence
        val weekPickerView: View = taskRepeatView.recurrence_week_picker
        val toggleGroup: MaterialButtonToggleGroup = weekPickerView.weekday_button_group
    }

    class SpinnerViewHolder(row: View?) {
        val label: TextView = row?.findViewById(android.R.id.text1) as TextView
    }

    data class ViewItem(val taskRecurrence: TaskRecurrence)

    interface Callback {
        fun onRecurrenceSelection(selection: TaskRecurrence)
    }
}
