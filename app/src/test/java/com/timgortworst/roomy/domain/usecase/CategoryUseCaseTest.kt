//package com.timgortworst.roomy.domain.usecase
//
//import com.timgortworst.roomy.TestCoroutineRule
//import com.timgortworst.roomy.data.repository.UserRepository
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.mockito.Mock
//import org.mockito.Mockito.*
//import org.mockito.junit.MockitoJUnitRunner
//
//@RunWith(MockitoJUnitRunner::class)
//class CategoryUseCaseTest {
//    @Mock lateinit var categoryRepository: CategoryRepository
//    @Mock lateinit var userRepository: UserRepository
//
//    lateinit var useCase: CategoryUseCase
//
//    @get:Rule val testCoroutineRule = TestCoroutineRule()
//
//    @Before
//    fun setUp() {
//        useCase = CategoryUseCase(categoryRepository, userRepository)
//    }
//
//    @Test
//    fun listenToCategoriesForHousehold() = testCoroutineRule.runBlockingTest {
//        val categoryListPresenter = mock(CategoryListPresenter::class.java)
//        val householdId = userRepository.currentHouseholdIdForCurrentUser()
//
//        useCase.listenToCategoriesForHousehold(categoryListPresenter)
//
//        verify(categoryRepository).listenToCategoriesForHousehold(householdId, categoryListPresenter)
//    }
//
//    @Test
//    fun detachCategoryListener() = testCoroutineRule.runBlockingTest {
//        useCase.detachCategoryListener()
//
//        verify(categoryRepository).detachCategoryListener()
//    }
//
//    @Test
//    fun deleteCategory() = testCoroutineRule.runBlockingTest {
//        val category = mock(Category::class.java)
//
//        useCase.deleteCategory(category)
//
//        verify(categoryRepository).deleteCategory(category)
//    }
//
//    @Test
//    fun createCategoryBatch() = testCoroutineRule.runBlockingTest {
//        val list = mutableListOf<Category>()
//
//        useCase.createCategoryBatch(list)
//
//        verify(categoryRepository).createCategoryBatch(list)
//    }
//
//    @Test
//    fun updateCategory() = testCoroutineRule.runBlockingTest {
//        val id = "id"
//        val name = "name"
//        val description = "desc"
//
//        useCase.updateCategory(id, name, description)
//
//        verify(categoryRepository).updateCategory(id, name, description)
//    }
//
//    @Test
//    fun createCategory()  = testCoroutineRule.runBlockingTest {
//        val name = "name"
//        val description = "desc"
//        val householdId = "123456"
//
//        `when`(userRepository.currentHouseholdIdForCurrentUser()).thenReturn(householdId)
//
//        useCase.createCategory(name, description)
//
//        verify(categoryRepository).createCategory(name, description, householdId)
//    }
//}