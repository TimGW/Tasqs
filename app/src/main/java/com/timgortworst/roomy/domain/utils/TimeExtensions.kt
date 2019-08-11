package com.timgortworst.roomy.domain.utils

fun Long.isTimeStampInPast() = this < System.currentTimeMillis()