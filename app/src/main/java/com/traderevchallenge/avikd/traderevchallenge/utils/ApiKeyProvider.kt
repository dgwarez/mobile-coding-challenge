package com.traderevchallenge.avikd.traderevchallenge.utils

import android.util.Log
import com.traderevchallenge.avikd.traderevchallenge.MyApplication.Companion.context
import java.util.*

class ApiKeyProvider {
    companion object {
        fun fetchApiKey(): String {
            val appProps = Properties()
            try {
                appProps.load(context.assets.open("app.properties"))
            } catch (e: Exception) {
                Log.e("Properties file Error", e.stackTrace.toString())
                return ""
            }
            return appProps.getProperty("API_KEY")
        }
    }
}
