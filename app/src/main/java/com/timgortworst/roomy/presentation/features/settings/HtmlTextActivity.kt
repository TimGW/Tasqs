package com.timgortworst.roomy.presentation.features.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.appcompat.app.AppCompatActivity
import com.timgortworst.roomy.R
import com.timgortworst.roomy.databinding.ActivityHtmlTextBinding
import com.timgortworst.roomy.domain.utils.fromHtml
import kotlinx.android.synthetic.main.activity_html_text.*

class HtmlTextActivity : AppCompatActivity() {
    private lateinit var htmlText: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_html_text)

        title = intent.getStringExtra(INTENT_EXTRA_TITLE) ?: ""
        htmlText = intent.getStringExtra(INTENT_EXTRA_HTML_TEXT) ?: return
        setDisclaimerText(htmlText)
    }

    private fun setDisclaimerText(htmlText: String) {
        val htmlString = htmlText.fromHtml()
        html_text.movementMethod = LinkMovementMethod.getInstance()
        html_text.text = htmlString
    }

    companion object {
        private const val INTENT_EXTRA_HTML_TEXT = "INTENT_EXTRA_HTML_TEXT"
        private const val INTENT_EXTRA_TITLE = "INTENT_EXTRA_TITLE"

        fun start(context: Context, htmlText: String, title: String) {
            val intent = Intent(context, HtmlTextActivity::class.java)
            intent.putExtra(INTENT_EXTRA_HTML_TEXT, htmlText)
            intent.putExtra(INTENT_EXTRA_TITLE, title)
            context.startActivity(intent)
        }
    }
}
