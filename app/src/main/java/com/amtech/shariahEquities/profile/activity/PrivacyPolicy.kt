package com.amtech.shariahEquities.profile.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
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
        link=intent.getStringExtra("link").toString()
        binding.appCompatTextView2.text=title
//        if(title=="Privacy Policy"){
//            link =  "http://ehcf.in/api/users/get_pdf?type=privacy"
//        }else{
//            link =  "https://shariahsecurities.com/contact-us-2/"
//        }

        if(title =="Contact Us")
        {
            binding.webView.visibility = View.GONE
            binding.tvEmail.visibility = View.VISIBLE
            binding.tvMobile.visibility = View.VISIBLE
        }else
        {
            binding.tvEmail.visibility = View.GONE
            binding.tvMobile.visibility = View.GONE
        }
        with(binding){
            imgBack.setOnClickListener {
                onBackPressed()
            }



            webView.settings.javaScriptEnabled = true
            webView.settings.domStorageEnabled = true
            webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE

// Enable mixed content if needed
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
            }

// Set WebViewClient to handle HTTPS and navigation
            webView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    view.loadUrl(url)
                    return true
                }
            }

// Load URL
            webView.loadUrl(link)


        }

    }
}