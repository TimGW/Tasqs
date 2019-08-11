package com.timgortworst.roomy.ui.features.category.presenter

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.firestore.DocumentChange
import com.timgortworst.roomy.R
import com.timgortworst.roomy.data.model.Category
import com.timgortworst.roomy.data.repository.BaseResponse
import com.timgortworst.roomy.domain.usecase.CategoryUseCase
import com.timgortworst.roomy.domain.utils.CoroutineLifecycleScope
import com.timgortworst.roomy.ui.features.category.view.CategoryListView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class CategoryListPresenter @Inject constructor(
        val view: CategoryListView,
        val categoryInteractor: CategoryUseCase
) : BaseResponse(), DefaultLifecycleObserver {
    private val scope = CoroutineLifecycleScope(Dispatchers.Main)

    init {
        if (view is LifecycleOwner) {
            view.lifecycle.addObserver(scope)
        }
    }

    fun listenToCategories() = scope.launch {
        categoryInteractor.listenToCategoriesForHousehold(this@CategoryListPresenter)
    }

    fun detachCategoryListener() {
        categoryInteractor.detachCategoryListener()
    }

    fun deleteCategory(agendaEventCategory: Category) = scope.launch {
        categoryInteractor.deleteCategory(agendaEventCategory)
    }

    fun generateCategories() = scope.launch {
        categoryInteractor.setupCategoriesForHousehold()
    }

    override fun renderSuccessfulState(dc: List<DocumentChange>, totalDataSetSize: Int, hasPendingWrites: Boolean) {
        view.setLoadingView(false)
        view.setErrorView(false)
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
        view.setErrorView(true, R.string.error_list_state_title, R.string.error_list_state_text)
    }
}
