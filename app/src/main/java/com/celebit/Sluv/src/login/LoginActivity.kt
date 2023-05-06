package com.celebit.Sluv.src.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.celebit.Sluv.BuildConfig
import com.celebit.Sluv.config.ApplicationClass.Companion.ACCESS_TOKEN
import com.celebit.Sluv.config.ApplicationClass.Companion.GOOGLE_SNS_TYPE
import com.celebit.Sluv.config.ApplicationClass.Companion.KAKAO_SNS_TYPE
import com.celebit.Sluv.config.ApplicationClass.Companion.prefs
import com.celebit.Sluv.config.BaseActivity
import com.celebit.Sluv.databinding.ActivityLoginBinding
import com.celebit.Sluv.src.login.models.LoginRequest
import com.celebit.Sluv.src.login.models.LoginResponse
import com.celebit.Sluv.src.login.network.LoginService
import com.celebit.Sluv.src.main.MainActivity

interface LoginActivityView {
    fun onPostLoginSuccess(response: LoginResponse)
    fun onPostLoginFailure(message: String)
}

class LoginActivity : BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate), LoginActivityView {

    val TAG = "LoginActivity"

    //google client
    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configure 구글 로그인
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.GOOGLE_LOGIN_CLIENT_ID)
            .requestEmail()
            .requestProfile()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // 구글 로그인 버튼 클릭
        binding.googleLoginBtn.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            resultLauncher.launch(signInIntent)
        }

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                val idToken = task.getResult(ApiException::class.java).idToken
                // 구글 로그인 API 호출
                LoginService(this).tryPostLogin(LoginRequest(idToken!!, GOOGLE_SNS_TYPE))
            } else {
                Log.d(TAG, "구글 로그인 resultCode Error")
                Log.d(TAG, result.resultCode.toString())
            }
        }

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
                Log.d(TAG, "카카오톡 설치 안되어있음")
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }
        }
    }


    override fun onPostLoginSuccess(response: LoginResponse) {
        if(response.isSuccess) {
            // jwt  키 값 저장
            prefs.setString(ACCESS_TOKEN, response.result.token)
            // 메인 액티비티로 이동
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            // isSuccess == false
            showCustomToast(response.message.toString())
            Log.d(
                TAG,
                "LoginActivity - onPostLoginSuccess() : code : ${response.code}, message : ${response.message}"
            )
        }
    }

    override fun onPostLoginFailure(message: String) {
        Log.d(TAG, "LoginActivity - onPostSignInFailure() : $message")
        showCustomToast(message)
    }
}