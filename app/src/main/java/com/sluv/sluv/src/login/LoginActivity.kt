package com.sluv.sluv.src.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.sluv.sluv.config.ApplicationClass
import com.sluv.sluv.config.ApplicationClass.Companion.JWT_TOKEN
import com.sluv.sluv.config.ApplicationClass.Companion.KAKAO_SNS_TYPE
import com.sluv.sluv.config.ApplicationClass.Companion.prefs
import com.sluv.sluv.config.BaseActivity
import com.sluv.sluv.databinding.ActivityLoginBinding
import com.sluv.sluv.src.login.models.LoginRequest
import com.sluv.sluv.src.login.models.LoginResponse
import com.sluv.sluv.src.login.network.LoginService
import com.sluv.sluv.src.main.MainActivity

interface LoginActivityView {
    fun onPostLoginSuccess(response: LoginResponse)
    fun onPostLoginFailure(message: String)
}

class LoginActivity : BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate), LoginActivityView {

    val TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 카카오 로그인 공통 callback 구성
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e(TAG, "카카오계정으로 로그인 실패", error)
            } else if (token != null) {
                Log.i(TAG, "카카오계정으로 로그인 성공 ${token.accessToken}")

                // 카카오 로그인 API 호출
                LoginService(this).tryPostLogin(LoginRequest(token.accessToken, KAKAO_SNS_TYPE))
            }
        }

        // 카카오 로그인 버튼 클릭
        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        binding.btnKakaoSignIn.setOnClickListener {
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                    if (error != null) {
                        Log.e(TAG, "카카오톡으로 로그인 실패", error)

                        // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                        // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            return@loginWithKakaoTalk
                        }

                        // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                        UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
                    } else if (token != null) {
                        Log.i(TAG, "카카오톡으로 로그인 성공 ${token.accessToken}")

                        // 카카오 로그인 API 호출
                        LoginService(this).tryPostLogin(LoginRequest(accessToken = token.accessToken, snsType = KAKAO_SNS_TYPE))
                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }
        }
    }

    override fun onPostLoginSuccess(response: LoginResponse) {
        if(response.isSuccess) {
            // jwt  키 값 저장
            prefs.setString(JWT_TOKEN, response.result.token)
            // 메인 액티비티로 이동
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            // isSuccess == false
            Log.d(
                TAG,
                "LoginActivity - onPostLoginSuccess() : code : ${response.code}, message : ${response.message}"
            )
        }
    }

    override fun onPostLoginFailure(message: String) {
        Log.d(TAG, "LoginActivity - onPostSignInFailure() : $message")
    }
}