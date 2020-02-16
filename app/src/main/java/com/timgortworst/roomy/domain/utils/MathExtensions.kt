package com.timgortworst.roomy.domain.utils

import android.text.Editable

fun Int.betweenUntil(x: Int, y: Int): Boolean = (this in x until y)

fun Editable?.toIntOrOne(): Int = toString().toIntOrNull() ?: 1
