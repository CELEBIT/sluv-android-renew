package com.celebit.Sluv.src.main

import android.app.Activity
import android.webkit.WebChromeClient

class MyWebChromeClient(private val act: Activity, private val link: MainActivity.RoomToWebview) : WebChromeClient() {
}