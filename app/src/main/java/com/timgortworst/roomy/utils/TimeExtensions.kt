package com.timgortworst.roomy.utils

fun Long.isTimeStampInPast() = this < System.currentTimeMillis()