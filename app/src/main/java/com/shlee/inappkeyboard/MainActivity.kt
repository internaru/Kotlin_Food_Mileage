package com.shlee.inappkeyboard

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editText = findViewById<View>(R.id.editText) as EditText
        val keyboard = findViewById<View>(R.id.keyboard) as MyKeyboard

        // prevent system keyboard from appearing when EditText is tapped
        editText.setRawInputType(InputType.TYPE_CLASS_NUMBER)
        editText.setTextIsSelectable(false)

        // Network Check
        checkInternet(this) { isConnected ->
            if (isConnected) {
                showCustomToast(this, "Network is available", 26f, "GREEN") // 연결됨
            } else {
                showCustomToast(this, "Network is not available", 26f, "RED")   // 연결 안됨
            }
        }

        // pass the InputConnection from the EditText to the keyboard
        val ic = editText.onCreateInputConnection(EditorInfo())
        keyboard.setInputConnection(ic)
    }
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if(!hasFocus) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.hide(WindowInsets.Type.systemBars() or WindowInsets.Type.navigationBars())
            window.insetsController?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        else {
            window.decorView.systemUiVisibility =
                (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
        }
    }

    // 네트워크 체크 함수
    fun checkInternet(context: Context, onResult: (Boolean) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = try {
                val url = URL("https://clients3.google.com/generate_204")
                val connection = url.openConnection() as HttpURLConnection
                connection.setRequestProperty("User-Agent", "Android")
                connection.setRequestProperty("Connection", "close")
                connection.connectTimeout = 1500
                connection.connect()
                connection.responseCode == 204
            } catch (e: Exception) {
                false
            }

            // 메인(UI) 스레드로 결과 전달
            withContext(Dispatchers.Main) {
                onResult(result)
            }
        }
    }
}