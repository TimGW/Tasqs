package com.timgortworst.roomy.utils

import android.content.Context
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.text.Html
import android.text.Spanned
import android.util.Log
import android.util.Log.e
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText

fun Context.showToast(stringResource: Int, length: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, getString(stringResource), length).show()
}

fun Context.showToast(string: String, length: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, string, length).show()
}

fun Context.openKeyboard(editText: TextInputEditText) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    imm?.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
}

fun Context.closeKeyboard(windowToken: IBinder) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}


fun Context.dpToPx(dp: Float): Int {
    return (dp * resources.displayMetrics.density + 0.5f).toInt()
}

fun Context.pxToDp(px: Float): Int {
    return (px / resources.displayMetrics.density + 0.5f).toInt()
}

fun Context.spToDp(size: Int): Int {
    return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            size.toFloat(), resources.displayMetrics
    ).toInt()
}

fun Context.spToPx(sp: Float): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, resources.displayMetrics).toInt()
}

fun Log.msg(message: String) = e("TIMTIM", message)

fun View.runBeforeDraw(onFinished: (view: View) -> Unit) {
    val preDrawListener = object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            Runnable { onFinished.invoke(this@runBeforeDraw) }.run()
            viewTreeObserver.removeOnPreDrawListener(this)
            return true
        }
    }
    viewTreeObserver.addOnPreDrawListener(preDrawListener)
}

fun Context.isAirplaneModeEnabled() =
        Settings.Global.getInt(contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0) != 0

@Suppress("DEPRECATION")
fun String.fromHtml(): Spanned {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(this)
    }
}
