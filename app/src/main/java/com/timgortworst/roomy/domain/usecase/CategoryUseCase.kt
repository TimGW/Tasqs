package com.timgortworst.roomy.domain.usecase

import com.timgortworst.roomy.data.model.Category
import com.timgortworst.roomy.data.repository.CategoryRepository
import com.timgortworst.roomy.data.repository.UserRepository
import com.timgortworst.roomy.data.utils.GenerateData
import com.timgortworst.roomy.ui.features.category.presenter.CategoryListPresenter
import javax.inject.Inject

class CategoryUseCase
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

    suspend fun updateCategory(categoryId: String, name: String, description: String) {
        categoryRepository.updateCategory(categoryId, name, description)
    }

    suspend fun createCategory(name: String, description: String) {
        categoryRepository.createCategory(name, description, userRepository.getHouseholdIdForUser())
    }
}
