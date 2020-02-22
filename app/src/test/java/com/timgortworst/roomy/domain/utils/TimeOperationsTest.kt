package com.timgortworst.roomy.domain.utils

import com.timgortworst.roomy.data.model.EventRecurrence
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

class TimeOperationsTest {

    private lateinit var timeOperations: TimeOperations
    private val baseInput: ZonedDateTime = ZonedDateTime.of(LocalDate.of(2020, 1, 1), LocalTime.NOON, ZoneId.of("Europe/Amsterdam"))

    @Before
    fun initTest() {
        timeOperations = TimeOperations()
    }

    @Test
    fun calcNextWeekDay_everySunday() {
        val weekDays = listOf(7)

        val result = timeOperations.calcNextWeekDay(baseInput, EventRecurrence.Weekly(1, weekDays))
        assertEquals(5, result.dayOfMonth)
        assertEquals(1, result.month.value)

        val resultNext = timeOperations.calcNextWeekDay(result, EventRecurrence.Weekly(1, weekDays))
        assertEquals(12, resultNext.dayOfMonth)
        assertEquals(1, resultNext.month.value)
    }

    @Test
    fun calcNextWeekDay_every4WeeksOnSunday() {
        val weekDays = listOf(7)

        val result = timeOperations.calcNextWeekDay(baseInput, EventRecurrence.Weekly(4, weekDays))
        assertEquals(5, result.dayOfMonth)
        assertEquals(1, result.month.value)

        val resultNext = timeOperations.calcNextWeekDay(result, EventRecurrence.Weekly(4, weekDays))
        assertEquals(2, resultNext.dayOfMonth)
        assertEquals(2, resultNext.month.value)

        val resultLast = timeOperations.calcNextWeekDay(resultNext, EventRecurrence.Weekly(4, weekDays))
        assertEquals(1, resultLast.dayOfMonth)
        assertEquals(3, resultLast.month.value)
    }

    @Test
    fun calcNextWeekDay_everyOtherMonday() {
        val weekDays = listOf(1)

        val result = timeOperations.calcNextWeekDay(baseInput, EventRecurrence.Weekly(2, weekDays))
        assertEquals(13, result.dayOfMonth)
        assertEquals(1, result.month.value)

        val resultNext = timeOperations.calcNextWeekDay(result, EventRecurrence.Weekly(2, weekDays))
        assertEquals(27, resultNext.dayOfMonth)
        assertEquals(1, resultNext.month.value)
    }

    @Test
    fun calcNextWeekDay_everyOtherWednesday() {
        val weekDays = listOf(3)

        val result = timeOperations.calcNextWeekDay(baseInput, EventRecurrence.Weekly(2, weekDays))
        assertEquals(15, result.dayOfMonth)
        assertEquals(1, result.month.value)

        val resultNext = timeOperations.calcNextWeekDay(result, EventRecurrence.Weekly(2, weekDays))
        assertEquals(29, resultNext.dayOfMonth)
        assertEquals(1, resultNext.month.value)
    }

    @Test
    fun calcNextWeekDay_everyOtherSunday() {
        val weekDays = listOf(7)

        val result = timeOperations.calcNextWeekDay(baseInput, EventRecurrence.Weekly(2, weekDays))
        assertEquals(5, result.dayOfMonth)
        assertEquals(1, result.month.value)

        val resultNext = timeOperations.calcNextWeekDay(result, EventRecurrence.Weekly(2, weekDays))
        assertEquals(19, resultNext.dayOfMonth)
        assertEquals(1, resultNext.month.value)
    }

    @Test
    fun calcNextWeekDay_everySaSu() {
        val weekDays = listOf(6,7)
        val result = timeOperations.calcNextWeekDay(baseInput, EventRecurrence.Weekly(1, weekDays))

        assertEquals(4, result.dayOfMonth)
        assertEquals(1, result.month.value)

        val resultNext = timeOperations.calcNextWeekDay(result, EventRecurrence.Weekly(1, weekDays))

        assertEquals(5, resultNext.dayOfMonth)
        assertEquals(1, resultNext.month.value)
    }

