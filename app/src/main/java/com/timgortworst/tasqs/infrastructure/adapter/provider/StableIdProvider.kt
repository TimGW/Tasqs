package com.timgortworst.tasqs.infrastructure.adapter.provider

import com.timgortworst.tasqs.infrastructure.adapter.viewholder.ViewHolderBinder

interface StableIdProvider {
    fun getItemId(item: Any?, viewHolderBinder: ViewHolderBinder<*, *>?): Long?
}