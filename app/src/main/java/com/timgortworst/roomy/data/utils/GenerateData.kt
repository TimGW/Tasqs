package com.timgortworst.roomy.data.utils

import android.content.Context
import com.timgortworst.roomy.R
import com.timgortworst.roomy.data.model.Category

class GenerateData(private val context: Context) {

    fun createCategoryList() = mutableListOf<Category>().apply {
        add(Category(name = context.getString(R.string.example_data_bathroom_title),
                description = context.getString(R.string.example_data_bathroom_description)))
        add(Category(name = context.getString(R.string.example_data_kitchen_title), 
                description = context.getString(R.string.example_data_kitchen_description)))
        add(Category(name = context.getString(R.string.example_data_toilet_title)))
        add(Category(name = context.getString(R.string.example_data_dusting_title)))
        add(Category(name = context.getString(R.string.example_data_pets_title)))
        add(Category(name = context.getString(R.string.example_data_groceries_title)))
        add(Category(name = context.getString(R.string.example_data_laundry_title)))
        add(Category(name = context.getString(R.string.example_data_mopping_title)))
        add(Category(name = context.getString(R.string.example_data_trash_title)))
        add(Category(name = context.getString(R.string.example_data_vacuuming_title), 
                description = context.getString(R.string.example_data_vacuuming_description)))
        add(Category(name = context.getString(R.string.example_data_plants_title)))
        add(Category(name = context.getString(R.string.example_data_dishes_title)))
        add(Category(name = context.getString(R.string.example_data_windows_title)))
    }
}