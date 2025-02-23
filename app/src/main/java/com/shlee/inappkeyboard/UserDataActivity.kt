package com.shlee.inappkeyboard

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.security.AccessController.getContext

class UserDataActivity : AppCompatActivity() {
    private lateinit var radioGroup: RadioGroup
    private lateinit var okButton: Button
    private lateinit var cancelButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_data)

        // Phone Number
        val number = intent.getStringExtra("number")

        // Grade
        radioGroup = findViewById(R.id.radioGroup_Grade)

        // Button, TextView
        var phone = findViewById<TextView>(R.id.textView_PhoneNum)
        var grade = findViewById<TextView>(R.id.textView_GradeNum)
        okButton = findViewById(R.id.button_ok)
        cancelButton = findViewById(R.id.button_cancel)

        // Phone Number Display
        phone.text = number

        // Grad Display
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedRadioButton = findViewById<RadioButton>(checkedId)
            val selectedOption = selectedRadioButton.text.toString()
            grade.text = selectedOption
        }

        // OK Button
        okButton.setOnClickListener {
            val selectedId = radioGroup.checkedRadioButtonId
            if (selectedId != -1) {
                val selectedRadioButton: RadioButton = findViewById(selectedId)
                val selectedOption = selectedRadioButton.text.toString()

                val resultIntent = Intent().apply {
                    putExtra("selected_option", selectedOption)
                }
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }

        // Cancel Button
        cancelButton.setOnClickListener {
            val resultIntent = Intent().apply {
                putExtra("selected_option", "0")
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}