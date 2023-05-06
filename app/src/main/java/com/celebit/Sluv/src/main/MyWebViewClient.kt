package com.celebit.Sluv.src.main

import android.app.Activity
import android.webkit.WebViewClient

class MyWebViewClient(private val act: Activity, private val link: MainActivity.RoomToWebview):
    WebViewClient() {
}