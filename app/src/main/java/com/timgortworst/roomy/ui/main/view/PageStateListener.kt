package com.timgortworst.roomy.ui.main.view

interface PageStateListener {
    fun setErrorView(isVisible: Boolean)
    fun setLoadingView(isLoading: Boolean)
    fun reloadPage()
}