package com.timgortworst.roomy.presentation.features.category.presenter

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.firestore.DocumentChange
import com.timgortworst.roomy.R
import com.timgortworst.roomy.data.model.Category
import com.timgortworst.roomy.domain.ApiStatus
import com.timgortworst.roomy.domain.usecase.CategoryUseCase
import com.timgortworst.roomy.presentation.base.CoroutineLifecycleScope
import com.timgortworst.roomy.presentation.features.category.view.CategoryListView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class CategoryListPresenter @Inject constructor(
        private val view: CategoryListView,
        private val categoryUseCase: CategoryUseCase
) : ApiStatus(), DefaultLifecycleObserver {
    private val scope = CoroutineLifecycleScope(Dispatchers.Main)

    init {
        if (view is LifecycleOwner) {
            view.lifecycle.addObserver(scope)
        }
    }

    fun listenToCategories() = scope.launch {
        view.presentEmptyView(true)

        categoryUseCase.listenToCategoriesForHousehold(this@CategoryListPresenter)
    }

    fun detachCategoryListener() {
        categoryUseCase.detachCategoryListener()
    }

    fun deleteCategory(agendaEventCategory: Category) = scope.launch {
        categoryUseCase.deleteCategory(agendaEventCategory)
    }

    fun createCategoryBatch(generatedListOfCategories: MutableList<Category>) = scope.launch {
        categoryUseCase.createCategoryBatch(generatedListOfCategories)
    }

    override fun renderSuccessfulState(dc: List<Pair<*, DocumentChange.Type>>, totalDataSetSize: Int, hasPendingWrites: Boolean) {
        view.setLoadingView(false)
        view.setErrorView(false)
        view.presentEmptyView(totalDataSetSize == 0)

        dc.forEach {
            val category = (it.first as? Category) ?: return

            when (it.second) {
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
        view.setErrorView(true, R.string.error_list_state_title, R.string.error_list_state_text)
    }
}
