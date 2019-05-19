package com.timgortworst.roomy.utils

import android.content.Context
import android.widget.Toast

fun Context.showToast(stringResource: Int, length: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, getString(stringResource), length).show()
}

fun Context.showToast(string: String, length: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, string, length).show()
}