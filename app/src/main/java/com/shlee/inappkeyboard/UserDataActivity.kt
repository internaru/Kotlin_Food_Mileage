package com.shlee.inappkeyboard

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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import android.content.Context
import android.view.LayoutInflater
import java.time.ZoneId

fun showCustomToast(
    context: Context,
    message: String,
    textSizeSp: Float = 24f,
    color: String = "GREEN"
) {
    val inflater = LayoutInflater.from(context)
    val layout = if(color == "GREEN")
        inflater.inflate(R.layout.custom_toast_green, null)
    else
        inflater.inflate(R.layout.custom_toast_red, null)

    val textView = layout.findViewById<TextView>(R.id.toast_text)
    textView.text = message
    textView.textSize = textSizeSp

    Toast(context).apply {
        duration = Toast.LENGTH_SHORT
        view = layout
        show()
    }
}

class UserDataActivity : AppCompatActivity() {
    private lateinit var radioGroup: RadioGroup
    private lateinit var button_ok: Button
    private lateinit var cancelButton: Button
    private lateinit var textView_GradeNum: TextView
    private lateinit var textView_PhoneNum: TextView

    private val myeRf = Firebase.database.reference
    private val database = Firebase.database
    val koreaZone = ZoneId.of("Asia/Seoul")
    private var today: LocalDate = LocalDate.now(koreaZone)

    private fun getUserDataInRank(userId: String, callback: (String?, String?) -> Unit) {
        val userRef = database.getReference("Rank/$userId")

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userMileage = if (snapshot.hasChild("mileage")) {
                        snapshot.child("mileage").getValue(String::class.java) ?: "0"
                    } else {
                        "NotExist"
                    }
                    val userUpdate = if (snapshot.hasChild("update")) {
                        snapshot.child("update").getValue(String::class.java) ?: "0000-00-00"
                    } else {
                        "NotExist"
                    }
                    callback(userMileage, userUpdate)
                }
                else {
                    callback(null, null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null, null)
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_data)

        // Phone Number
        val strNumber = intent.getStringExtra("number")

        // Grade
        var nGrade = 0
        radioGroup = findViewById(R.id.radioGroup_Grade)

        var id = "000000"   // 6 digit (phone(4) + '_' + grade(1))

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

                today = LocalDate.now()
//                today = LocalDate.now().minusDays(1)
                val userData = UserData(today.toString(),"1")
                id = strNumber + '_'+ nGrade.toString()

                myeRf.child(this.today.toString()).child(id).setValue(userData).addOnSuccessListener {
                //myeRf.child("Rank").child(id).setValue(userData).addOnSuccessListener {

                    Toast.makeText(this, "Daily Record : Success", Toast.LENGTH_SHORT).show()

                    getUserDataInRank(id) { miles, date ->
                        // Exist already
                        if (miles != null && date != null) {
                            // Update
                            if (date != today.toString()) {
                            //if ("0000-00-00" != today.toString()) {
                                val userDataNew = UserData(today.toString(), (miles.toInt()+1).toString())
                                myeRf.child("Rank").child(id).setValue(userDataNew).addOnSuccessListener {
                                    showCustomToast(this, "Total Mileages : ${miles.toInt()+1}", 26f, "GREEN")
                                }.addOnFailureListener{
                                    showCustomToast(this, "Total Mileage : Fail", 26f, "RED")
                                }
                            }
                            // Ignore
                            else{
                                showCustomToast(this, "1 Point a day",26f, "RED")
                            }
                        }
                        // Not Exist
                        else {
                            // Create new user
                            myeRf.child("Rank").child(id).setValue(userData).addOnSuccessListener {
                                showCustomToast(this, "New user Mileages : 1", 26f, "GREEN")
                            }.addOnFailureListener{
                                showCustomToast(this, "New user Registration : Fail",26f, "RED")
                            }
                        }
                    }
                }.addOnFailureListener{
                    showCustomToast(this, "Daily Record : Fail",26f, "RED")
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