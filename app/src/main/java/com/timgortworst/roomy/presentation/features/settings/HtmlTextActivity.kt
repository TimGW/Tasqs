package com.timgortworst.roomy.presentation.features.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.timgortworst.roomy.databinding.ActivityHtmlTextBinding
import org.koin.android.viewmodel.ext.android.viewModel

class HtmlTextActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHtmlTextBinding
    private lateinit var htmlText: String
    private val settingsViewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHtmlTextBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            viewModel = settingsViewModel
            lifecycleOwner = this@HtmlTextActivity
        }

        title = intent.getStringExtra(INTENT_EXTRA_TITLE) ?: ""
        htmlText = intent.getStringExtra(INTENT_EXTRA_HTML_TEXT) ?: return
        settingsViewModel.setDisclaimerText(htmlText)
    }

    companion object {
        private const val INTENT_EXTRA_HTML_TEXT = "INTENT_EXTRA_HTML_TEXT"
        private const val INTENT_EXTRA_TITLE = "INTENT_EXTRA_TITLE"

        fun intentBuilder(context: Context, htmlText: String, title: String): Intent {
            val intent = Intent(context, HtmlTextActivity::class.java)
            intent.putExtra(INTENT_EXTRA_HTML_TEXT, htmlText)
            intent.putExtra(INTENT_EXTRA_TITLE, title)
            return intent
        }
    }
}
