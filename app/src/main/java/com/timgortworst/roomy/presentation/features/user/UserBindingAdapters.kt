package com.timgortworst.roomy.presentation.features.user

import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.timgortworst.roomy.domain.model.ResponseState
import com.timgortworst.roomy.domain.model.User

@BindingAdapter("dataVisibility")
fun ViewGroup.dataVisibility(responseState: ResponseState?) {
    visibility = if (responseState is ResponseState.Success<*>) View.VISIBLE else View.GONE
}

@BindingAdapter("loadingVisibility")
fun ProgressBar.loadingVisibility(responseState: ResponseState?) {
    visibility = if (responseState is ResponseState.Loading) View.VISIBLE else View.GONE
}

@BindingAdapter("adminLabelVisibility")
fun TextView.adminLabelVisibility(isAdmin: Boolean) {
    visibility = if (isAdmin) View.VISIBLE else View.GONE
}

@BindingAdapter("messageVisibility")
fun TextView.messageVisibility(responseState: ResponseState?) {
    visibility = if (responseState is ResponseState.Error) View.VISIBLE else View.GONE
    (responseState as? ResponseState.Error)?.message?.let { text = context.getString(it) }
}

@BindingAdapter("users")
fun RecyclerView.setUsers(responseState: ResponseState?) {
    if (isInstanceOf<ResponseState.Success<List<User>>>(responseState)) {
        val response = responseState as ResponseState.Success<List<User>>
        (adapter as UserAdapter).addAll(response.data)
    }
}

inline fun <reified T> isInstanceOf(instance: Any?): Boolean {
    return instance is T
}