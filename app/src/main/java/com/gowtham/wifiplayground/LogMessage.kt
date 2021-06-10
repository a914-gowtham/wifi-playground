package com.gowtham.wifiplayground

import android.util.Log

object LogMessage {

    private val logVisible = true

    internal fun v(msg: String) {
        if (logVisible)
            Log.v("Countries-App", msg)
    }

    internal fun e(msg: String) {
        if (logVisible)
            Log.e("Countries-App", msg)
    }

}