package com.timgortworst.tasqs.presentation.features.task.view

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.NumberPicker
import android.widget.NumberPicker.OnValueChangeListener
import androidx.fragment.app.DialogFragment
import com.timgortworst.tasqs.R

class NumberPickerDialog(
    private val initialValue: Int = MIN_RECURRENCE_FREQUENCY
) : DialogFragment() {
    var valueChangeListener: OnValueChangeListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val numberPicker = NumberPicker(activity)
        numberPicker.minValue = MIN_RECURRENCE_FREQUENCY
        numberPicker.maxValue = MAX_RECURRENCE_FREQUENCY
        numberPicker.value = initialValue

        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setTitle(getString(R.string.recurrence))
        builder.setMessage(getString(R.string.pick_frequency))
        builder.setPositiveButton(getString(android.R.string.ok)) { _, _ ->
            valueChangeListener?.onValueChange(
                numberPicker,
                numberPicker.value,
                numberPicker.value
            )
        }
        builder.setNegativeButton(getString(android.R.string.cancel)) { _, _ ->
            dismiss()
        }
        builder.setView(numberPicker)
        return builder.create()
    }

    companion object {
        private const val MIN_RECURRENCE_FREQUENCY = 1
        private const val MAX_RECURRENCE_FREQUENCY = 99
    }
}
