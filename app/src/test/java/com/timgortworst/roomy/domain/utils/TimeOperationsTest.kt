package com.timgortworst.roomy.domain.utils

import com.timgortworst.roomy.domain.model.task.TaskRecurrence
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
    fun calcNextTask_everyMonth() {
        val result = timeOperations.nextTask(baseInput, TaskRecurrence.Monthly(1))
        assertEquals(1, result.dayOfMonth)
        assertEquals(2, result.month.value)

        val resultNext = timeOperations.nextTask(result, TaskRecurrence.Monthly(1))
        assertEquals(1, resultNext.dayOfMonth)
        assertEquals(3, resultNext.month.value)

        val resultLast = timeOperations.nextTask(resultNext, TaskRecurrence.Monthly(1))
        assertEquals(1, resultLast.dayOfMonth)
        assertEquals(4, resultLast.month.value)
    }

    @Test
    fun calcNextTask_everyOtherMonth() {
        val result = timeOperations.nextTask(baseInput, TaskRecurrence.Monthly(2))
        assertEquals(1, result.dayOfMonth)
        assertEquals(3, result.month.value)

        val resultNext = timeOperations.nextTask(result, TaskRecurrence.Monthly(2))
        assertEquals(1, resultNext.dayOfMonth)
        assertEquals(5, resultNext.month.value)

        val resultLast = timeOperations.nextTask(resultNext, TaskRecurrence.Monthly(2))
        assertEquals(1, resultLast.dayOfMonth)
        assertEquals(7, resultLast.month.value)
    }

    @Test
    fun calcNextTask_everyDay() {
        val result = timeOperations.nextTask(baseInput, TaskRecurrence.Daily(1))
        assertEquals(2, result.dayOfMonth)
        assertEquals(1, result.month.value)

        val resultNext = timeOperations.nextTask(result, TaskRecurrence.Daily(1))
        assertEquals(3, resultNext.dayOfMonth)
        assertEquals(1, resultNext.month.value)

        val resultLast = timeOperations.nextTask(resultNext, TaskRecurrence.Daily(1))
        assertEquals(4, resultLast.dayOfMonth)
        assertEquals(1, resultLast.month.value)
    }

    @Test
    fun calcNextTask_everyOtherDay() {
        val result = timeOperations.nextTask(baseInput, TaskRecurrence.Daily(2))
        assertEquals(3, result.dayOfMonth)
        assertEquals(1, result.month.value)

        val resultNext = timeOperations.nextTask(result, TaskRecurrence.Daily(2))
        assertEquals(5, resultNext.dayOfMonth)
        assertEquals(1, resultNext.month.value)

        val resultLast = timeOperations.nextTask(resultNext, TaskRecurrence.Daily(2))
        assertEquals(7, resultLast.dayOfMonth)
        assertEquals(1, resultLast.month.value)
    }

    @Test
    fun calcNextTask_everyYear() {
        val result = timeOperations.nextTask(baseInput, TaskRecurrence.Annually(1))
        assertEquals(1, result.dayOfMonth)
        assertEquals(1, result.month.value)
        assertEquals(2021, result.year)

        val resultNext = timeOperations.nextTask(result, TaskRecurrence.Annually(1))
        assertEquals(1, resultNext.dayOfMonth)
        assertEquals(1, resultNext.month.value)
        assertEquals(2022, resultNext.year)

        val resultLast = timeOperations.nextTask(resultNext, TaskRecurrence.Annually(1))
        assertEquals(1, resultLast.dayOfMonth)
        assertEquals(1, resultLast.month.value)
        assertEquals(2023, resultLast.year)
    }

    @Test
    fun calcNextTask_everyOtherYear() {
        val result = timeOperations.nextTask(baseInput, TaskRecurrence.Annually(2))
        assertEquals(1, result.dayOfMonth)
        assertEquals(1, result.month.value)
        assertEquals(2022, result.year)

        val resultNext = timeOperations.nextTask(result, TaskRecurrence.Annually(2))
        assertEquals(1, resultNext.dayOfMonth)
        assertEquals(1, resultNext.month.value)
        assertEquals(2024, resultNext.year)

        val resultLast = timeOperations.nextTask(resultNext, TaskRecurrence.Annually(2))
        assertEquals(1, resultLast.dayOfMonth)
        assertEquals(1, resultLast.month.value)
        assertEquals(2026, resultLast.year)
    }

    @Test
    fun calcNextWeekDay_everySunday() {
        val weekDays = listOf(7)

        val result = timeOperations.calcNextWeekDay(baseInput, TaskRecurrence.Weekly(1, weekDays))
        assertEquals(5, result.dayOfMonth)
        assertEquals(1, result.month.value)

        val resultNext = timeOperations.calcNextWeekDay(result, TaskRecurrence.Weekly(1, weekDays))
        assertEquals(12, resultNext.dayOfMonth)
        assertEquals(1, resultNext.month.value)
    }

    @Test
    fun calcNextWeekDay_every4WeeksOnSunday() {
        val weekDays = listOf(7)

        val result = timeOperations.calcNextWeekDay(baseInput, TaskRecurrence.Weekly(4, weekDays))
        assertEquals(5, result.dayOfMonth)
        assertEquals(1, result.month.value)

        val resultNext = timeOperations.calcNextWeekDay(result, TaskRecurrence.Weekly(4, weekDays))
        assertEquals(2, resultNext.dayOfMonth)
        assertEquals(2, resultNext.month.value)

        val resultLast = timeOperations.calcNextWeekDay(resultNext, TaskRecurrence.Weekly(4, weekDays))
        assertEquals(1, resultLast.dayOfMonth)
        assertEquals(3, resultLast.month.value)
    }

    @Test
    fun calcNextWeekDay_everyOtherMonday() {
        val weekDays = listOf(1)

        val result = timeOperations.calcNextWeekDay(baseInput, TaskRecurrence.Weekly(2, weekDays))
        assertEquals(13, result.dayOfMonth)
        assertEquals(1, result.month.value)

        val resultNext = timeOperations.calcNextWeekDay(result, TaskRecurrence.Weekly(2, weekDays))
        assertEquals(27, resultNext.dayOfMonth)
        assertEquals(1, resultNext.month.value)
    }

    @Test
    fun calcNextWeekDay_everyOtherWednesday() {
        val weekDays = listOf(3)

        val result = timeOperations.calcNextWeekDay(baseInput, TaskRecurrence.Weekly(2, weekDays))
        assertEquals(15, result.dayOfMonth)
        assertEquals(1, result.month.value)

        val resultNext = timeOperations.calcNextWeekDay(result, TaskRecurrence.Weekly(2, weekDays))
        assertEquals(29, resultNext.dayOfMonth)
        assertEquals(1, resultNext.month.value)
    }

    @Test
    fun calcNextWeekDay_everyOtherSunday() {
        val weekDays = listOf(7)

        val result = timeOperations.calcNextWeekDay(baseInput, TaskRecurrence.Weekly(2, weekDays))
        assertEquals(5, result.dayOfMonth)
        assertEquals(1, result.month.value)

        val resultNext = timeOperations.calcNextWeekDay(result, TaskRecurrence.Weekly(2, weekDays))
        assertEquals(19, resultNext.dayOfMonth)
        assertEquals(1, resultNext.month.value)
    }

    @Test
    fun calcNextWeekDay_everySaSu() {
        val weekDays = listOf(6,7)
        val result = timeOperations.calcNextWeekDay(baseInput, TaskRecurrence.Weekly(1, weekDays))

        assertEquals(4, result.dayOfMonth)
        assertEquals(1, result.month.value)

        val resultNext = timeOperations.calcNextWeekDay(result, TaskRecurrence.Weekly(1, weekDays))

        assertEquals(5, resultNext.dayOfMonth)
        assertEquals(1, resultNext.month.value)
    }

    @Test
    fun calcNextWeekDay_everySuMoSa() {
        val weekDays = listOf(1,5,7) // mo, fr, su

        val resultOne = timeOperations.calcNextWeekDay(baseInput, TaskRecurrence.Weekly(1, weekDays))
        assertEquals(3, resultOne.dayOfMonth)
        assertEquals(1, resultOne.month.value)

        val resultTwo = timeOperations.calcNextWeekDay(resultOne, TaskRecurrence.Weekly(1, weekDays))
        assertEquals(5, resultTwo.dayOfMonth)
        assertEquals(1, resultTwo.month.value)

        val resultThree = timeOperations.calcNextWeekDay(resultTwo, TaskRecurrence.Weekly(1, weekDays))
        assertEquals(6, resultThree.dayOfMonth)
        assertEquals(1, resultThree.month.value)
    }

    @Test
    fun calcNextWeekDay_everyOtherWeekOnSuMoSa() {
        val weekDays = listOf(1,5,7) // mo, fr, su

        val resultOne = timeOperations.calcNextWeekDay(baseInput, TaskRecurrence.Weekly(2, weekDays))
        assertEquals(3, resultOne.dayOfMonth)
        assertEquals(1, resultOne.month.value)

        val resultTwo = timeOperations.calcNextWeekDay(resultOne, TaskRecurrence.Weekly(2, weekDays))
        assertEquals(5, resultTwo.dayOfMonth)
        assertEquals(1, resultTwo.month.value)

        val resultThree = timeOperations.calcNextWeekDay(resultTwo, TaskRecurrence.Weekly(2, weekDays))
        assertEquals(13, resultThree.dayOfMonth)
        assertEquals(1, resultThree.month.value)

        val resultFour = timeOperations.calcNextWeekDay(resultThree, TaskRecurrence.Weekly(2, weekDays))
        assertEquals(17, resultFour.dayOfMonth)
        assertEquals(1, resultFour.month.value)

        val resultFive = timeOperations.calcNextWeekDay(resultFour, TaskRecurrence.Weekly(2, weekDays))
        assertEquals(19, resultFive.dayOfMonth)
        assertEquals(1, resultFive.month.value)

        val resultSix = timeOperations.calcNextWeekDay(resultFive, TaskRecurrence.Weekly(2, weekDays))
        assertEquals(27, resultSix.dayOfMonth)
        assertEquals(1, resultSix.month.value)

        val resultSeven = timeOperations.calcNextWeekDay(resultSix, TaskRecurrence.Weekly(2, weekDays))
        assertEquals(31, resultSeven.dayOfMonth)
        assertEquals(1, resultSeven.month.value)
    }

    @Test
    fun calcNextWeekDay_every3WeeksOnSuMoSa() {
        val weekDays = listOf(1,5,7) // mo, fr, su

        val resultOne = timeOperations.calcNextWeekDay(baseInput, TaskRecurrence.Weekly(4, weekDays))
        assertEquals(3, resultOne.dayOfMonth)
        assertEquals(1, resultOne.month.value)

        val resultTwo = timeOperations.calcNextWeekDay(resultOne, TaskRecurrence.Weekly(4, weekDays))
        assertEquals(5, resultTwo.dayOfMonth)
        assertEquals(1, resultTwo.month.value)

        val resultThree = timeOperations.calcNextWeekDay(resultTwo, TaskRecurrence.Weekly(4, weekDays))
        assertEquals(27, resultThree.dayOfMonth)
        assertEquals(1, resultThree.month.value)

        val resultFour = timeOperations.calcNextWeekDay(resultThree, TaskRecurrence.Weekly(4, weekDays))
        assertEquals(31, resultFour.dayOfMonth)
        assertEquals(1, resultFour.month.value)

        val resultFive = timeOperations.calcNextWeekDay(resultFour, TaskRecurrence.Weekly(4, weekDays))
        assertEquals(2, resultFive.dayOfMonth)
        assertEquals(2, resultFive.month.value)
    }
}