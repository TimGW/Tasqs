package com.timgortworst.roomy.ui.category.presenter

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.firestore.DocumentChange
import com.timgortworst.roomy.model.Category
import com.timgortworst.roomy.repository.BaseResponse
import com.timgortworst.roomy.repository.CategoryRepository
import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.ui.category.view.CategoryListView
import com.timgortworst.roomy.utils.CoroutineLifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class CategoryListPresenter @Inject constructor(
        val view: CategoryListView,
        private val categoryRepository: CategoryRepository,
        private val userRepository: UserRepository
) : BaseResponse(), DefaultLifecycleObserver {
    private val scope = CoroutineLifecycleScope(Dispatchers.Main)

    init {
        if (view is LifecycleOwner) {
            view.lifecycle.addObserver(scope)
        }
    }

    fun listenToCategories() = scope.launch {
        categoryRepository.listenToCategoriesForHousehold(userRepository.getHouseholdIdForUser(), this@CategoryListPresenter)
    }

    fun detachCategoryListener() {
        categoryRepository.detachCategoryListener()
    }

    fun deleteCategory(agendaEventCategory: Category) = scope.launch {
        categoryRepository.deleteCategory(agendaEventCategory)
    }

    override fun renderSuccessfulState(dc: List<DocumentChange>, totalDataSetSize: Int) {
        view.setLoadingView(false)
        view.presentEmptyView(totalDataSetSize == 0)

        dc.forEach {
            val category = it.document.toObject(Category::class.java)
            when (it.type) {
                DocumentChange.Type.ADDED -> view.presentAddedCategory(category)
                DocumentChange.Type.MODIFIED -> view.presentEditedCategory(category)
                DocumentChange.Type.REMOVED -> view.presentDeletedCategory(category)
            }
        }
    }

    override fun renderLoadingState() {
        view.setLoadingView(true)
    }

    override fun renderUnsuccessfulState(throwable: Throwable) {
        view.setLoadingView(false)
        view.setErrorView(true)
    }
}
