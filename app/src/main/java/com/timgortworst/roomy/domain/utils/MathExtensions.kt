package com.timgortworst.roomy.domain.utils

fun Int.betweenUntil(x: Int, y: Int): Boolean = (this in x until y)

fun Int.between(x: Int, y: Int): Boolean = (this in x..y)

