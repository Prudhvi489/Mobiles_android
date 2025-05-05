package com.mobivault.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.mobivault.R
import com.mobivault.databinding.ActivitySplashBinding
import com.mobivault.ui.login.LoginActivity
import com.mobivault.ui.mobiledetails.MobilesListActivity
import com.mobivault.utils.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    @Inject
    lateinit var sm:SessionManager
    lateinit var binding:ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding=DataBindingUtil.setContentView(this,R.layout.activity_splash)
        initUI()
    }

    private fun initUI() {
        // Using Handler to add a delay of 3 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            // if the user loged in then navigate to home else login activity
             if(sm.isUserLoggedIn()){
                startActivity(Intent(this, MobilesListActivity::class.java))
            }else{
                startActivity(Intent(this,LoginActivity::class.java))
            }
            finish() // Finish SplashActivity so it won't appear again
        }, 3000)
    }
}