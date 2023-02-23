package com.sluv.sluv.src.login

import android.os.Bundle
import android.util.Log
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.sluv.sluv.config.ApplicationClass.Companion.KAKAO_LOGIN
import com.sluv.sluv.config.ApplicationClass.Companion.LOGIN_TYPE
import com.sluv.sluv.config.BaseActivity
import com.sluv.sluv.databinding.ActivityLoginBinding
import com.sluv.sluv.util.PreferenceUtil

class LoginActivity : BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate) {

    val TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 카카오 로그인 공통 callback 구성
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e(TAG, "카카오 로그인 실패", error)
            } else if (token != null) {
                Log.i(TAG, "카카오 로그인 성공 ${token.accessToken}")
                PreferenceUtil(this).setString(LOGIN_TYPE, KAKAO_LOGIN)

                // 소셜 로그인 API 호출

                UserApiClient.instance.scopes { scopeInfo, error->
                    if (error != null) {
                        Log.e(TAG, "동의 정보 확인 실패", error)
                    }else if (scopeInfo != null) {
                        Log.i(TAG, "동의 정보 확인 성공\n 현재 가지고 있는 동의 항목 $scopeInfo")
                    }
                }
            }

        }

        // 카카오 로그인 버튼 클릭
        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        binding.btnKakaoSignIn.setOnClickListener {
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }
        }
    }
}