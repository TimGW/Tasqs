package com.timgortworst.roomy.presentation.features.settings

import android.os.Build
import android.text.Html
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("app:htmlText")
fun TextView.htmlText(htmlText: String) {
    movementMethod = LinkMovementMethod.getInstance()
    text = textToHtml(htmlText)
}

@Suppress("DEPRECATION")
fun textToHtml(text: String): Spanned {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(text)
    }
}