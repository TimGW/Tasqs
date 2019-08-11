package com.timgortworst.roomy.data.repository

import com.google.firebase.firestore.DocumentChange

sealed class DataListener {
    object Loading : DataListener()
    data class Success(val dc: List<DocumentChange>, val totalDataSetSize: Int, val hasPendingWrites: Boolean) : DataListener()
    data class Error(val throwable: Throwable) : DataListener()
}
