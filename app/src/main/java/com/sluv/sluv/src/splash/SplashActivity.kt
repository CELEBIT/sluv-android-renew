package com.sluv.sluv.src.splash

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.kakao.sdk.common.util.Utility
import com.sluv.sluv.R
import com.sluv.sluv.config.ApplicationClass
import com.sluv.sluv.config.ApplicationClass.Companion.FCM_TOKEN
import com.sluv.sluv.config.BaseActivity
import com.sluv.sluv.databinding.ActivitySplashBinding
import com.sluv.sluv.src.login.LoginActivity
import com.sluv.sluv.src.main.MainActivity
import com.sluv.sluv.src.splash.models.AutoLoginResponse
import com.sluv.sluv.src.splash.network.SplashService

interface SplashActivityView {
    fun onPostAutoLoginSuccess(response: AutoLoginResponse)
    fun onPostAutoLoginFailure(message: String)
}

class SplashActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate), SplashActivityView{

    val TAG = "SplashActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 자동로그인 API 호출
        SplashService(this).postAutoLogin()

        Log.d(TAG, "SplashActivity - onCreate() : ${ApplicationClass.prefs.getString(FCM_TOKEN, "")}")

        // 여기부터 FCM 관련 작업 필요
    }

    override fun onPostAutoLoginSuccess(response: AutoLoginResponse) {
        if (response.isSuccess && response.code == 1000) {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        } else {
            Log.d(
                TAG,
                "SplashActivity - onPostAutoLoginSuccess() : 코드 ${response.code} : ${response.message}"
            )
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    override fun onPostAutoLoginFailure(message: String) {
        if (message == "response is null") {
            Log.d(
                TAG,
                "SplashActivity - onPostAutoLoginFailure() : ${message}"
            )
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        } else {
            Log.d(TAG, "SplashActivity - onPostAutoLoginFailure() : $message")
            showCustomToast(getString(R.string.networkError))
        }
    }
}