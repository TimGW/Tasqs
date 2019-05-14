package com.timgortworst.roomy.utils

import android.content.Context
import android.net.ConnectivityManager
import android.os.IBinder
import android.support.design.widget.TextInputEditText
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateUtils
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.accessibility.AccessibilityManager
import android.view.inputmethod.InputMethodManager
import com.google.android.gms.tasks.Task
import com.timgortworst.roomy.RoomyApp
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


object AndroidUtil {
    private var dpiMultiplier: Float = 0.toFloat()
    private var displayMetrics: DisplayMetrics? = null
    private var manager: AccessibilityManager? = null
    private var connectivityManager: ConnectivityManager? = null

    fun init(context: Context) {
        dpiMultiplier = context.resources.displayMetrics.density
        displayMetrics = context.resources.displayMetrics
        manager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    fun dpToPx(dp: Float): Int {
        return (dp * dpiMultiplier + 0.5f).toInt()
    }

    fun pxToDp(px: Float): Int {
        return (px / dpiMultiplier + 0.5f).toInt()
    }

    fun spToDp(size: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            size.toFloat(), RoomyApp.applicationContext().resources.displayMetrics
        ).toInt()
    }

    fun spToPx(sp: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, displayMetrics).toInt()
    }

    fun openKeyboard(context: Context, editText: TextInputEditText) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    fun closeKeyboard(context: Context, windowToken: IBinder) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    fun isInPast(timestamp: Long): Boolean {
        return DateUtils.isToday(timestamp + DateUtils.DAY_IN_MILLIS) && timestamp < System.currentTimeMillis()
    }
}

suspend fun <T> Task<T>.await(): T = suspendCoroutine { continuation ->
    addOnCompleteListener { task ->
        val result = task.result
        result?.let {
            if (task.isSuccessful) {
                continuation.resume(it)
            } else {
                continuation.resumeWithException(task.exception ?: RuntimeException("Unknown task exception"))
            }
        }
    }
}

fun TextInputEditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
    })
}
