package com.celebit.Sluv.src.main

import android.app.Activity
import android.util.Log
import android.webkit.JavascriptInterface
import android.widget.Toast

class WebAppInterface(private val act: Activity, private val link: MainActivity.RoomToWebview) {

    private val TAG = "debugging"

    // 토스트메세지 웹->앱 패키징
    @JavascriptInterface
    fun showToast(test: String) {
        Log.d(TAG, "웹->앱 : 토스트 메세지 띄우기")
        Toast.makeText(act, test, Toast.LENGTH_SHORT)
            .show()
    }
}