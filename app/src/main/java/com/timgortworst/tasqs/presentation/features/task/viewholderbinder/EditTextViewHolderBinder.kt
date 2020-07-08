package com.timgortworst.tasqs.presentation.features.task.viewholderbinder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.timgortworst.tasqs.R
import com.timgortworst.tasqs.infrastructure.adapter.ViewHolderBinder
import kotlinx.android.synthetic.main.layout_input_edittext.view.*

class EditTextViewHolderBinder :
    ViewHolderBinder<EditTextViewHolderBinder.ViewItem, EditTextViewHolderBinder.ViewHolder> {

    override fun createViewHolder(parent: ViewGroup): ViewHolder =
            ViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_input_edittext, parent, false))

    override fun bind(viewHolder: ViewHolder, item: ViewItem) {
        viewHolder.editText.setText(item.text)
        viewHolder.hint?.error = item.errorMessage

        // set listener
        viewHolder.editText.doAfterTextChanged {
            if (it?.isNotBlank() == true) viewHolder.hint?.error = null

            item.callback?.onDescriptionInput(viewHolder.editText.text.toString())
        }
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val editText: TextInputEditText = itemView.task_description
        val hint: TextInputLayout? = itemView.task_description_hint
    }

    data class ViewItem(
        val text: String,
        val errorMessage: String?,
        val callback: Callback? = null
    )

    interface Callback {
        fun onDescriptionInput(text: String)
    }
}



