package com.timgortworst.roomy.presentation.features.user

import android.view.View
import androidx.databinding.BindingAdapter
import com.google.android.material.textview.MaterialTextView
import com.timgortworst.roomy.domain.model.Role

@BindingAdapter("showIfAdmin")
fun MaterialTextView.showIfAdmin(role: String) {
    visibility = if (role == Role.ADMIN.name)
        View.VISIBLE
    else
        View.GONE
}
