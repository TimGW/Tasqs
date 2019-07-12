package com.timgortworst.roomy.utils

fun Long.isTimeStampInPast(): Boolean {
    return /*DateUtils.isToday(this + DateUtils.DAY_IN_MILLIS) && */ this < System.currentTimeMillis()
}