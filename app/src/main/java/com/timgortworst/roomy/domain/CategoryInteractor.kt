package com.timgortworst.roomy.domain

import com.timgortworst.roomy.model.Category
import com.timgortworst.roomy.repository.CategoryRepository
import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.ui.category.presenter.CategoryListPresenter
import com.timgortworst.roomy.utils.GenerateData
import javax.inject.Inject

class CategoryInteractor
@Inject
constructor(private val categoryRepository: CategoryRepository,
            private val userRepository: UserRepository) {

    suspend fun listenToCategoriesForHousehold(categoryListPresenter: CategoryListPresenter) {
        categoryRepository.listenToCategoriesForHousehold(
                userRepository.getHouseholdIdForUser(),
                categoryListPresenter)
    }

    fun detachCategoryListener() {
        categoryRepository.detachCategoryListener()
    }

    suspend fun deleteCategory(agendaEventCategory: Category) {
        categoryRepository.deleteCategory(agendaEventCategory)
    }

    suspend fun setupCategoriesForHousehold() {
        categoryRepository.createCategoryBatch(GenerateData.setupCategoriesForHousehold(userRepository.getHouseholdIdForUser()))
    }
}
