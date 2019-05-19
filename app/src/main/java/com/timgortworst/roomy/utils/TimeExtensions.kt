package com.timgortworst.roomy.utils

import android.text.format.DateUtils

fun Long.isTimeStampInPast(): Boolean {
    return DateUtils.isToday(this + DateUtils.DAY_IN_MILLIS) && this < System.currentTimeMillis()
}