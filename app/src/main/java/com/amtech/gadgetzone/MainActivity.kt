package com.amtech.gadgetzone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.amtech.gadgetzone.databinding.ActivityMainBinding
import com.amtech.gadgetzone.sharedpreferences.SessionManager
import com.amtech.gadgetzone.wishlist.Wishlist
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private var context = this@MainActivity
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    lateinit var sessionManager: SessionManager
    private lateinit var bottomNav: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        sessionManager = SessionManager(context)
        bottomNav = binding.bottomNavigationView
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.hostFragment)
        val navController = navHostFragment!!.findNavController()
        val popupMenu = PopupMenu(this, null)
        popupMenu.inflate(R.menu.bootom_nav_menu)
        binding.bottomNavigationView.setupWithNavController(navController)

        with(binding) {
            navController.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.fragment_Home -> {
                        appCompatTextView2.text = "Home"
                    }
                    R.id.fragment_cart -> {
                        appCompatTextView2.text = "My Cart"
                    }
                    R.id.fragment_notification -> {
                        appCompatTextView2.text = "Notification"
                    }
                    R.id.fragment_profile -> {
                        appCompatTextView2.text = "Profile"
                    }
                }
            }
            layoutWishList.setOnClickListener {
                startActivity(Intent(this@MainActivity,Wishlist::class.java))
            }
        }

    }
}