package com.timgortworst.tasqs.infrastructure.adapter

interface StableIdProvider {
    fun getItemId(item: Any?, viewHolderBinder: ViewHolderBinder<*, *>?): Long?
}