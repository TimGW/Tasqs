package com.timgortworst.roomy.presentation.features.settings.presenter

import com.timgortworst.roomy.R
import com.timgortworst.roomy.data.SharedPrefs
import com.timgortworst.roomy.presentation.features.settings.SettingsPresenter
import com.timgortworst.roomy.presentation.features.settings.SettingsView
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyZeroInteractions
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SettingsPresenterTest {
    @Mock
    private lateinit var view: SettingsView

    @Mock
    lateinit var sharedPrefs: SharedPrefs

    lateinit var presenter: SettingsPresenter

    @Before
    fun setUp() {
        presenter = SettingsPresenter(view, sharedPrefs)
    }

    @Test
    fun onAppVersionClick_below7_doNothing() {
        presenter.onAppVersionClick(6)

        verifyZeroInteractions(view)
        verifyZeroInteractions(sharedPrefs)
    }

    @Test
    fun onAppVersionClick_7_showMessage() {
        val counter = 7
        presenter.onAppVersionClick(counter)

        verifyZeroInteractions(sharedPrefs)
        verify(view).toasti(R.string.easter_egg_message, 10 - counter)
    }

    @Test
    fun onAppVersionClick_9_showMessage() {
        val counter = 9
        presenter.onAppVersionClick(counter)

        verifyZeroInteractions(sharedPrefs)
        verify(view).toasti(R.string.easter_egg_message, 10 - counter)
    }

    @Test
    fun onAppVersionClick_10orAbove_enableEasterEgg() {
        val counter = 10
        presenter.onAppVersionClick(counter)

        verify(sharedPrefs).setAdsEnabled(false)
        verify(view).toasti(R.string.easter_egg_enabled)
    }
}