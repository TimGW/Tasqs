package com.timgortworst.roomy.utils

import com.timgortworst.roomy.model.EventCategory

object GenerateData {

    fun eventCategories() : MutableList<EventCategory>{
        val listOfTasks = mutableListOf<EventCategory>()
        listOfTasks.add(EventCategory("1", "Dusting", "description"))
        listOfTasks.add(EventCategory("2", "Sweeping", "description"))
        listOfTasks.add(EventCategory("3", "Vacuuming", "description"))
        listOfTasks.add(EventCategory("4", "Washing dishes", "description"))
        listOfTasks.add(EventCategory("5", "Feeding pets", "description"))
        listOfTasks.add(EventCategory("6", "Doing laundry", "description"))
        listOfTasks.add(EventCategory("7", "Preparing meals", "description"))
        listOfTasks.add(EventCategory("8", "Cleaning bathrooms", "description"))
        listOfTasks.add(EventCategory("9", "Washing bedding", "description"))
        listOfTasks.add(EventCategory("10", "Mopping floors", "description"))
        listOfTasks.add(EventCategory("11", "Watering plants", "description"))
        listOfTasks.add(EventCategory("12", "Mowing the lawn", "description"))
        listOfTasks.add(EventCategory("13", "Weeding the garden", "description"))
        listOfTasks.add(EventCategory("14", "Taking out the trash", "description"))
        listOfTasks.add(EventCategory("15", "Wash the car", "description"))
        listOfTasks.add(EventCategory("16", "Washing windows", "description"))
        listOfTasks.add(EventCategory("17", "Bathing pets", "description"))
        listOfTasks.add(EventCategory("18", "Clean refrigerator", "description"))
        listOfTasks.add(EventCategory("19", "Change air filters on furnace or air conditioner", "description"))
        listOfTasks.add(EventCategory("20", "Clean blinds", "description"))
        listOfTasks.add(EventCategory("21", "Vacuum curtains", "description"))
        listOfTasks.add(EventCategory("22", "Shampooing the carpets", "description"))
        listOfTasks.add(EventCategory("23", "Winterize the house", "description"))
        listOfTasks.add(EventCategory("24", "Clean garage", "description"))
        listOfTasks.add(EventCategory("25", "Prune trees and shrubs", "description"))
        return listOfTasks
    }
}