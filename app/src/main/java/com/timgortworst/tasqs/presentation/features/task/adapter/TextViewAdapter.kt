package com.timgortworst.tasqs.presentation.features.task.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.timgortworst.tasqs.R
import kotlinx.android.synthetic.main.layout_input_text.view.*

class TextViewAdapter(
    private var viewItem: ViewItem
) : RecyclerView.Adapter<TextViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_input_text, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.hint?.hint = holder.hint?.context?.getString(viewItem.hint)
        holder.textView.setText(viewItem.text)

        holder.textView.setOnClickListener {
            viewItem.callback?.onClick()
        }
    }

    override fun getItemViewType(position: Int): Int = R.layout.layout_input_text

    override fun getItemCount() = 1

    fun setViewItem(viewItem: ViewItem) {
        this.viewItem = viewItem
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val textView: TextInputEditText = itemView.text_view
        val hint: TextInputLayout? = itemView.text_view_hint
    }

    data class ViewItem(
        val text: String,
        @StringRes val hint: Int
    ){
        var callback: Callback? = null
    }

    interface Callback {
        fun onClick()
    }
}
