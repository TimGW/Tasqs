package com.timgortworst.roomy.repository

import com.google.firebase.firestore.DocumentChange

sealed class DataListener {
    object Loading : DataListener()
    data class Success(val dc: List<DocumentChange>, val totalDataSetSize: Int) : DataListener()
    data class Error(val throwable: Throwable) : DataListener()
}
