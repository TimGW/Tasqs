package com.timgortworst.roomy.utils

import com.timgortworst.roomy.model.Category

object GenerateData {

    fun eventCategories() : MutableList<Category>{
        val listOfTasks = mutableListOf<Category>()
        listOfTasks.add(Category("1", "Dusting", "description"))
        listOfTasks.add(Category("2", "Sweeping", "description"))
        listOfTasks.add(Category("3", "Vacuuming", "description"))
        listOfTasks.add(Category("4", "Washing dishes", "description"))
        listOfTasks.add(Category("5", "Feeding pets", "description"))
        listOfTasks.add(Category("6", "Doing laundry", "description"))
        listOfTasks.add(Category("7", "Preparing meals", "description"))
        listOfTasks.add(Category("8", "Cleaning bathrooms", "description"))
        listOfTasks.add(Category("9", "Washing bedding", "description"))
        listOfTasks.add(Category("10", "Mopping floors", "description"))
        listOfTasks.add(Category("11", "Watering plants", "description"))
        listOfTasks.add(Category("12", "Mowing the lawn", "description"))
        listOfTasks.add(Category("13", "Weeding the garden", "description"))
        listOfTasks.add(Category("14", "Taking out the trash", "description"))
        listOfTasks.add(Category("15", "Wash the car", "description"))
        listOfTasks.add(Category("16", "Washing windows", "description"))
        listOfTasks.add(Category("17", "Bathing pets", "description"))
        listOfTasks.add(Category("18", "Clean refrigerator", "description"))
        listOfTasks.add(Category("19", "Change air filters on furnace or air conditioner", "description"))
        listOfTasks.add(Category("20", "Clean blinds", "description"))
        listOfTasks.add(Category("21", "Vacuum curtains", "description"))
        listOfTasks.add(Category("22", "Shampooing the carpets", "description"))
        listOfTasks.add(Category("23", "Winterize the house", "description"))
        listOfTasks.add(Category("24", "Clean garage", "description"))
        listOfTasks.add(Category("25", "Prune trees and shrubs", "description"))
        return listOfTasks
    }
}