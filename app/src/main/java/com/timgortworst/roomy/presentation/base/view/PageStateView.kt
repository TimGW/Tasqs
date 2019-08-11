package com.timgortworst.roomy.presentation.base.view

interface PageStateView {
    fun setErrorView(isVisible: Boolean, title: Int? = null, text: Int? = null)
    fun setLoadingView(isLoading: Boolean)
}