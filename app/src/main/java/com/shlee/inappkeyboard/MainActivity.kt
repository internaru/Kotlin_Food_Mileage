package com.shlee.inappkeyboard

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editText = findViewById<View>(R.id.editText) as EditText
        val keyboard = findViewById<View>(R.id.keyboard) as MyKeyboard

        // prevent system keyboard from appearing when EditText is tapped
        editText.setRawInputType(InputType.TYPE_CLASS_TEXT)
        editText.setTextIsSelectable(true)

        // pass the InputConnection from the EditText to the keyboard
        val ic = editText.onCreateInputConnection(EditorInfo())
        keyboard.setInputConnection(ic)
    }
}