package com.traderevchallenge.avikd.traderevchallenge.utils

import android.content.Context
import android.util.Log
import java.util.*

class ApiKeyProvider {
    companion object {
        private var apiKey: String = ""
        fun fetchApiKey(context: Context?): String {
            if (apiKey == "") {
                val appProps = Properties()
                try {
                    appProps.load(context?.assets?.open("app.properties"))
                    apiKey = appProps.getProperty("API_KEY")
                } catch (e: Exception) {
                    Log.e("Properties file Error", e.stackTrace.toString())
                    return ""
                }
            }
            return apiKey
        }
    }
}
