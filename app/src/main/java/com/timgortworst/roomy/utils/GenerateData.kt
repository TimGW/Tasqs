package com.timgortworst.roomy.utils

import com.timgortworst.roomy.model.Category

object GenerateData {

    fun setupCategoriesForHousehold(householdId: String) = mutableListOf<Category>().apply {
        add(Category(name = "Dusting", description = "description", householdId = householdId))
        add(Category(name = "Sweeping", description = "description", householdId = householdId))
        add(Category(name = "Vacuuming", description = "description", householdId = householdId))
        add(Category(name = "Washing dishes", description = "description", householdId = householdId))
        add(Category(name = "Feeding pets", description = "description", householdId = householdId))
        add(Category(name = "Doing laundry", description = "description", householdId = householdId))
        add(Category(name = "Preparing meals", description = "description", householdId = householdId))
        add(Category(name = "Cleaning bathrooms", description = "description", householdId = householdId))
        add(Category(name = "Washing bedding", description = "description", householdId = householdId))
        add(Category(name = "Mopping floors", description = "description", householdId = householdId))
        add(Category(name = "Watering plants", description = "description", householdId = householdId))
        add(Category(name = "Mowing the lawn", description = "description", householdId = householdId))
        add(Category(name = "Weeding the garden", description = "description", householdId = householdId))
        add(Category(name = "Taking out the trash", description = "description", householdId = householdId))
        add(Category(name = "Wash the car", description = "description", householdId = householdId))
        add(Category(name = "Washing windows", description = "description", householdId = householdId))
        add(Category(name = "Bathing pets", description = "description", householdId = householdId))
        add(Category(name = "Clean refrigerator", description = "description", householdId = householdId))
        add(Category(name = "Change air filters on furnace or air conditioner", description = "description", householdId = householdId))
        add(Category(name = "Clean blinds", description = "description", householdId = householdId))
        add(Category(name = "Vacuum curtains", description = "description", householdId = householdId))
        add(Category(name = "Shampooing the carpets", description = "description", householdId = householdId))
        add(Category(name = "Winterize the house", description = "description", householdId = householdId))
        add(Category(name = "Clean garage", description = "description", householdId = householdId))
        add(Category(name = "Prune trees and shrubs", description = "description", householdId = householdId))
    }
}