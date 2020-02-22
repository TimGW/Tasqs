package com.timgortworst.roomy.domain.utils

import com.timgortworst.roomy.domain.model.EventRecurrence
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
    fun calcNextEvent_everyMonth() {
        val result = timeOperations.nextEvent(baseInput, EventRecurrence.Monthly(1))
        assertEquals(1, result.dayOfMonth)
        assertEquals(2, result.month.value)

        val resultNext = timeOperations.nextEvent(result, EventRecurrence.Monthly(1))
        assertEquals(1, resultNext.dayOfMonth)
        assertEquals(3, resultNext.month.value)

        val resultLast = timeOperations.nextEvent(resultNext, EventRecurrence.Monthly(1))
        assertEquals(1, resultLast.dayOfMonth)
        assertEquals(4, resultLast.month.value)
    }

    @Test
    fun calcNextEvent_everyOtherMonth() {
        val result = timeOperations.nextEvent(baseInput, EventRecurrence.Monthly(2))
        assertEquals(1, result.dayOfMonth)
        assertEquals(3, result.month.value)

        val resultNext = timeOperations.nextEvent(result, EventRecurrence.Monthly(2))
        assertEquals(1, resultNext.dayOfMonth)
        assertEquals(5, resultNext.month.value)

        val resultLast = timeOperations.nextEvent(resultNext, EventRecurrence.Monthly(2))
        assertEquals(1, resultLast.dayOfMonth)
        assertEquals(7, resultLast.month.value)
    }

    @Test
    fun calcNextEvent_everyDay() {
        val result = timeOperations.nextEvent(baseInput, EventRecurrence.Daily(1))
        assertEquals(2, result.dayOfMonth)
        assertEquals(1, result.month.value)

        val resultNext = timeOperations.nextEvent(result, EventRecurrence.Daily(1))
        assertEquals(3, resultNext.dayOfMonth)
        assertEquals(1, resultNext.month.value)

        val resultLast = timeOperations.nextEvent(resultNext, EventRecurrence.Daily(1))
        assertEquals(4, resultLast.dayOfMonth)
        assertEquals(1, resultLast.month.value)
    }

    @Test
    fun calcNextEvent_everyOtherDay() {
        val result = timeOperations.nextEvent(baseInput, EventRecurrence.Daily(2))
        assertEquals(3, result.dayOfMonth)
        assertEquals(1, result.month.value)

        val resultNext = timeOperations.nextEvent(result, EventRecurrence.Daily(2))
        assertEquals(5, resultNext.dayOfMonth)
        assertEquals(1, resultNext.month.value)

        val resultLast = timeOperations.nextEvent(resultNext, EventRecurrence.Daily(2))
        assertEquals(7, resultLast.dayOfMonth)
        assertEquals(1, resultLast.month.value)
    }

    @Test
    fun calcNextEvent_everyYear() {
        val result = timeOperations.nextEvent(baseInput, EventRecurrence.Annually(1))
        assertEquals(1, result.dayOfMonth)
        assertEquals(1, result.month.value)
        assertEquals(2021, result.year)

        val resultNext = timeOperations.nextEvent(result, EventRecurrence.Annually(1))
        assertEquals(1, resultNext.dayOfMonth)
        assertEquals(1, resultNext.month.value)
        assertEquals(2022, resultNext.year)

        val resultLast = timeOperations.nextEvent(resultNext, EventRecurrence.Annually(1))
        assertEquals(1, resultLast.dayOfMonth)
        assertEquals(1, resultLast.month.value)
        assertEquals(2023, resultLast.year)
    }

    @Test
    fun calcNextEvent_everyOtherYear() {
        val result = timeOperations.nextEvent(baseInput, EventRecurrence.Annually(2))
        assertEquals(1, result.dayOfMonth)
        assertEquals(1, result.month.value)
        assertEquals(2022, result.year)

        val resultNext = timeOperations.nextEvent(result, EventRecurrence.Annually(2))
        assertEquals(1, resultNext.dayOfMonth)
        assertEquals(1, resultNext.month.value)
        assertEquals(2024, resultNext.year)

        val resultLast = timeOperations.nextEvent(resultNext, EventRecurrence.Annually(2))
        assertEquals(1, resultLast.dayOfMonth)
        assertEquals(1, resultLast.month.value)
        assertEquals(2026, resultLast.year)
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