package com.timgortworst.roomy.presentation.features.user

import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.timgortworst.roomy.domain.model.UIResponseState
import com.timgortworst.roomy.domain.model.User

@BindingAdapter("dataVisibility")
fun ViewGroup.dataVisibility(responseState: UIResponseState?) {
    visibility = if (responseState is UIResponseState.Success<*>) View.VISIBLE else View.GONE
}

@BindingAdapter("loadingVisibility")
fun ProgressBar.loadingVisibility(responseState: UIResponseState?) {
    visibility = if (responseState is UIResponseState.Loading) View.VISIBLE else View.GONE
}

@BindingAdapter("adminLabelVisibility")
fun TextView.adminLabelVisibility(isAdmin: Boolean) {
    visibility = if (isAdmin) View.VISIBLE else View.GONE
}

@BindingAdapter("messageVisibility")
fun TextView.messageVisibility(responseState: UIResponseState?) {
    visibility = if (responseState is UIResponseState.Error) View.VISIBLE else View.GONE
    (responseState as? UIResponseState.Error)?.message?.let { text = context.getString(it) }
}

@BindingAdapter("users")
fun RecyclerView.setUsers(responseState: UIResponseState?) {
    if (isInstanceOf<UIResponseState.Success<List<User>>>(responseState)) {
        val response = responseState as UIResponseState.Success<List<User>>
        (adapter as UserAdapter).addAll(response.data)
    }
}

inline fun <reified T> isInstanceOf(instance: Any?): Boolean {
    return instance is T
}