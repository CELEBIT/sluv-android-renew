package com.sluv.sluv.src.splash

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.kakao.sdk.common.util.Utility
import com.sluv.sluv.config.BaseActivity
import com.sluv.sluv.databinding.ActivitySplashBinding
import com.sluv.sluv.src.login.LoginActivity

class SplashActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)



    }
}