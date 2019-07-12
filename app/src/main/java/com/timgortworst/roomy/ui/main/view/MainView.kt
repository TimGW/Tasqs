package com.timgortworst.roomy.ui.main.view

interface MainView {
    fun presentText(text: String)
    fun presentAgendaFragment()
    fun presentTasksFragment()
    fun presentHousematesFragment()
}