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
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.LocalDateTime

class UserDataActivity : AppCompatActivity() {
    private lateinit var radioGroup: RadioGroup
    private lateinit var button_ok: Button
    private lateinit var cancelButton: Button
    private lateinit var textView_GradeNum: TextView
    private lateinit var textView_PhoneNum: TextView

    val myeRf = Firebase.database.reference
    val dateAndtime: LocalDateTime = LocalDateTime.now()
    var onlyDate: LocalDate = LocalDate.now()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_data)

        // Phone Number
        val strNumber = intent.getStringExtra("number")

        // Grade
        var nGrade = 0
        radioGroup = findViewById(R.id.radioGroup_Grade)

        var id = "00000"

        // Button, TextView
        textView_PhoneNum = findViewById<TextView>(R.id.textView_PhoneNum)
        textView_GradeNum = findViewById<TextView>(R.id.textView_GradeNum)
        button_ok = findViewById(R.id.button_ok)
        cancelButton = findViewById(R.id.button_cancel)

        // Phone Number Display
        textView_PhoneNum.text = strNumber

        // Grad Display
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedRadioButton = findViewById<RadioButton>(checkedId)
            val selectedOption = selectedRadioButton.text.toString()
            textView_GradeNum.text = selectedOption
            nGrade = selectedOption.toInt()
        }

        // OK Button
        button_ok.setOnClickListener {
            val selectedId = radioGroup.checkedRadioButtonId
            if (selectedId != -1) {

                //all_button_enable(false)

                val userData = UserData("NoData","1")
                onlyDate = LocalDate.now()
                id = strNumber + '_'+ nGrade.toString()

                myeRf.child(this.onlyDate.toString()).child(id).setValue(userData).addOnSuccessListener {
                    Toast.makeText(this, "New user save OK", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener{
                    Toast.makeText(this, "New user save Fail !", Toast.LENGTH_SHORT).show()
                }
                finish()
            }
        }

        // Cancel Button
        cancelButton.setOnClickListener {
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}