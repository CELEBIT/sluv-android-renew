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

        // 자동로그인 API 호출
        // 성공 시, MainActivity로 전환 후 웹뷰 띄우기
        // 실패 시, LoginActivity로 전환

        // 임시 화면 전환
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)

    }
}