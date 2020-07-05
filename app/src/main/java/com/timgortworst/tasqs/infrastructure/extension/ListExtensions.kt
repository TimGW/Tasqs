package com.timgortworst.tasqs.infrastructure.extension

/**
 * Returns an element at the given [index] or `first` if the [index] is out of bounds of this list.
 */
fun <T> List<T>.getOrFirst(index: Int): T {
    return if (index >= 0 && index <= lastIndex) get(index) else first()
}

/**
 * Returns first index of [element], or a default if the list does not contain element.
 */
fun <T> List<T>.indexOr(element: T, default: Int): Int {
    return if (indexOf(element) != -1) indexOf(element) else default
}
