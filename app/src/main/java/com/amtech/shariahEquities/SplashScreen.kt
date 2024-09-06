package com.amtech.shariahEquities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.amtech.shariahEquities.login.Login
import com.amtech.shariahEquities.sharedpreferences.SessionManager
import com.sellacha.tlismiherbs.databinding.ActivitySplashScreenBinding

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    private  val binding by lazy {
        ActivitySplashScreenBinding.inflate(layoutInflater)
    }
    private lateinit var sessionManager: SessionManager
    private var context=this@SplashScreen
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        sessionManager= SessionManager(this@SplashScreen)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        Handler().postDelayed({
            if (sessionManager.isLogin){
                startActivity(Intent(context,MainActivity::class.java))
            }else {
                startActivity(Intent(this, Login::class.java))
            }
            finish()
        }, 2000)
    }

}