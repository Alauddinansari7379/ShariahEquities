package com.amtech.shariahEquities.profile.activity

import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
 import com.sellacha.tlismiherbs.databinding.ActivityPrivacyPolicyBinding

class PrivacyPolicy : AppCompatActivity() {
    val binding by lazy { ActivityPrivacyPolicyBinding.inflate(layoutInflater) }
    var link=""
    var title=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         setContentView(binding.root)
        title=intent.getStringExtra("title").toString()
        binding.appCompatTextView2.text=title
        link = if(title=="Privacy Policy"){
            "https://shariahsecurities.com/privacy-policy/"
        }else{
            "https://shariahsecurities.com/contact-us-2/"
        }
        with(binding){
            imgBack.setOnClickListener {
                onBackPressed()
            }



            // Enable JavaScript
            webView.settings.javaScriptEnabled = true

            // Enable other settings if needed
            webView.settings.domStorageEnabled = true
            webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE

            // Set a WebViewClient to handle page navigation
            webView.webViewClient = WebViewClient()

            // Load a URL or HTML content
            webView.loadUrl(link)

        }

    }
}