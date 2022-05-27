package com.bike.race.ui.splash

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.webkit.WebView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bike.race.R
import com.bike.race.databinding.ActivitySplashBinding
import com.bike.race.domain.preference.UserPreferenceManager
import com.bike.race.ui.home.HomeActivity
import com.bike.race.utils.ViewUtils
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PrivacyCheckActivity : AppCompatActivity() {

    private val viewModel: SplashViewModel by viewModel()
    private val userPreferenceManager: UserPreferenceManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ViewUtils.setTheme(userPreferenceManager.getTheme())
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        if (viewModel.isPrivacyPolicyAccepted()) {
            continueHome()
        } else {
            val viewBinding = ActivitySplashBinding.inflate(layoutInflater)
            setContentView(viewBinding.root)

            viewBinding.skipLoginButton.setOnClickListener {
                viewModel.acceptPrivacyPolicy()
                continueHome()
            }

            setupTermsAndConditionText(viewBinding.termsAndConditionText)
        }
    }

    private fun setupTermsAndConditionText(termsAndConditionText: TextView) {
        val message = getString(R.string.splash_continue)
        val termsNCondition = getString(R.string.splash_terms_of_service)
        val appender = getString(R.string.splash_and)
        val privacyPolicy = getString(R.string.splash_privacy_policy)

        val spanText = SpannableStringBuilder()
        spanText.append(message)
        spanText.append(" ")
        spanText.append(termsNCondition)
        spanText.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                openTermsAndConditions()
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.color = ds.linkColor    // you can use custom color
                ds.isUnderlineText = true    // this remove the underline
            }
        }, spanText.length - termsNCondition.length, spanText.length, 0)

        spanText.append(" ")
        spanText.append(appender)
        spanText.append(" ")
        spanText.append(privacyPolicy)
        spanText.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                openPrivacyPolicy()
            }

            override fun updateDrawState(textPaint: TextPaint) {
                textPaint.color = textPaint.linkColor    // you can use custom color
                textPaint.isUnderlineText = true    // this remove the underline
            }
        }, spanText.length - privacyPolicy.length, spanText.length, 0)

        termsAndConditionText.movementMethod = LinkMovementMethod.getInstance()
        termsAndConditionText.setText(spanText, TextView.BufferType.SPANNABLE)
    }

    private fun openPrivacyPolicy() {
        val webView = WebView(this)
        webView.loadUrl("file:///android_asset/html/privacy.html")
        AlertDialog.Builder(this, R.style.AppDialogTheme).setView(webView)
            .setPositiveButton("Close") { _, _ -> }.create().show()
    }

    private fun openTermsAndConditions() {
        val webView = WebView(this)
        webView.loadUrl("file:///android_asset/html/terms_conditions.html")
        AlertDialog.Builder(this, R.style.AppDialogTheme).setView(webView)
            .setPositiveButton("Close") { _, _ -> }.create().show()
    }

    private fun continueHome() {
        HomeActivity.open(this)
        finish()
    }

}