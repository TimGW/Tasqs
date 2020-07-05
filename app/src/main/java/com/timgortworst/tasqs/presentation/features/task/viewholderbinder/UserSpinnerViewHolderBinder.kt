package com.timgortworst.tasqs.presentation.features.task.viewholderbinder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.timgortworst.tasqs.R
import com.timgortworst.tasqs.domain.model.Task
import com.timgortworst.tasqs.domain.model.response.Response
import com.timgortworst.tasqs.infrastructure.adapter.viewholder.ViewHolderBinder
import com.timgortworst.tasqs.infrastructure.extension.indexOr
import com.timgortworst.tasqs.presentation.features.task.adapter.BaseArrayAdapter
import com.timgortworst.tasqs.presentation.features.task.viewmodel.TaskEditViewModel
import kotlinx.android.synthetic.main.layout_input_user.view.*
import org.koin.core.KoinComponent
import org.koin.core.inject

class UserSpinnerViewHolderBinder(private val lifecycleOwner: LifecycleOwner) :
    ViewHolderBinder<UserSpinnerViewHolderBinder.ViewItem, UserSpinnerViewHolderBinder.ViewHolder>,
    KoinComponent {
    private val viewModel: TaskEditViewModel by inject()
    var callback: Callback? = null
    private val spinnerAdapter = object : BaseArrayAdapter<Task.User>(mutableListOf()) {
        override fun getAdapterView(
            position: Int,
            convertView: View?,
            parent: ViewGroup?,
            t: Task.User
        ): View? {
            return inflateSpinnerAdapter(convertView, parent, t)
        }
    }

    override fun createViewHolder(parent: ViewGroup): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_input_user, parent, false)
        )

    override fun bind(viewHolder: ViewHolder, item: ViewItem) {
        viewHolder.spinnerTitle.text = item.title

        viewHolder.spinner.adapter = spinnerAdapter
        viewHolder.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                callback?.onSpinnerSelection(Response.Success(spinnerAdapter.getItem(position)))
            }
        }

        if(viewHolder.spinner.adapter.isEmpty){
            observeUsers(viewHolder, item)
        }
    }

    private fun observeUsers(viewHolder: ViewHolder, item: ViewItem){
        viewModel.taskUsersLiveData.observe(lifecycleOwner, Observer { response ->
            when (response) {
                Response.Loading -> callback?.onSpinnerSelection(Response.Loading)
                is Response.Error -> callback?.onSpinnerSelection(Response.Error())
                is Response.Success -> {
                    val userList = response.data ?: return@Observer

                    spinnerAdapter.clear()
                    spinnerAdapter.addAll(userList.toMutableList())
                    spinnerAdapter.notifyDataSetChanged()

                    if (userList.size == 1) {
                        callback?.onSpinnerSelection(Response.Success(userList.first()))
                    } else {
                        val index = userList.indexOr(item.currentUser, 0)
                        viewHolder.spinner.setSelection(index)
                        callback?.onSpinnerSelection(Response.Success(userList[index]))
                    }
                }
            }
        })
    }

    private fun inflateSpinnerAdapter(
        convertView: View?,
        parent: ViewGroup?,
        user: Task.User
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

        vh.label.text = user.name

        return view
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val spinnerTitle: TextView = itemView.spinner_title
        val spinner: Spinner = itemView.spinner
    }

    class SpinnerViewHolder(row: View?) {
        val label: TextView = row?.findViewById(android.R.id.text1) as TextView
    }

    data class ViewItem(val title: String, val currentUser: Task.User?)

    interface Callback {
        fun onSpinnerSelection(response: Response<Task.User>)
    }
}





