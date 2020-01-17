//package com.timgortworst.roomy.domain.utils
//
//import android.content.Context
//import androidx.work.WorkManager
//import org.junit.Assert.assertEquals
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.mockito.Mock
//import org.mockito.junit.MockitoJUnitRunner
//
//@RunWith(MockitoJUnitRunner::class)
//class NotificationWorkerBuilderTest {
//
//    @Mock
//    private lateinit var mockContext: Context
//    private lateinit var notificationWorkerBuilder: NotificationWorkerBuilder
//
//    @Before
//    fun setUp() {
//        notificationWorkerBuilder = NotificationWorkerBuilder(mockContext, WorkManager.getInstance(mockContext))
//    }
//
//    @Test
//    fun enqueueOneTimeNotification() {
//    }
//
//    @Test
//    fun enqueueNotification() {
//    }
//
//    @Test
//    fun removePendingNotificationReminder() {
//    }
//
//    @Test
//    fun calculateInitialDelay_future() {
//        // futuretime: vrijdag 1 januari 2021 21:00:00 GMT+01:00
//        // currentTime: dinsdag 1 oktober 2019 14:23:13 GMT+02:00 DST
//
//        assertEquals(notificationWorkerBuilder.calculateInitialDelay(1609531200, 1569932593), 39598607)
//    }
//
////    fun calculateInitialDelay(nextOccurrence: Long) = max(0, (nextOccurrence - System.currentTimeMillis()))
//
//}