//package com.timgortworst.roomy.presentation.features.setup.presenter
//
//import com.timgortworst.roomy.TestCoroutineRule
//import com.timgortworst.roomy.domain.usecase.SetupUseCase
//import com.timgortworst.roomy.presentation.features.splash.view.SetupView
//import com.timgortworst.roomy.presentation.features.splash.presenter.SetupPresenter
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.mockito.Mock
//import org.mockito.Mockito.verify
//import org.mockito.junit.MockitoJUnitRunner
//
//@RunWith(MockitoJUnitRunner::class)
//class SetupPresenterTest {
//    @Mock lateinit var view: SetupView
//    @Mock lateinit var useCase: SetupUseCase
//
//    @get:Rule
//    val testCoroutineRule = TestCoroutineRule()
//
//    lateinit var presenter: SetupPresenter
//
//    @Before
//    fun setUp() {
//        presenter = SetupPresenter(view, useCase)
//    }
//
//    @Test
//    fun referredSetup() = testCoroutineRule.runBlockingTest {
//        val referredHouseholdId = ""
//
//        presenter.referredSetup(referredHouseholdId)
//
//        verify(useCase).createUser()
//    }
//
//    @Test
//    fun changeCurrentUserHousehold() {
//    }
//}