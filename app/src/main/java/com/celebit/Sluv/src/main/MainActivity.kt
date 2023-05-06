package com.celebit.Sluv.src.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.celebit.Sluv.BuildConfig
import com.celebit.Sluv.config.ApplicationClass
import com.celebit.Sluv.config.ApplicationClass.Companion.prefs
import com.celebit.Sluv.config.BaseActivity
import com.celebit.Sluv.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    val TAG = "MainActivity"

    private val RC_PERMISSION = 3

    private val accessToken = prefs.getString(ApplicationClass.ACCESS_TOKEN, "ERROR")
    private val versionNumber = BuildConfig.VERSION_NAME
    private val firstAccess = prefs.getString("first_access","true")

    private val BASE_URL = "https://develop.d1ff59r8egbr9x.amplifyapp.com"
    private var fullUrl = ""

    private lateinit var neededPermissionList: ArrayList<String>  //권한 요청이 필요한 리스트
    private val requiredPermissionList = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
        arrayOf(  // 안드로이드 13 이상 필요한 권한들
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.POST_NOTIFICATIONS
        )
    } else {
        arrayOf(  // 안드로이드 13 미만 필요한 권한들
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    inner class RoomToWebview {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 안드로이드 앱에서 WebView의 디버깅 기능을 활성화
        WebView.setWebContentsDebuggingEnabled(true)

        binding.webView.apply {
            // WebView에서 자바스크립트를 사용할 수 있도록 설정
            settings.javaScriptEnabled = true
            // WebView에서 DOM Storage를 사용할 수 있도록 설정
            settings.domStorageEnabled = true
            // WebView에서 화면의 크기를 뷰포트(Viewport)의 크기에 맞게 조정할 수 있도록 설정
            settings.useWideViewPort = true
            // WebView에서 화면을 확대/축소하는 컨트롤을 표시하지 않도록 설정
            settings.displayZoomControls = false
            // WebView에서 여러 개의 창을 지원하도록 설정
            settings.setSupportMultipleWindows(true)
            // WebView에서 자바스크립트로 새 창을 열 수 있도록 설정
            settings.javaScriptCanOpenWindowsAutomatically = true
            // WebView에서 콘텐츠에 접근할 수 있도록 설정
            settings.allowContentAccess = true
            // WebView에서 파일 시스템에 접근할 수 있도록 설정
            settings.allowFileAccess = true
            // WebView에서 자바스크립트 코드에서 안드로이드 앱의 메서드를 호출할 수 있는 인터페이스를 추가
            addJavascriptInterface(WebAppInterface(this@MainActivity, RoomToWebview()), "Android")

            Log.d(TAG,"webview 생성")
        }
        setWebViewClient()

        // 웹뷰 Load
        CoroutineScope(Dispatchers.Main).launch {
            // val fcm = async { MyFirebaseMessagingService().getFirebaseToken() }.await()
            callWebView()
        }

        if (firstAccess == "true") onCheckPermission()

    }

    private fun setWebViewClient() {
        binding.webView.webChromeClient = MyWebChromeClient(this,RoomToWebview())
        binding.webView.webViewClient = MyWebViewClient(this, RoomToWebview())
    }

    private fun callWebView() {

//        val queryAccess = "&AccessToken=$accessToken"
//        val queryVersion  = "&VersionNumber=$versionNumber"
//        // val queryFCM = "&fcmToken=$fcmToken"
//        val queryDevice = "&device=ANDROID"
//
//        fullUrl = BASE_URL + queryAccess + queryVersion + queryDevice
//        Log.d(TAG, fullUrl)
        fullUrl = BASE_URL
        binding.webView.loadUrl(fullUrl)
    }

    private fun onCheckPermission() {
        neededPermissionList = ArrayList<String>()  //초기화

        for (permission in requiredPermissionList) {

            // requiredPermissionList 중 허용되지 않은 권한 체크해서 neededPermissionList 에 추가
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                neededPermissionList.add(permission)
            }
        }
        if (neededPermissionList.isNotEmpty()) {

            // neededPermissionList 가 존재하는 경우, 해당 권한에 대한 권한허용 팝업 표시
            val neededPermissionArr = neededPermissionList.toArray(arrayOf<String>())
            ActivityCompat.requestPermissions(this, neededPermissionArr, RC_PERMISSION)
        }

        prefs.setString("first_access", "false")
    }
}