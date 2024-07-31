package com.amtech.tlismiherbs.myOrder.activity

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.sellacha.tlismiherbs.R
import com.sellacha.tlismiherbs.databinding.ActivityMyOrderBinding
import com.amtech.tlismiherbs.myOrder.adapter.ViewPagerAdapter
import com.amtech.tlismiherbs.sharedpreferences.SessionManager
import com.google.android.material.tabs.TabLayout

class MyOrder : AppCompatActivity() {
    private var context = this@MyOrder
    private val binding by lazy {
        ActivityMyOrderBinding.inflate(layoutInflater)
    }
    private lateinit var pager: ViewPager // creating object of ViewPager
    private lateinit var tab: TabLayout  // creating object of TabLayout
    private lateinit var sessionManager: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        sessionManager = SessionManager(context)

        with(binding) {
            imgBack.setOnClickListener {
                onBackPressed()
            }
        }

        pager = findViewById(R.id.viewPager)
        tab = findViewById(R.id.tabs)
        val adapter = ViewPagerAdapter(supportFragmentManager)

        val tabs = findViewById<View>(R.id.tabs) as TabLayout
        tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                pager.currentItem = tab.position
                when (tab.position) {
                    0 -> {
                        tabs.setSelectedTabIndicatorColor(Color.parseColor("#FFFFFF"))
                        tabs.setTabTextColors(
                            Color.parseColor("#FFFFFF"),
                            Color.parseColor("#FC2227")
                        )

                    }
                    1 -> {
                        tabs.setSelectedTabIndicatorColor(Color.parseColor("#FFFFFF"))
                        tabs.setTabTextColors(
                            Color.parseColor("#FFFFFF"),
                            Color.parseColor("#FC2227")
                        )

                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        adapter.addFragment(ProgressFragment(), "In Progress")
        adapter.addFragment(CompleteFragment(), "Complete")
        pager.adapter = adapter
        tab.setupWithViewPager(pager)
    }
}