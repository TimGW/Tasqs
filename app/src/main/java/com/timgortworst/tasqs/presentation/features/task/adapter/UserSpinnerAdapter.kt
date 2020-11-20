package com.timgortworst.tasqs.presentation.features.task.adapter

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
import com.timgortworst.tasqs.infrastructure.extension.indexOr
import com.timgortworst.tasqs.infrastructure.adapter.GenericArrayAdapter
import com.timgortworst.tasqs.presentation.features.task.viewmodel.TaskEditViewModel
import kotlinx.android.synthetic.main.layout_input_user.view.*
import org.koin.core.KoinComponent
import org.koin.core.inject

class UserSpinnerAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val viewItem: ViewItem
) : RecyclerView.Adapter<UserSpinnerAdapter.ViewHolder>(), KoinComponent {
    private val viewModel: TaskEditViewModel by inject()
    private val spinnerAdapter = object : GenericArrayAdapter<Task.User>(mutableListOf()) {
        override fun getAdapterView(
            position: Int,
            convertView: View?,
            parent: ViewGroup?,
            t: Task.User
        ): View? {
            return inflateSpinnerAdapter(convertView, parent, t)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_input_user, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.spinnerTitle.text = viewItem.title
        holder.spinner.adapter = spinnerAdapter
        holder.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewItem.callback?.onSpinnerSelection(Response.Success(spinnerAdapter.getItem(position)))
            }
        }

        if(holder.spinner.adapter.isEmpty){
            observeUsers(holder, viewItem)
        }
    }

    override fun getItemViewType(position: Int): Int = R.layout.layout_input_user

    override fun getItemCount() = 1

    private fun observeUsers(viewHolder: ViewHolder, item: ViewItem){
        viewModel.taskUsersLiveData.observe(lifecycleOwner, Observer { response ->
            when (response) {
                Response.Loading -> item.callback?.onSpinnerSelection(Response.Loading)
                is Response.Error -> item.callback?.onSpinnerSelection(Response.Error())
                is Response.Success -> {
                    val userList = response.data ?: return@Observer

                    spinnerAdapter.clear()
                    spinnerAdapter.addAll(userList.toMutableList())
                    spinnerAdapter.notifyDataSetChanged()

                    if (userList.size == 1) {
                        item.callback?.onSpinnerSelection(Response.Success(userList.first()))
                    } else {
                        val index = userList.indexOr(item.currentUser, 0)
                        viewHolder.spinner.setSelection(index)
                        item.callback?.onSpinnerSelection(Response.Success(userList[index]))
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

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val spinnerTitle: TextView = itemView.spinner_title
        val spinner: Spinner = itemView.spinner
    }

    inner class SpinnerViewHolder(row: View?) {
        val label: TextView = row?.findViewById(android.R.id.text1) as TextView
    }

    data class ViewItem(val title: String, val currentUser: Task.User?) {
        var callback: Callback? = null
    }

    interface Callback {
        fun onSpinnerSelection(response: Response<Task.User>)
    }
}





