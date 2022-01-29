package com.timgortworst.tasqs.presentation.features.task.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.get
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.timgortworst.tasqs.R
import com.timgortworst.tasqs.domain.model.TaskRecurrence
import com.timgortworst.tasqs.infrastructure.adapter.GenericArrayAdapter
import com.timgortworst.tasqs.infrastructure.extension.getOrFirst
import com.timgortworst.tasqs.presentation.features.task.view.NumberPickerDialog
import kotlinx.android.synthetic.main.layout_input_recurrence.view.*
import kotlinx.android.synthetic.main.layout_recurrence_picker.view.*
import kotlinx.android.synthetic.main.layout_week_picker.view.*
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.temporal.ChronoField

class RecurrenceAdapter(
    private val supportFragmentManager: FragmentManager,
    private val viewItem: ViewItem
) : RecyclerView.Adapter<RecurrenceAdapter.ViewHolder>() {
    private var adapter: GenericArrayAdapter<TaskRecurrence>? = null
    private val recurrences = mutableListOf(
        TaskRecurrence.Daily(),
        TaskRecurrence.Weekly(),
        TaskRecurrence.Monthly(),
        TaskRecurrence.Annually()
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_input_recurrence, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        setupCheckBox(holder, viewItem)
        setupFrequency(holder, viewItem)
        setupSpinner(holder, viewItem)
    }

    override fun getItemViewType(position: Int): Int = R.layout.layout_input_recurrence

    override fun getItemCount() = 1

    private fun setupCheckBox(
        viewHolder: ViewHolder,
        item: ViewItem
    ) {
        val isRepeating = item.taskRecurrence !is TaskRecurrence.SingleTask

        viewHolder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            viewHolder.taskRepeatView.visibility = if (isChecked) {
                item.callback?.onRecurrenceSelection(recurrenceFromSelection(viewHolder))
                View.VISIBLE
            } else {
                item.callback?.onRecurrenceSelection(TaskRecurrence.SingleTask())
                View.GONE
            }
        }

        viewHolder.checkBox.isChecked = isRepeating
    }

    private fun setupFrequency(viewHolder: ViewHolder, item: ViewItem) {
        viewHolder.frequency.text = item.taskRecurrence.frequency.toString()

        viewHolder.frequency.setOnClickListener {
            val numberPickerDialog = NumberPickerDialog(
                viewHolder.frequency.text.toString().toInt()
            ).apply {
                valueChangeListener = NumberPicker.OnValueChangeListener { numberPicker, _, _ ->
                    viewHolder.frequency.text = numberPicker.value.toString()
                    item.callback?.onRecurrenceSelection(recurrenceFromSelection(viewHolder))
                    adapter?.notifyDataSetChanged()
                }
            }
            numberPickerDialog.show(supportFragmentManager, null)
        }
    }

    private fun setupSpinner(parentViewHolder: ViewHolder, item: ViewItem) {
        adapter = object : GenericArrayAdapter<TaskRecurrence>(recurrences) {

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

                    item.callback?.onRecurrenceSelection(recurrenceFromSelection(parentViewHolder))
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
            item.callback?.onRecurrenceSelection(recurrenceFromSelection(parentViewHolder))
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

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.task_repeat_checkbox
        val taskRepeatView: View = itemView.task_repeat_view
        val frequency: TextView = taskRepeatView.recurrence_frequency
        val spinner: Spinner = taskRepeatView.spinner_recurrence
        val weekPickerView: View = taskRepeatView.recurrence_week_picker
        val toggleGroup: MaterialButtonToggleGroup = weekPickerView.weekday_button_group
    }

    class SpinnerViewHolder(row: View?) {
        val label: TextView = row?.findViewById(android.R.id.text1) as TextView
    }

    data class ViewItem(val taskRecurrence: TaskRecurrence) {
        var callback: Callback? = null
    }

    interface Callback {
        fun onRecurrenceSelection(selection: TaskRecurrence)
    }
}
