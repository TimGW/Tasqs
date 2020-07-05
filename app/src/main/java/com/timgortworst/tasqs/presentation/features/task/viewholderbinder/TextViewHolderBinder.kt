package com.timgortworst.tasqs.presentation.features.task.viewholderbinder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.timgortworst.tasqs.R
import com.timgortworst.tasqs.infrastructure.adapter.viewholder.ViewHolderBinder
import kotlinx.android.synthetic.main.layout_input_text.view.*

class TextViewHolderBinder : ViewHolderBinder<TextViewHolderBinder.ViewItem, TextViewHolderBinder.ViewHolder> {

    override fun createViewHolder(parent: ViewGroup): ViewHolder =
            ViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_input_text, parent, false))

    override fun bind(viewHolder: ViewHolder, item: ViewItem) {
        viewHolder.hint?.hint = viewHolder.hint?.context?.getString(item.hint)
        viewHolder.textView.setText(item.text)

        viewHolder.textView.setOnClickListener {
            item.callback?.onClick()
        }
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val textView: TextInputEditText = itemView.text_view
        val hint: TextInputLayout? = itemView.text_view_hint
    }

    data class ViewItem(
        val text: String,
        @StringRes val hint: Int,
        val callback: Callback? = null
    )

    interface Callback {
        fun onClick()
    }
}
