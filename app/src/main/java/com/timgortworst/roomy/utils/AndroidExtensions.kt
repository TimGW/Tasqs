package com.timgortworst.roomy.utils

import android.content.Context
import android.os.IBinder
import android.support.design.widget.TextInputEditText
import android.util.TypedValue
import android.view.inputmethod.InputMethodManager
import android.widget.Toast

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
