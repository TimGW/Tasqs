package com.timgortworst.roomy.utils

import com.timgortworst.roomy.model.EventCategory

object GenerateData {

    fun eventCategories() : MutableList<EventCategory>{
        val listOfTasks = mutableListOf<EventCategory>()
        listOfTasks.add(EventCategory("1", "Dusting", "description", 3))
        listOfTasks.add(EventCategory("2", "Sweeping", "description", 4))
        listOfTasks.add(EventCategory("3", "Vacuuming", "description", 5))
        listOfTasks.add(EventCategory("4", "Washing dishes", "description", 6))
        listOfTasks.add(EventCategory("5", "Feeding pets", "description", 7))
        listOfTasks.add(EventCategory("6", "Doing laundry", "description", 8))
        listOfTasks.add(EventCategory("7", "Preparing meals", "description", 9))
        listOfTasks.add(EventCategory("8", "Cleaning bathrooms", "description", 10))
        listOfTasks.add(EventCategory("9", "Washing bedding", "description", 11))
        listOfTasks.add(EventCategory("10", "Mopping floors", "description", 12))
        listOfTasks.add(EventCategory("11", "Watering plants", "description", 13))
        listOfTasks.add(EventCategory("12", "Mowing the lawn", "description", 14))
        listOfTasks.add(EventCategory("13", "Weeding the garden", "description", 15))
        listOfTasks.add(EventCategory("14", "Taking out the trash", "description", 16))
        listOfTasks.add(EventCategory("15", "Wash the car", "description", 17))
        listOfTasks.add(EventCategory("16", "Washing windows", "description", 18))
        listOfTasks.add(EventCategory("17", "Bathing pets", "description", 19))
        listOfTasks.add(EventCategory("18", "Clean refrigerator", "description", 20))
        listOfTasks.add(EventCategory("19", "Change air filters on furnace or air conditioner", "description", 21))
        listOfTasks.add(EventCategory("20", "Clean blinds", "description", 22))
        listOfTasks.add(EventCategory("21", "Vacuum curtains", "description", 23))
        listOfTasks.add(EventCategory("22", "Shampooing the carpets", "description", 24))
        listOfTasks.add(EventCategory("23", "Winterize the house", "description", 25))
        listOfTasks.add(EventCategory("24", "Clean garage", "description", 26))
        listOfTasks.add(EventCategory("25", "Prune trees and shrubs", "description", 27))
        return listOfTasks
    }
}