    @Test
    fun calcNextWeekDay_everySuMoSa() {
        val weekDays = listOf(1,5,7) // mo, fr, su

        val resultOne = timeOperations.calcNextWeekDay(baseInput, EventRecurrence.Weekly(1, weekDays))
        assertEquals(3, resultOne.dayOfMonth)
        assertEquals(1, resultOne.month.value)

        val resultTwo = timeOperations.calcNextWeekDay(resultOne, EventRecurrence.Weekly(1, weekDays))
        assertEquals(5, resultTwo.dayOfMonth)
        assertEquals(1, resultTwo.month.value)

        val resultThree = timeOperations.calcNextWeekDay(resultTwo, EventRecurrence.Weekly(1, weekDays))
        assertEquals(6, resultThree.dayOfMonth)
        assertEquals(1, resultThree.month.value)
    }

    @Test
    fun calcNextWeekDay_everyOtherWeekOnSuMoSa() {
        val weekDays = listOf(1,5,7) // mo, fr, su

        val resultOne = timeOperations.calcNextWeekDay(baseInput, EventRecurrence.Weekly(2, weekDays))
        assertEquals(3, resultOne.dayOfMonth)
        assertEquals(1, resultOne.month.value)

        val resultTwo = timeOperations.calcNextWeekDay(resultOne, EventRecurrence.Weekly(2, weekDays))
        assertEquals(5, resultTwo.dayOfMonth)
        assertEquals(1, resultTwo.month.value)

        val resultThree = timeOperations.calcNextWeekDay(resultTwo, EventRecurrence.Weekly(2, weekDays))
        assertEquals(13, resultThree.dayOfMonth)
        assertEquals(1, resultThree.month.value)

        val resultFour = timeOperations.calcNextWeekDay(resultThree, EventRecurrence.Weekly(2, weekDays))
        assertEquals(17, resultFour.dayOfMonth)
        assertEquals(1, resultFour.month.value)

        val resultFive = timeOperations.calcNextWeekDay(resultFour, EventRecurrence.Weekly(2, weekDays))
        assertEquals(19, resultFive.dayOfMonth)
        assertEquals(1, resultFive.month.value)

        val resultSix = timeOperations.calcNextWeekDay(resultFive, EventRecurrence.Weekly(2, weekDays))
        assertEquals(27, resultSix.dayOfMonth)
        assertEquals(1, resultSix.month.value)

        val resultSeven = timeOperations.calcNextWeekDay(resultSix, EventRecurrence.Weekly(2, weekDays))
        assertEquals(31, resultSeven.dayOfMonth)
        assertEquals(1, resultSeven.month.value)
    }

    @Test
    fun calcNextWeekDay_every3WeeksOnSuMoSa() {
        val weekDays = listOf(1,5,7) // mo, fr, su

        val resultOne = timeOperations.calcNextWeekDay(baseInput, EventRecurrence.Weekly(4, weekDays))
        assertEquals(3, resultOne.dayOfMonth)
        assertEquals(1, resultOne.month.value)

        val resultTwo = timeOperations.calcNextWeekDay(resultOne, EventRecurrence.Weekly(4, weekDays))
        assertEquals(5, resultTwo.dayOfMonth)
        assertEquals(1, resultTwo.month.value)

        val resultThree = timeOperations.calcNextWeekDay(resultTwo, EventRecurrence.Weekly(4, weekDays))
        assertEquals(27, resultThree.dayOfMonth)
        assertEquals(1, resultThree.month.value)

        val resultFour = timeOperations.calcNextWeekDay(resultThree, EventRecurrence.Weekly(4, weekDays))
        assertEquals(31, resultFour.dayOfMonth)
        assertEquals(1, resultFour.month.value)

        val resultFive = timeOperations.calcNextWeekDay(resultFour, EventRecurrence.Weekly(4, weekDays))
        assertEquals(2, resultFive.dayOfMonth)
        assertEquals(2, resultFive.month.value)
    }
}